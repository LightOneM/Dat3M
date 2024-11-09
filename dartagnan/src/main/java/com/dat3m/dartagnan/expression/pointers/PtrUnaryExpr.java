package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.base.UnaryExpressionBase;
import com.dat3m.dartagnan.expression.type.PointerType;

public final class PtrUnaryExpr extends UnaryExpressionBase<PointerType, PtrUnaryOp> {

    public PtrUnaryExpr(PtrUnaryOp operator, Expression operand) {
        super((PointerType) operand.getType(), operator, operand);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitPtrUnaryExpression(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj == this || obj instanceof PtrUnaryExpr expr
                && kind.equals(expr.kind)
                && operand.equals(expr.operand));
    }
}