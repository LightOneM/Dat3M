package com.dat3m.dartagnan.analysis;

import com.dat3m.dartagnan.analysis.graphRefinement.GraphRefinement;
import com.dat3m.dartagnan.analysis.graphRefinement.RefinementResult;
import com.dat3m.dartagnan.analysis.graphRefinement.RefinementStats;
import com.dat3m.dartagnan.analysis.graphRefinement.coreReason.AbstractEdgeLiteral;
import com.dat3m.dartagnan.analysis.graphRefinement.coreReason.AddressLiteral;
import com.dat3m.dartagnan.analysis.graphRefinement.coreReason.CoreLiteral;
import com.dat3m.dartagnan.analysis.graphRefinement.coreReason.EventLiteral;
import com.dat3m.dartagnan.analysis.graphRefinement.logic.Conjunction;
import com.dat3m.dartagnan.analysis.graphRefinement.logic.DNF;
import com.dat3m.dartagnan.asserts.AssertTrue;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.Thread;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.event.MemEvent;
import com.dat3m.dartagnan.program.utils.EType;
import com.dat3m.dartagnan.utils.Result;
import com.dat3m.dartagnan.utils.equivalence.BranchEquivalence;
import com.dat3m.dartagnan.utils.equivalence.EquivalenceClass;
import com.dat3m.dartagnan.utils.symmetry.ThreadSymmetry;
import com.dat3m.dartagnan.verification.VerificationTask;
import com.dat3m.dartagnan.wmm.Wmm;
import com.dat3m.dartagnan.wmm.axiom.Acyclic;
import com.dat3m.dartagnan.wmm.relation.Relation;
import com.dat3m.dartagnan.wmm.relation.binary.RelUnion;
import com.dat3m.dartagnan.wmm.utils.RelationRepository;
import com.dat3m.dartagnan.wmm.utils.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sosy_lab.java_smt.api.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.dat3m.dartagnan.GlobalSettings.*;
import static com.dat3m.dartagnan.program.utils.Utils.generalEqual;
import static com.dat3m.dartagnan.utils.Result.*;

public class Refinement {

    private static final Logger logger = LogManager.getLogger(Refinement.class);

    //TODO: Currently, we pop the complete refinement before performing the bound check
    // This may lead to situations where a bound is only reachable because
    // we don't have a memory model and thus the bound check is imprecise.
    // We may even want to perform refinement to check the bounds (we envision a case where the
    // refinement is accurate enough to verify the assertions but not accurate enough to check the bounds)
    public static Result runAnalysisGraphRefinement(SolverContext ctx, ProverEnvironment prover, VerificationTask task)
            throws InterruptedException, SolverException {
        task.unrollAndCompile();
        if(task.getProgram().getAss() instanceof AssertTrue) {
            logger.info("Verification finished: assertion trivially holds");
            return PASS;
        }

        task.initialiseEncoding(ctx);
        prover.addConstraint(task.encodeProgram(ctx));

        if (REF_BASELINE_WMM) {
            Wmm baseline = createBaselineWmm(task);
            baseline.initialise(task, ctx); // this is a little suspicious
            prover.addConstraint(baseline.encode(ctx));
            prover.addConstraint(baseline.consistent(ctx));
        } else {
            prover.addConstraint(task.encodeWmmCore(ctx));
        }

        return refinementCore(ctx, prover, task);
    }

