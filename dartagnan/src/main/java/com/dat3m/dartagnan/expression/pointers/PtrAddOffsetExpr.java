
package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionKind;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.base.PointerOffsetExpressionBase;
import com.dat3m.dartagnan.expression.type.IntegerType;
import com.dat3m.dartagnan.expression.type.PointerType;
import com.dat3m.dartagnan.expression.utils.ExpressionHelper;


public final class PtrAddOffsetExpr extends PointerOffsetExpressionBase<PointerType, ExpressionKind.Other> {

    public PtrAddOffsetExpr(Expression base, Expression offset) {
        super((PointerType) base.getType(), ExpressionKind.Other.PTR_OFFSET, base, offset);
        ExpressionHelper.checkExpectedType(offset, IntegerType.class);
        //Preconditions.checkArgument(offset.) 
        // TODO force integer to be of arch size
        ExpressionHelper.checkExpectedType(base, PointerType.class);
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitPtrAddOffsetExpression(this);
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PtrAddOffsetExpr expr
                && kind.equals(expr.kind)
                && base.equals(expr.base)
                && offset.equals(expr.offset));
    }
}