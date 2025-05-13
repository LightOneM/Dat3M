package com.dat3m.dartagnan.llvm;

import com.dat3m.dartagnan.configuration.Arch;
import com.dat3m.dartagnan.utils.Result;
import com.dat3m.dartagnan.utils.rules.Provider;
import com.dat3m.dartagnan.verification.solving.AssumeSolver;
import com.dat3m.dartagnan.verification.solving.RefinementSolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;

import static com.dat3m.dartagnan.configuration.Arch.*;
import static com.dat3m.dartagnan.utils.ResourceHelper.getTestResourcePath;
import static com.dat3m.dartagnan.utils.Result.*;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PointerTest extends AbstractCTest {

    public PointerTest(String name, Arch target, Result expected) {
        super(name, target, expected);
    }

    @Override
    protected Provider<String> getProgramPathProvider() {
        return () -> getTestResourcePath("ptr/" + name + ".ll");
    }

    @Override
    protected long getTimeout() {
        return 60000;
    }

    @Parameterized.Parameters(name = "{index}: {0}, target={1}")
    public static Iterable<Object[]> data() throws IOException {
        return Arrays.asList(new Object[][]{

                {"oob", ARM8, FAIL},
//                {"oob", TSO, FAIL},
//                {"oob", POWER, FAIL},
//                {"oob", RISCV, FAIL},
                {"oop", ARM8, PASS},
//                {"oop", TSO, PASS},
//                {"oop", POWER, PASS},
//                {"oop", RISCV, PASS},
                {"add", ARM8, PASS},
//                {"ope", TSO, PASS},
//                {"ope", POWER, PASS},
//                {"ope", RISCV, PASS},
                {"wrd", ARM8, PASS},
//                {"wrd", TSO, PASS},
//                {"wrd", POWER, PASS},
//                {"wrd", RISCV, PASS},

        });
    }

    @Test
    public void testAssume() throws Exception {
        AssumeSolver s = AssumeSolver.run(contextProvider.get(), proverProvider.get(), taskProvider.get());
        System.out.println("Result :" + s.getResult() + "<>" + expected);
        assertEquals(expected, s.getResult());
    }

    @Test
    public void testRefinement() throws Exception {
        RefinementSolver s = RefinementSolver.run(contextProvider.get(), proverProvider.get(), taskProvider.get());
        System.out.println("Result :" + s.getResult() + "<>" + expected);
        assertEquals(expected, s.getResult());
    }
}