    private static Result refinementCore(SolverContext ctx, ProverEnvironment prover, VerificationTask task)
            throws InterruptedException, SolverException {

        // ======= Some preprocessing to use a visible representative for each branch ========
        for (BranchEquivalence.Class c : task.getBranchEquivalence().getAllEquivalenceClasses()) {
            c.stream().sorted().filter(e -> e.is(EType.VISIBLE)).findFirst().ifPresent(c::setRepresentative);
        }
        // =====================================================================================

        BooleanFormulaManager bmgr = ctx.getFormulaManager().getBooleanFormulaManager();
        Program program = task.getProgram();
        GraphRefinement refinement = new GraphRefinement(task);
        Result res = UNKNOWN;

        // ====== Test code ======
        List<Function<Event, Event>> perms = computePerms(task); // Used for symmetry learning
        // ----------
        if (ENABLE_SYMMETRY_BREAKING) {
            prover.addConstraint(task.encodeSymmetryBreaking(ctx));
        }
        // =======================

        prover.push();
        prover.addConstraint(task.encodeAssertions(ctx));

        // Just for statistics
        List<DNF<CoreLiteral>> foundViolations = new ArrayList<>();
        List<RefinementStats> statList = new ArrayList<>();
        int vioCount = 0;
        long lastTime = System.currentTimeMillis();
        long curTime;
        long totalSolvingTime = 0;

        while (!prover.isUnsat()) {
            curTime = System.currentTimeMillis();
            totalSolvingTime += (curTime - lastTime);

            if (REF_PRINT_STATISTICS) {
                System.out.println(" ===== Iteration: " + ++vioCount + " =====");
                System.out.println("Solving time( ms): " + (curTime - lastTime));
            }

            RefinementResult gRes;
            try (Model model = prover.getModel()) {
                gRes = refinement.kSearch(model, ctx, 2);
            }
            RefinementStats stats = gRes.getStatistics();
            statList.add(stats);
            if (REF_PRINT_STATISTICS) {
                System.out.println(stats);
            }

            res = gRes.getResult();
            if (res == FAIL) {
                DNF<CoreLiteral> violations = gRes.getViolations();
                foundViolations.add(violations);
                refine(prover, ctx, violations, perms);
                // Some statistics
                if (REF_PRINT_STATISTICS) {
                    for (Conjunction<CoreLiteral> cube : violations.getCubes()) {
                        System.out.println("Violation size: " + cube.getSize());
                        System.out.println(cube);
                    }
                }
            } else {
                // No violations found, we can't refine
                break;
            }
            lastTime = System.currentTimeMillis();
        }
        curTime = System.currentTimeMillis();
        totalSolvingTime += (curTime - lastTime);
        if (REF_PRINT_STATISTICS) {
            System.out.println(" ===== Final Iteration: " + (vioCount + 1) + " =====");
            System.out.println("Solving/Proof time(ms): " + (curTime - lastTime));
        }

        // Possible outcomes: - check() == SAT && res == UNKNOWN -> Inconclusive
        //                    - check() == SAT && res == PASS -> Unsafe
        //                    - check() == UNSAT -> Safe


        boolean isSat = !prover.isUnsat();
        if (REF_PRINT_STATISTICS) {
            if (isSat && res == UNKNOWN) {
                // We couldn't verify the found counterexample, nor exclude it.
                System.out.println("Procedure was inconclusive.");
                return UNKNOWN;
            } else if (isSat) {
                // We found a true violation
                System.out.println("Violation verified.");
            } else {
                // We showed safety but still need to verify bounds
                System.out.println("Bounded safety proven.");
            }
        } else {
            if (isSat && res == UNKNOWN) {
                return UNKNOWN;
            }
        }

        long boundCheckTime = 0;
        if (prover.isUnsat()) {
            // ------- CHECK BOUNDS -------
            lastTime = System.currentTimeMillis();
            prover.pop();
            prover.addConstraint(bmgr.not(program.encodeNoBoundEventExec(ctx)));
            res = !prover.isUnsat() ? UNKNOWN : PASS; // Initial bound check without any WMM constraints
            if (res == UNKNOWN) {
                //TODO: This is just a temporary fallback
                // We probably have to perform a second refinement for the bound checks!
                for (DNF<CoreLiteral> violation : foundViolations) {
                    refine(prover, ctx, violation, perms);
                }
                res = !prover.isUnsat() ? UNKNOWN : PASS;
            }
            boundCheckTime = System.currentTimeMillis() - lastTime;
        } else {
            res = FAIL;
        }
        if (REF_PRINT_STATISTICS) {
            printSummary(statList, totalSolvingTime, boundCheckTime);
        }

        res = program.getAss().getInvert() ? res.invert() : res;
        logger.info("Verification finished with result " + res);
        return res;
    }


