package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.ExpressionKind;

public enum PtrUnaryOp implements ExpressionKind {
    CTLZ, CTTZ, MINUS;

    @Override
    public String toString() {
        return getSymbol();
    }

    @Override
    public String getSymbol(){
        System.out.println("Pointer unary operation detected  - Fix it in PtrUnaryOp");
        return "----------------nope-----------------";
    }
}