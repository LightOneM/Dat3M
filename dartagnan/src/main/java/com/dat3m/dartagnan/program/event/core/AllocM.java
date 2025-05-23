package com.dat3m.dartagnan.program.event.core;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.program.event.EventVisitor;
import com.dat3m.dartagnan.program.event.MemoryAccess;
import com.dat3m.dartagnan.program.event.Tag;
import com.dat3m.dartagnan.program.event.metadata.MemoryOrder;

import java.util.List;

// this class is used only to reference the allocation in CAT

public class AllocM extends AbstractMemoryCoreEvent {


    public AllocM(Expression address) {
        super(address,null);
        addTags(Tag.ALLOC, Tag.WRITE);
    }

    protected AllocM(AllocM other) {
        super(other);
    }

    @Override
    public List<MemoryAccess> getMemoryAccesses() {
        return List.of(new MemoryAccess(address, accessType, MemoryAccess.Mode.ALLOC));
    }

    public String defaultString() {
        final MemoryOrder mo = getMetadata(MemoryOrder.class);
        return String.format("Allocated memory to (*%s) in heap", address);
    }

    @Override
    public AllocM getCopy() {
        return new AllocM(this);
    }

}