    // This method adds new constraints to the prover based on the found violations.
    // Furthermore, it computes symmetric violations if symmetry learning is enabled.
    private static void refine(ProverEnvironment prover, SolverContext ctx, DNF<CoreLiteral> coreViolations,
                               List<Function<Event, Event>> perms) throws InterruptedException {
        BooleanFormulaManager bmgr = ctx.getFormulaManager().getBooleanFormulaManager();
        for (Function<Event, Event> p : perms) {
            BooleanFormula refinement = bmgr.makeTrue();
            for (Conjunction<CoreLiteral> violation : coreViolations.getCubes()) {
                BooleanFormula clause = bmgr.makeFalse();
                for (CoreLiteral literal : violation.getLiterals()) {
                    clause = bmgr.or(clause, bmgr.not(permuteAndConvert(literal, p, ctx)));
                }
                refinement = bmgr.and(refinement, clause);
            }
            prover.addConstraint(refinement);
        }
    }


    // ======================= Helper Methods ======================

    // -------------------- Printing -----------------------------

    private static void printSummary(List<RefinementStats> statList, long totalSolvingTime, long boundCheckTime) {
        long totalModelTime = 0;
        long totalSearchTime = 0;
        long totalViolationComputationTime = 0;
        long totalResolutionTime = 0;
        long totalNumGuesses = 0;
        long totalNumViolations = 0;
        long totalModelSize = 0;
        long minModelSize = Long.MAX_VALUE;
        long maxModelSize = Long.MIN_VALUE;
        int satDepth = 0;

        for (RefinementStats stats : statList) {
            totalModelTime += stats.getModelConstructionTime();
            totalSearchTime += stats.getSearchTime();
            totalViolationComputationTime += stats.getViolationComputationTime();
            totalResolutionTime += stats.getResolutionTime();
            totalNumGuesses += stats.getNumGuessedCoherences();
            totalNumViolations += stats.getNumComputedViolations();
            satDepth = Math.max(satDepth, stats.getSaturationDepth());

            totalModelSize += stats.getModelSize();
            minModelSize = Math.min(stats.getModelSize(), minModelSize);
            maxModelSize = Math.max(stats.getModelSize(), maxModelSize);
        }

        System.out.println(" ======= Summary ========");
        System.out.println("Total solving time( ms): " + totalSolvingTime);
        System.out.println("Total model construction time( ms): " + totalModelTime);
        if (statList.size() > 0) {
            System.out.println("Min model size (#events): " + minModelSize);
            System.out.println("Average model size (#events): " + totalModelSize / statList.size());
            System.out.println("Max model size (#events): " + maxModelSize);
        }
        System.out.println("Total violation computation time( ms): " + totalViolationComputationTime);
        System.out.println("Total resolution time( ms): " + totalResolutionTime);
        System.out.println("Total search time( ms): " + totalSearchTime);
        System.out.println("Total guessing: " + totalNumGuesses);
        System.out.println("Total violations: " + totalNumViolations);
        System.out.println("Max Saturation Depth: " + satDepth);
        System.out.println("Bound check time( ms): " + boundCheckTime);
    }

    // ---------------------------- Outer WMM -----------------------------

