package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionVisitor;

import com.dat3m.dartagnan.expression.base.UnaryExpressionBase;
import com.dat3m.dartagnan.expression.type.BooleanType;

import static com.dat3m.dartagnan.expression.pointers.PointerValidationOp.VALID;


public class PointerValidation extends UnaryExpressionBase<BooleanType, PointerValidationOp> {
    private final Expression pointerV;
    public PointerValidation(Expression pointer, BooleanType type) {
        super(type,VALID,pointer);
        this.pointerV = pointer;
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
