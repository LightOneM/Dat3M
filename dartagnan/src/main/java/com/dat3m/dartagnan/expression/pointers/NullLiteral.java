package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.ExpressionKind;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.Type;
import com.dat3m.dartagnan.expression.base.LeafExpressionBase;
import com.dat3m.dartagnan.expression.type.PointerType;

public final class NullLiteral extends LeafExpressionBase<PointerType>{


    public NullLiteral(PointerType type) {
        super(type);
    }

    @Override
    public String toString() {
        return "NullPtr";
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitNullPointerLiteral(this);
    }
    @Override
    public ExpressionKind.Other getKind() { return ExpressionKind.Other.NULL_PTR; }
}
