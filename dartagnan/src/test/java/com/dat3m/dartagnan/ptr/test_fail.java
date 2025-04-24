package com.dat3m.dartagnan.ptr;

import com.dat3m.dartagnan.configuration.Arch;
import com.dat3m.dartagnan.configuration.OptionNames;
import com.dat3m.dartagnan.utils.Result;
import com.dat3m.dartagnan.utils.rules.Provider;
import com.dat3m.dartagnan.verification.solving.AssumeSolver;
import com.dat3m.dartagnan.verification.solving.RefinementSolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.ConfigurationBuilder;

import java.io.IOException;
import java.util.Arrays;

import static com.dat3m.dartagnan.configuration.Arch.RISCV;
import static com.dat3m.dartagnan.utils.Result.FAIL;
import static com.dat3m.dartagnan.utils.Result.PASS;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class test_fail extends AbstractCTest {

    private final int bound;

    public test_fail(String name, Arch target, Result expected, int bound) {
        super(name, target, expected);
        this.bound = bound;
    }

    @Override
    protected Provider<String> getProgramPathProvider() {
        return () -> "/home/lightone/Code/Dat3M/dartagnan/src/test/resources/ptr/test_fail.ll";
    }


    @Override
    protected long getTimeout() {
        return 50000;
    }

    @Override
    protected Provider<Configuration> getConfigurationProvider() {
        return Provider.fromSupplier(() -> {
            ConfigurationBuilder builder = Configuration.builder();
            builder.setOption(OptionNames.USE_INTEGERS, "false");
            builder.setOption(OptionNames.USE_BVPOINTERS, "false");
            builder.setOption(OptionNames.RECURSION_BOUND, String.valueOf(3));

            return builder.build();
        });
    }

    @Parameterized.Parameters(name = "{index}: {0}, target={1}")
    public static Iterable<Object[]> data() throws IOException {
        return Arrays.asList(new Object[][]{
                {"test", RISCV, FAIL, 1},
        });
    }

    @Test
    public void testAssume() throws Exception {
        AssumeSolver s = AssumeSolver.run(contextProvider.get(), proverProvider.get(), taskProvider.get());
        assertEquals(expected, s.getResult());
    }

    //@Test
    public void testRefinement() throws Exception {
        RefinementSolver s = RefinementSolver.run(contextProvider.get(), proverProvider.get(), taskProvider.get());
        assertEquals(expected, s.getResult());
    }
}