    private static Wmm createBaselineWmm(VerificationTask task) {
        Wmm original = task.getMemoryModel();
        Wmm baseline = new Wmm();
        baseline.setEncodeCo(false);
        RelationRepository origRepo = original.getRelationRepository();
        RelationRepository repo = baseline.getRelationRepository();
        // We copy relations from the original WMM to avoid recomputations of max-/minSets
        // This causes active set computations to be reflected in the original WMM (which shouldn't be problematic)
        repo.addRelation(origRepo.getRelation("rf"));
        repo.addRelation(origRepo.getRelation("po"));
        repo.addRelation(origRepo.getRelation("co"));
        repo.addRelation(origRepo.getRelation("idd"));
        repo.addRelation(origRepo.getRelation("addrDirect"));
        if (origRepo.containsRelation("loc")) {
            repo.addRelation(origRepo.getRelation("loc"));
        }
        if (origRepo.containsRelation("po-loc")) {
            repo.addRelation(origRepo.getRelation("po-loc"));
        }

        // ---- acyclic(po-loc | rf) ----
        Relation poloc = repo.getRelation("po-loc");
        Relation rf = repo.getRelation("rf");
        Relation porf = new RelUnion(poloc, rf);
        repo.addRelation(porf);
        baseline.addAxiom(new Acyclic(porf));

        // ---- acyclic (dep | rf) ----
        if (REF_ADD_ACYCLIC_DEP_RF) {
            if (origRepo.containsRelation("data")) {
                repo.addRelation(origRepo.getRelation("data"));
            }
            if (origRepo.containsRelation("ctrl")) {
                repo.addRelation(origRepo.getRelation("ctrl"));
            }
            if (origRepo.containsRelation("addr")) {
                repo.addRelation(origRepo.getRelation("addr"));
            }
            Relation data = repo.getRelation("data");
            Relation ctrl = repo.getRelation("ctrl");
            Relation addr = repo.getRelation("addr");
            Relation dep = new RelUnion(data, addr);
            repo.addRelation(dep);
            dep = new RelUnion(ctrl, dep);
            repo.addRelation(dep);
            Relation hb = new RelUnion(dep, rf);
            repo.addRelation(hb);
            baseline.addAxiom(new Acyclic(hb));
        }

        return baseline;
    }


    // ---------------------- Symmetry computations -----------------------

    // Computes a list of all permutations allowed by the program
    private static List<Function<Event, Event>> computePerms(VerificationTask task) {

        ThreadSymmetry symm = task.getThreadSymmetry();
        Set<? extends EquivalenceClass<Thread>> symmClasses = symm.getNonTrivialClasses();
        List<Function<Event, Event>> perms = new ArrayList<>();
        if (symmClasses.isEmpty() || REF_SYMMETRY_LEARNING == SymmetryLearning.NONE) {
            perms.add(Function.identity());
            return perms;
        }

        for (EquivalenceClass<Thread> c : symmClasses) {
            List<Thread> threads = new ArrayList<>(c);
            threads.sort(Comparator.comparingInt(Thread::getId));
            if (REF_SYMMETRY_LEARNING == SymmetryLearning.LINEAR) {
                // ==== Linear ====
                perms.add(Function.identity());
                for (int i = 0; i < threads.size(); i++) {
                    int j = (i + 1) % threads.size();
                    perms.add(symm.createTransposition(threads.get(i), threads.get(j)));
                }
            } else if (REF_SYMMETRY_LEARNING == SymmetryLearning.QUADRATIC) {
                // ==== Quadratic ====
                perms.add(Function.identity());
                for (int i = 0; i < threads.size(); i++) {
                    for (int j = i + 1; j < threads.size(); j++) {
                        perms.add(symm.createTransposition(threads.get(i), threads.get(j)));
                    }
                }
            } else if (REF_SYMMETRY_LEARNING == SymmetryLearning.FULL) {
                // ==== Full ====
                perms.addAll(symm.createAllPermutations(c));
            }
        }

        return perms;
    }

    // Changes a reasoning <literal> based on a given permutation <p> and translates the result into a BooleanFormula
    // for Refinement
    private static BooleanFormula permuteAndConvert(CoreLiteral literal, Function<Event, Event> p, SolverContext ctx) {
        if (literal instanceof EventLiteral) {
            EventLiteral lit = (EventLiteral) literal;
            return p.apply(lit.getEvent().getEvent()).exec();
        } else if (literal instanceof AddressLiteral) {
            AddressLiteral loc = (AddressLiteral) literal;
            MemEvent e1 = (MemEvent) p.apply(loc.getEdge().getFirst().getEvent());
            MemEvent e2 = (MemEvent) p.apply(loc.getEdge().getSecond().getEvent());
            return generalEqual(e1.getMemAddressExpr(), e2.getMemAddressExpr(), ctx);
        } else if (literal instanceof AbstractEdgeLiteral) {
            AbstractEdgeLiteral lit = (AbstractEdgeLiteral) literal;
            return Utils.edge(lit.getName(),
                    p.apply(lit.getEdge().getFirst().getEvent()),
                    p.apply(lit.getEdge().getSecond().getEvent()), ctx);
        }
        throw new IllegalArgumentException("CoreLiteral " + literal.toString() + " is not supported");
    }

}
