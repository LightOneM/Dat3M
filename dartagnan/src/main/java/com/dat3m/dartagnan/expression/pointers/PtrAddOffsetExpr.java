
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
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;



public final class PtrAddOffsetExpr extends ExpressionBase<PointerType> {
    final Expression base_pointer;
    final Expression added_offset;
    final ExpressionKind kind = ExpressionKind.Other.PTR_OFFSET;
    private static final TypeFactory types = TypeFactory.getInstance();

    public PtrAddOffsetExpr(Expression value, Expression added_offset) {
        super((PointerType) value.getType());
        // base is forced to be a ptr because we assume offset + ptr does not exist in llvm.
        ExpressionHelper.checkExpectedType(added_offset, IntegerType.class);
        ExpressionHelper.checkExpectedType(value, PointerType.class);
        Preconditions.checkArgument(added_offset.getType().equals(types.getArchType()),"Pointer offset addition of integer with non archType size");
        this.base_pointer = value;
        this.added_offset = added_offset;
    }

    public Expression getBasePointerVal() { return base_pointer; }
    public Expression getAddedOffset() { return added_offset; }

    @Override
    public ImmutableList<Expression> getOperands() { return ImmutableList.copyOf(List.of(base_pointer, added_offset)); }

    @Override
    public ExpressionKind getKind() { return kind; }

    @Override
    public int hashCode() {
        return Objects.hash(type, kind, base_pointer, added_offset);
    }


    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitPtrAddOffsetExpression(this);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PtrAddOffsetExpr expr
                && kind.equals(expr.kind)
                && base_pointer.equals(expr.base_pointer)
                && added_offset.equals(expr.added_offset));
    }
    @Override
    public String toString() {return base_pointer.toString() + " P{+offset} " + added_offset.toString();}
}