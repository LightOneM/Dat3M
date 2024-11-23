
package com.dat3m.dartagnan.expression.pointers;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionKind;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.base.ExpressionBase;
import com.dat3m.dartagnan.expression.type.IntegerType;
import com.dat3m.dartagnan.expression.type.PointerType;
import com.dat3m.dartagnan.expression.type.TypeFactory;
import com.dat3m.dartagnan.expression.utils.ExpressionHelper;
import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;



public final class PtrAddOffsetExpr extends ExpressionBase<PointerType> {
    final Expression base;
    final Expression offset;
    final ExpressionKind kind = ExpressionKind.Other.PTR_OFFSET;
    private static final TypeFactory types = TypeFactory.getInstance();

    public PtrAddOffsetExpr(Expression base, Expression offset) {
        super((PointerType) base.getType());
        // base is forced to be a ptr because we assume offset + ptr does not exist in llvm.
        ExpressionHelper.checkExpectedType(offset, IntegerType.class);
        ExpressionHelper.checkExpectedType(base, PointerType.class);
        Preconditions.checkArgument(offset.getType().equals(types.getArchType()),"Pointer offset addition of integer with non archType size");
        this.base = base;
        this.offset = offset;
    }

    public Expression getBase() { return base; }
    public Expression getOffset() { return offset; }

    @Override
    public List<Expression> getOperands() { return List.of(base, offset); }

    @Override
    public ExpressionKind getKind() { return kind; }

    @Override
    public int hashCode() {
        return Objects.hash(type, kind, base, offset);
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
    @Override
    public String toString() {return base.toString() + " P{+} " + offset.toString();}
}