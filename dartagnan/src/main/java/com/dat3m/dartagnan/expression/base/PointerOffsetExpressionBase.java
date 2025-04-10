package com.dat3m.dartagnan.expression.base;

import com.dat3m.dartagnan.expression.PointerOffsetExpression;
import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionKind;
import com.dat3m.dartagnan.expression.Type;
import com.dat3m.dartagnan.program.event.common.NoInterface;

import java.util.List;
import java.util.Objects;

@NoInterface
public abstract class PointerOffsetExpressionBase<TType extends Type, TKind extends ExpressionKind>
        extends ExpressionBase<TType> implements PointerOffsetExpression {

    protected final Expression base;
    protected final Expression offset;
    protected final TKind kind;

    protected PointerOffsetExpressionBase(TType type, TKind kind, Expression base, Expression offset) {
        super(type);
        this.base = base;
        this.offset = offset;
        this.kind = kind;
    }


    // [x] Note that this class is generalised to accomodate possible opperations in the future

    public Expression getBase() { return base; }
    public Expression getOffset() { return offset; }

    @Override
    public List<Expression> getOperands() { return List.of(base, offset); }

    @Override
    public TKind getKind() { return kind; }

    @Override
    public int hashCode() {
        return Objects.hash(type, kind, base, offset);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != getClass()) {
            return false;
        }

        final PointerOffsetExpression expr = (PointerOffsetExpression) obj;
        return this.type.equals(expr.getType())
                && this.getKind().equals(expr.getKind())
                && this.base.equals(expr.getBase())
                && this.offset.equals(expr.getOffset());
    }
}
