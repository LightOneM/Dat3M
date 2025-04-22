//package com.dat3m.dartagnan.program.processing;
//
//import com.dat3m.dartagnan.expression.Expression;
//import com.dat3m.dartagnan.expression.processing.ExpressionInspector;
//import com.dat3m.dartagnan.program.Function;
//import com.dat3m.dartagnan.program.Program;
//import com.dat3m.dartagnan.program.event.RegReader;
//import com.dat3m.dartagnan.program.memory.MemoryObject;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.*;
//
//public class Instrumentation implements ProgramProcessor{
//    private static final Logger logger = LogManager.getLogger(Instrumentation.class);
//
//    private Instrumentation() { }
//
//    public static Instrumentation newInstance() {
//        return new Instrumentation();
//    }
//
//    @Override
//    public void run(Program program) {
//        final Set<Function> reachableFunctions = findPointerOperations(program);
//
//        for (Function func : List.copyOf(program.getFunctions())) {
//            if (!reachableFunctions.contains(func)) {
//                program.removeFunction(func);
//                logger.debug("Removed dead function: {}", func.getName());
//            }
//        }
//    }
//
//    private Set<Function> findPointerOperations(Program program) {
//        final Instrumentation.PointerOperationFinder pointeropslist = new RemoveDeadFunctions.FunctionCollector();
//
//        return pointeropslist;
//    }
//
//    private static class PointerOperationFinder implements ExpressionInspector {
//
//        private final Set<Function> pointerops = new HashSet<>();
//
//        public void reset() { pointerops.clear(); }
//
//        @Override
//        public Expression visitMemory(Function function) {
//            pointerops.add(function);
//            return function;
//        }
//    }
//
//
//
//
//
//
//}
