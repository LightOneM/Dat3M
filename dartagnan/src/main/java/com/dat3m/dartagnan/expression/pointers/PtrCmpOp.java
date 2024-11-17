package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.ExpressionKind;

public enum PtrCmpOp implements ExpressionKind {
    EQ, NEQ;

    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public String getSymbol() {
        return switch (this) {
            case EQ -> "==";
            case NEQ -> "!=";
        };
    }

    public PtrCmpOp inverted() {
        return switch (this) {
            case EQ -> NEQ;
            case NEQ -> EQ;
        };
    }

    public PtrCmpOp reverse() {
        return switch (this) {
            case EQ, NEQ -> this;
        };
    }
}