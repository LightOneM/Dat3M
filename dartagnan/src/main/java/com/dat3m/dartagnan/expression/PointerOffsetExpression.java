package com.dat3m.dartagnan.expression;

public interface PointerOffsetExpression extends Expression {
    Expression getBase();
    Expression getOffset();
}
