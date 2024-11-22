package com.dat3m.dartagnan.expression;

import com.dat3m.dartagnan.expression.booleans.BoolBinaryExpr;
import com.dat3m.dartagnan.expression.booleans.BoolLiteral;
import com.dat3m.dartagnan.expression.booleans.BoolUnaryExpr;
import com.dat3m.dartagnan.expression.floats.FloatBinaryExpr;
import com.dat3m.dartagnan.expression.floats.FloatCmpExpr;
import com.dat3m.dartagnan.expression.floats.FloatLiteral;
import com.dat3m.dartagnan.expression.floats.FloatSizeCast;
import com.dat3m.dartagnan.expression.floats.FloatUnaryExpr;
import com.dat3m.dartagnan.expression.floats.IntToFloatCast;
import com.dat3m.dartagnan.expression.integers.FloatToIntCast;
import com.dat3m.dartagnan.expression.integers.IntBinaryExpr;
import com.dat3m.dartagnan.expression.integers.IntCmpExpr;
import com.dat3m.dartagnan.expression.integers.IntLiteral;
import com.dat3m.dartagnan.expression.integers.IntSizeCast;
import com.dat3m.dartagnan.expression.integers.IntUnaryExpr;
import com.dat3m.dartagnan.expression.integers.PtrToIntCast;
import com.dat3m.dartagnan.expression.misc.ConstructExpr;
import com.dat3m.dartagnan.expression.misc.ExtractExpr;
import com.dat3m.dartagnan.expression.misc.ITEExpr;
import com.dat3m.dartagnan.expression.pointers.GEPExpr;
import com.dat3m.dartagnan.expression.pointers.IntToPtrCast;
import com.dat3m.dartagnan.expression.pointers.NullLiteral;
import com.dat3m.dartagnan.expression.pointers.PtrAddOffsetExpr;
import com.dat3m.dartagnan.expression.pointers.PtrCmpExpr;
import com.dat3m.dartagnan.expression.type.PointerType;
import com.dat3m.dartagnan.program.Function;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.memory.FinalMemoryValue;
import com.dat3m.dartagnan.program.memory.MemoryObject;
import com.dat3m.dartagnan.program.misc.NonDetValue;

public interface ExpressionVisitor<TRet> {

    // =================================== General ===================================
    default TRet visitExpression(Expression expr) { throw unsupported(expr, getClass()); }
    default TRet visitBinaryExpression(BinaryExpression expr) { return visitExpression(expr); }
    default TRet visitUnaryExpression(UnaryExpression expr) { return visitExpression(expr); }
    default TRet visitLeafExpression(LeafExpression expr) { return visitExpression(expr); }
    default TRet visitCastExpression(CastExpression expr) { return visitUnaryExpression(expr); }

    // =================================== Integers ===================================
    default TRet visitIntBinaryExpression(IntBinaryExpr expr) { return visitBinaryExpression(expr); }
    default TRet visitIntCmpExpression(IntCmpExpr expr) { return visitBinaryExpression(expr); }
    default TRet visitIntUnaryExpression(IntUnaryExpr expr) { return visitUnaryExpression(expr); }
    default TRet visitIntSizeCastExpression(IntSizeCast expr) { return visitCastExpression(expr); }
    default TRet visitFloatToIntCastExpression(FloatToIntCast expr) { return visitCastExpression(expr); }
    default TRet visitIntLiteral(IntLiteral lit) { return visitLeafExpression(lit); }
    default TRet visitPtrToIntCastExpression(PtrToIntCast expr) { return visitCastExpression(expr); }

    // =================================== Booleans ===================================
    default TRet visitBoolBinaryExpression(BoolBinaryExpr expr) { return visitBinaryExpression(expr); }
    default TRet visitBoolUnaryExpression(BoolUnaryExpr expr) { return visitUnaryExpression(expr); }
    default TRet visitBoolLiteral(BoolLiteral lit) { return visitLeafExpression(lit); }

    // =================================== Floats ===================================
    default TRet visitFloatBinaryExpression(FloatBinaryExpr expr) { return visitBinaryExpression(expr); }
    default TRet visitFloatCmpExpression(FloatCmpExpr expr) { return visitBinaryExpression(expr); }
    default TRet visitFloatUnaryExpression(FloatUnaryExpr expr) { return visitUnaryExpression(expr); }
    default TRet visitFloatSizeCastExpression(FloatSizeCast expr) { return visitCastExpression(expr); }
    default TRet visitIntToFloatCastExpression(IntToFloatCast expr) { return visitCastExpression(expr); }
    default TRet visitFloatLiteral(FloatLiteral lit) { return visitLeafExpression(lit); }

    // =================================== Aggregates ===================================
    default TRet visitExtractExpression(ExtractExpr extract) { return visitUnaryExpression(extract); }
    default TRet visitConstructExpression(ConstructExpr construct) { return visitExpression(construct); }

    // =================================== Pointer ===================================
    default TRet visitGEPExpression(GEPExpr expr) { return visitExpression(expr); }
    default TRet visitPtrCmpExpression(PtrCmpExpr expr) { return visitBinaryExpression(expr); }
    default TRet visitPtrAddOffsetExpression(PtrAddOffsetExpr expr) { return visitExpression(expr); }
    default TRet visitIntToPtrCastExpression(IntToPtrCast expr) { return visitCastExpression(expr); }
    default TRet visitNullPointerLiteral(NullLiteral nullptr){return visitLeafExpression(nullptr);}

    // =================================== Generic ===================================
    default TRet visitITEExpression(ITEExpr expr) { return visitExpression(expr); }

    // =================================== Program-specific ===================================
    default TRet visitRegister(Register reg) { return visitLeafExpression(reg); }
    default TRet visitFunction(Function function) { return visitLeafExpression(function); }
    default TRet visitMemoryObject(MemoryObject memObj) { return visitLeafExpression(memObj); }
    default TRet visitFinalMemoryValue(FinalMemoryValue val) { return visitLeafExpression(val); }
    default TRet visitNonDetValue(NonDetValue nonDet) { return visitLeafExpression(nonDet); }


    private static UnsupportedOperationException unsupported(Expression expr, Class<?> clazz) {
        final String error = String.format("Expression '%s' is unsupported by %s",
                expr.getClass().getSimpleName(), clazz.getSimpleName());
        return new UnsupportedOperationException(error);
    }
}
