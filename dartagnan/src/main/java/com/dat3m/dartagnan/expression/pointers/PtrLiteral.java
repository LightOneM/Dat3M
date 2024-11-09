package com.dat3m.dartagnan.expression.pointers;

import java.math.BigInteger;

import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.base.LiteralExpressionBase;
import com.dat3m.dartagnan.expression.type.PointerType;

/*
    Implementation note: This class represents values of fixed-size integer types using BigInteger.
    However, our integer types have no signedness but BigInteger does.
    This results in values that have their highest bit set to have non-unique representation,
    depending on whether that bit is treated as a sign-bit or not.
 */
public final class PtrLiteral extends LiteralExpressionBase<PointerType> {

    private final BigInteger value;
    private BigInteger origin = BigInteger.ZERO;
    private BigInteger offset = BigInteger.ZERO;

    public PtrLiteral(PointerType type, BigInteger value, BigInteger origin, BigInteger offset) {
        super(type);
        this.value = value;
        this.origin = origin; 
        this.offset = offset;
    }
     public PtrLiteral(PointerType type, BigInteger value) {
        super(type);
        this.value = value;
    }


    public BigInteger getValue() {
        return value;
    }
    public BigInteger getOrigin() {
        return origin;
    }
    public BigInteger getOffset() {
        return offset;
    }


    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitPtrLiteral(this);
    }

    @Override
    public int hashCode() {
        return getType().hashCode() ^ 0xa185f6b3 + value.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof PtrLiteral val && getType().equals(val.getType()) && value.equals(val.value);
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", getType(), value);
    }
}
