package com.dat3m.dartagnan.expression.integers;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.base.CastExpressionBase;
import com.dat3m.dartagnan.expression.type.IntegerType;
import com.dat3m.dartagnan.expression.type.PointerType;
import com.dat3m.dartagnan.expression.utils.ExpressionHelper;

public final class PtrToIntCast extends CastExpressionBase<IntegerType, PointerType> {

    private final boolean isSigned;

    public PtrToIntCast(IntegerType targetType, Expression operand, boolean isSigned) {
        super(targetType, operand);
        ExpressionHelper.checkExpectedType(operand, PointerType.class);
        this.isSigned = false; // false because all pointers should be memory adresses
        // TODO discuss pointer casts and signs
    }

    public boolean isSigned() { return isSigned; }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitPtrToIntCastExpression(this);
    }
}