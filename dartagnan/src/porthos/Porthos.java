package porthos;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import dartagnan.*;
import dartagnan.program.utils.EventRepository;
import dartagnan.wmm.Wmm;
import dartagnan.wmm.WmmResolver;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;
import com.microsoft.z3.enumerations.Z3_ast_print_mode;

import dartagnan.program.Program;
import dartagnan.utils.Utils;
import dartagnan.wmm.relation.Relation;
import static dartagnan.utils.Encodings.encodeReachedState;
import static dartagnan.utils.Encodings.encodeCommonExecutions;

import org.apache.commons.cli.*;

public class Porthos {

    public static void main(String[] args) throws Z3Exception, IOException {

        Options options = new Options();

        Option sourceOption = new Option("s", "source", true, "Source architecture to compile the program");
        sourceOption.setRequired(true);
        options.addOption(sourceOption);

        Option targetOption = new Option("t", "target", true, "Target architecture to compile the program");
        targetOption.setRequired(true);
        options.addOption(targetOption);

        Option inputOption = new Option("i", "input", true, "Path to the file containing the input program");
        inputOption.setRequired(true);
        options.addOption(inputOption);

        Option sourceCatOption = new Option("scat", true, "Path to the CAT file of the source memory model");
        sourceCatOption.setRequired(true);
        options.addOption(sourceCatOption);

        Option targetCatOption = new Option("tcat", true, "Path to the CAT file of the target memory model");
        targetCatOption.setRequired(true);
        options.addOption(targetCatOption);

        options.addOption(new Option("unroll", "Unrolling steps"));
        options.addOption(new Option("idl", "Uses IDL encoding for transitive closure"));
        options.addOption(new Option("relax", "Uses relax encoding for recursive relations"));
        options.addOption(new Option("draw", "Path to save the execution graph if the state is reachable"));
        options.addOption(new Option("rels", "Relations to be drawn in the graph"));

        CommandLineParser parserCmd = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parserCmd.parse(options, args);
        }
        catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("PORTHOS", options);
            System.exit(1);
            return;
        }

        WmmResolver wmmResolver = new WmmResolver();

        String source = cmd.getOptionValue("source").trim();
        if(!(wmmResolver.getArchSet().contains(source))){
            System.out.println("Unrecognized source");
            System.exit(0);
            return;
        }

        String target = cmd.getOptionValue("target").trim();
        if(!(wmmResolver.getArchSet().contains(target))){
            System.out.println("Unrecognized target");
            System.exit(0);
            return;
        }

        String inputFilePath = cmd.getOptionValue("input");
        if(!inputFilePath.endsWith("pts") && !inputFilePath.endsWith("litmus")) {
            System.out.println("Unrecognized program format");
            System.exit(0);
            return;
        }

        String[] rels = new String[100];
        if(cmd.hasOption("rels")) {
            rels = cmd.getOptionValues("rels");
        }

        Wmm mcmS = Dartagnan.parseCat(cmd.getOptionValue("scat"));
        Wmm mcmT = Dartagnan.parseCat(cmd.getOptionValue("tcat"));

        if (cmd.hasOption("relax")) {
            Relation.Approx = true;
        }

        int steps = 1;
        if(cmd.hasOption("unroll")) {
            steps = Integer.parseInt(cmd.getOptionValue("unroll"));
        }

        Program p = Dartagnan.parseProgram(inputFilePath);
        p.initialize(steps);

        Program pSource = p.clone();
        Program pTarget = p.clone();

        pSource.compile(source, false, true);
        Integer startEId = Collections.max(pSource.getEventRepository().getEvents(EventRepository.EVENT_INIT).stream().map(e -> e.getEId()).collect(Collectors.toSet())) + 1;
        pTarget.compile(target, false, true, startEId);

        Context ctx = new Context();
        ctx.setPrintMode(Z3_ast_print_mode.Z3_PRINT_SMTLIB_FULL);
        Solver s = ctx.mkSolver(ctx.mkTactic("qfufbv"));
        Solver s2 = ctx.mkSolver(ctx.mkTactic("qfufbv"));

        Relation.EncodeCtrlPo = wmmResolver.encodeCtrlPo(source);
        BoolExpr sourceDF = pSource.encodeDF(ctx);
        BoolExpr sourceCF = pSource.encodeCF(ctx);
        BoolExpr sourceDF_RF = pSource.encodeDF_RF(ctx);
        BoolExpr sourceFV = pSource.encodeFinalValues(ctx);
        BoolExpr sourceMM = mcmS.encode(pSource, ctx, false, cmd.hasOption("idl"));

        Relation.EncodeCtrlPo = wmmResolver.encodeCtrlPo(target);
        s.add(pTarget.encodeDF(ctx));
        s.add(pTarget.encodeCF(ctx));
        s.add(pTarget.encodeDF_RF(ctx));
        s.add(pTarget.encodeFinalValues(ctx));
        s.add(mcmT.encode(pTarget, ctx, false, cmd.hasOption("idl")));
        s.add(mcmT.consistent(pTarget, ctx));

        s.add(sourceDF);
        s.add(sourceCF);
        s.add(sourceDF_RF);
        s.add(sourceFV);
        s.add(sourceMM);
        s.add(mcmS.inconsistent(pSource, ctx));

        s.add(encodeCommonExecutions(pTarget, pSource, ctx));

        s2.add(sourceDF);
        s2.add(sourceCF);
        s2.add(sourceDF_RF);
        s2.add(sourceFV);
        s2.add(sourceMM);
        s2.add(mcmS.consistent(pSource, ctx));

        int iterations = 0;
        Status lastCheck = Status.SATISFIABLE;
        Set<Expr> visited = new HashSet<Expr>();

        while(lastCheck == Status.SATISFIABLE) {

            lastCheck = s.check();
            if(lastCheck == Status.SATISFIABLE) {
                iterations = iterations + 1;
                Model model = s.getModel();
                s2.push();
                BoolExpr reachedState = encodeReachedState(pTarget, model, ctx);
                visited.add(reachedState);
                assert(iterations == visited.size());
                s2.add(reachedState);
                if(s2.check() == Status.UNSATISFIABLE) {
                    System.out.println("The program is not state-portable");
                    System.out.println("Iterations: " + iterations);
                    if(cmd.hasOption("draw")) {
                  	  String outputPath = cmd.getOptionValue("draw");
                  	  Utils.drawGraph(p, pSource, pTarget, ctx, s.getModel(), outputPath, rels);
                      }
                    return;
                }
                else {
                    s2.pop();
                    s.add(ctx.mkNot(reachedState));
                }
            }
            else {
                System.out.println("The program is state-portable");
                System.out.println("Iterations: " + iterations);
                return;
            }
        }
    }
}