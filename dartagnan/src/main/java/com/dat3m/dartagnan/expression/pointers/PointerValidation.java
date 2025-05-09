package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionKind;
import com.dat3m.dartagnan.expression.ExpressionVisitor;

import com.dat3m.dartagnan.expression.base.LeafExpressionBase;

import com.dat3m.dartagnan.expression.type.BooleanType;


public class PointerValidation extends LeafExpressionBase<BooleanType> {
    private final Expression pointerV;
    public PointerValidation(Expression pointer, BooleanType type) {
        super(type);
        this.pointerV = pointer;
    }
    public Expression getPointerV() {
        return pointerV;
    }

    @Override
    public ExpressionKind getKind() {
        return () -> "PointerValidation";
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitPointerValidationExpression(this);
    }
    @Override
    public String toString() {
        return "Valid " + pointerV.toString();
    }
}
