package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.ExpressionKind;

public enum PtrAddOffsetOp implements ExpressionKind {// change expression kind
    ADD;

    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public String getSymbol() {
        return "+";
    }
// change ptr binary expr to pointer offset and define a kind
    public static PtrAddOffsetOp PtrToOp(int i) { // this one can be used later on
        return switch (i) {
            case 0 -> ADD;
            default -> throw new UnsupportedOperationException("The pointers binary operator is not recognized");
        };
    }

    public boolean isCommutative() {
        return switch (this) {
            case ADD -> true;
            default -> false;
        };
    }
}