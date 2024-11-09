
package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.base.BinaryExpressionBase;
import com.dat3m.dartagnan.expression.type.PointerType;
import com.dat3m.dartagnan.expression.utils.ExpressionHelper;


public final class PtrBinaryExpr extends BinaryExpressionBase<PointerType, PtrBinaryOp> {

    public PtrBinaryExpr(Expression left, PtrBinaryOp op, Expression right) {
        super((PointerType) left.getType(), op, left, right);
        ExpressionHelper.checkSameType(left, right);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitPtrBinaryExpression(this);
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PtrBinaryExpr expr
                && kind.equals(expr.kind)
                && left.equals(expr.left)
                && right.equals(expr.right));
    }
}