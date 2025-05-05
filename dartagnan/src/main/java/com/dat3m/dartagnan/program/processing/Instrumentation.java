package com.dat3m.dartagnan.program.processing;

import com.dat3m.dartagnan.exception.MalformedProgramException;
import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionFactory;
import com.dat3m.dartagnan.expression.Type;
import com.dat3m.dartagnan.expression.integers.IntLiteral;
import com.dat3m.dartagnan.expression.pointers.GEPExpr;
import com.dat3m.dartagnan.expression.processing.ExprTransformer;
import com.dat3m.dartagnan.expression.processing.ExpressionInspector;
import com.dat3m.dartagnan.expression.type.*;
import com.dat3m.dartagnan.program.Function;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.event.RegReader;
import com.dat3m.dartagnan.program.memory.MemoryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Instrumentation implements ProgramProcessor{
    private static final Logger logger = LogManager.getLogger(Instrumentation.class);

    private Instrumentation() { }

    public static Instrumentation newInstance() {
        return new Instrumentation();
    }

    @Override
    public void run(Program program) {
        final Set<Object> reachableFunctions = pointerInstrumentation(program);

        for (Function func : List.copyOf(program.getFunctions())) {
            if (!reachableFunctions.contains(func)) {
                program.removeFunction(func);
                logger.debug("Removed dead function: {}", func.getName());
            }
        }
    }



    private static final class PointerCheckInjector extends ExprTransformer {

        private final TypeFactory types = TypeFactory.getInstance();
        private final ExpressionFactory expressions = ExpressionFactory.getInstance();
        private final IntegerType archType = types.getArchType();








}
