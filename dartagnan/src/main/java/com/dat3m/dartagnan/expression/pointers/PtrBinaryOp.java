package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.ExpressionKind;

public enum PtrBinaryOp implements ExpressionKind {
    ADD, SUB;

    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public String getSymbol() {
        return switch (this) {
            case ADD -> "+";
            case SUB -> "-";
            default -> super.toString();
        };
    }

    public static PtrBinaryOp PtrToOp(int i) {
        return switch (i) {
            case 0 -> ADD;
            case 1 -> SUB;
            default -> throw new UnsupportedOperationException("The pointers binary operator is not recognized");
        };
    }

    public boolean isCommutative() {
        return switch (this) {
            case ADD -> true;
            default -> false;
        };
    }
    // be carefull here substraction can be undefined //
}