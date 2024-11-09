package com.dat3m.dartagnan.expression.type;

import com.dat3m.dartagnan.expression.Type;
import com.google.common.base.Preconditions;

public final class PointerType implements Type {

    private final int bitWidth;

    PointerType(int bitWidth) {
        Preconditions.checkArgument(bitWidth > 0, "Invalid size for pointers: %s", bitWidth);
        this.bitWidth = bitWidth;
    }

    public int getBitWidth() {
        return bitWidth;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return obj instanceof PointerType;
    }

    @Override
    public int hashCode() {
        return 31 * bitWidth;
    }

    @Override
    public String toString() {
        return "ptr" + bitWidth;
    }
}