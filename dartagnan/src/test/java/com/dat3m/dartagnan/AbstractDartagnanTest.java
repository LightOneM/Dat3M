package com.dat3m.dartagnan;

import com.dat3m.dartagnan.analysis.Refinement;
import com.dat3m.dartagnan.parsers.cat.ParserCat;
import com.dat3m.dartagnan.parsers.program.ProgramParser;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.utils.ResourceHelper;
import com.dat3m.dartagnan.utils.Result;
import com.dat3m.dartagnan.utils.Settings;
import com.dat3m.dartagnan.utils.TestHelper;
import com.dat3m.dartagnan.verification.RefinementTask;
import com.dat3m.dartagnan.verification.VerificationTask;
import com.dat3m.dartagnan.wmm.Wmm;
import com.dat3m.dartagnan.wmm.utils.Arch;
import com.dat3m.dartagnan.wmm.utils.alias.Alias;
import org.junit.Test;
import org.sosy_lab.java_smt.api.ProverEnvironment;
import org.sosy_lab.java_smt.api.SolverContext;
import org.sosy_lab.java_smt.api.SolverContext.ProverOptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import static com.dat3m.dartagnan.GlobalSettings.SKIP_TIMINGOUT_LITMUS;
import static com.dat3m.dartagnan.analysis.Base.runAnalysisTwoSolvers;
import static com.dat3m.dartagnan.utils.ResourceHelper.getCSVFileName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public abstract class AbstractDartagnanTest {

	static final int SOLVER_TIMEOUT = 60;
    static final int TIMEOUT = 10000;
	
    static Iterable<Object[]> buildParameters(String litmusPath, String cat, Arch target) throws IOException {
    	int n = ResourceHelper.LITMUS_RESOURCE_PATH.length();
        Map<String, Result> expectationMap = ResourceHelper.getExpectedResults();
        Wmm wmm = new ParserCat().parse(new File(ResourceHelper.CAT_RESOURCE_PATH + cat));

        Settings s1 = new Settings(Alias.CFIS, 1, SOLVER_TIMEOUT);
        try (Stream<Path> fileStream = Files.walk(Paths.get(ResourceHelper.LITMUS_RESOURCE_PATH + litmusPath))) {
            return fileStream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(f -> f.endsWith("litmus"))
                    // All litmus test timing out with refinement match this
                    .filter(f -> !SKIP_TIMINGOUT_LITMUS || !f.contains("manual/extra"))
                    .filter(f -> !SKIP_TIMINGOUT_LITMUS || !f.contains("PPC/Dart-"))
                    .filter(f -> expectationMap.containsKey(f.substring(n)))
                    .map(f -> new Object[]{f, expectationMap.get(f.substring(n))})
                    .collect(ArrayList::new,
                            (l, f) -> l.add(new Object[]{f[0], f[1], target, wmm, s1}), ArrayList::addAll);
        }
    }

    private final String path;
    private final Result expected;
    private final Arch target;
    private final Wmm wmm;
    private final Settings settings;

    AbstractDartagnanTest(String path, Result expected, Arch target, Wmm wmm, Settings settings) {
        this.path = path;
        this.expected = expected;
        this.target = target;
        this.wmm = wmm;
        this.settings = settings;
    }

    @Test(timeout = TIMEOUT)
    public void test() {
        try (SolverContext ctx = TestHelper.createContext();
             ProverEnvironment prover1 = ctx.newProverEnvironment(ProverOptions.GENERATE_MODELS);
             ProverEnvironment prover2 = ctx.newProverEnvironment(ProverOptions.GENERATE_MODELS);
        	 BufferedWriter writer = new BufferedWriter(new FileWriter(getCSVFileName(getClass(), "two-solvers"), true)))
        {
            Program program = new ProgramParser().parse(new File(path));
            if (program.getAss() != null) {
                VerificationTask task = new VerificationTask(program, wmm, target, settings);
                long start = System.currentTimeMillis();
                assertEquals(expected, runAnalysisTwoSolvers(ctx, prover1, prover2, task));
                long solvingTime = System.currentTimeMillis() - start;
                writer.append(path.substring(path.lastIndexOf("/") + 1)).append(", ").append(Long.toString(solvingTime));
                writer.newLine();
            }
        } catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test(timeout = TIMEOUT)
    public void testRefinement() {
        try (SolverContext ctx = TestHelper.createContext();
             ProverEnvironment prover = ctx.newProverEnvironment(ProverOptions.GENERATE_MODELS);
             BufferedWriter writer = new BufferedWriter(new FileWriter(getCSVFileName(getClass(), "refinement"), true)))
        {
            Program program = new ProgramParser().parse(new File(path));
            if (program.getAss() != null) {
                VerificationTask task = new VerificationTask(program, wmm, target, settings);
                long start = System.currentTimeMillis();
                assertEquals(expected, Refinement.runAnalysisSaturationSolver(ctx, prover,
                        RefinementTask.fromVerificationTaskWithDefaultBaselineWMM(task)));
                long solvingTime = System.currentTimeMillis() - start;
                writer.append(path.substring(path.lastIndexOf("/") + 1)).append(", ").append(Long.toString(solvingTime));
                writer.newLine();
            }
        } catch (Exception e){
            fail(e.getMessage());
        }
    }
}
