package com.dat3m.dartagnan.program.event.core;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.program.event.EventVisitor;
import com.dat3m.dartagnan.program.event.MemoryAccess;
import com.dat3m.dartagnan.program.event.Tag;
import com.dat3m.dartagnan.program.event.metadata.MemoryOrder;

import java.util.List;

// Called freeM because of collusion with free relation

public class FreeM extends AbstractMemoryCoreEvent {


    public FreeM(Expression address) {
        super(address,null);
        addTags(Tag.FREE);
    }

    protected FreeM(FreeM other) {
        super(other);
    }
    
    @Override
    public List<MemoryAccess> getMemoryAccesses() {
        return List.of(new MemoryAccess(address, accessType, MemoryAccess.Mode.FREE));
    }


    public String defaultString() {
        final MemoryOrder mo = getMetadata(MemoryOrder.class);
        return String.format("free(*%s)", address);
    }

    @Override
    public void transformExpressions(ExpressionVisitor<? extends Expression> exprTransformer) {
        super.transformExpressions(exprTransformer);
    }

    @Override
    public FreeM getCopy() {
        return new FreeM(this);
    }

    @Override
    public <T> T accept(EventVisitor<T> visitor) {
        return visitor.visitFreeM(this);
    }
}