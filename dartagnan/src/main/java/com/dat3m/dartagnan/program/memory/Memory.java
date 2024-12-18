package com.dat3m.dartagnan.program.memory;

import java.math.BigInteger;
import java.util.ArrayList;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionFactory;
import com.dat3m.dartagnan.expression.type.IntegerType;
import com.dat3m.dartagnan.expression.type.PointerType;
import com.dat3m.dartagnan.expression.type.TypeFactory;
import com.dat3m.dartagnan.program.event.core.Alloc;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class Memory {

    private final ArrayList<MemoryObject> objects = new ArrayList<>();
    private final PointerType ptrType = TypeFactory.getInstance().getPointerType();

    private final IntegerType archType = TypeFactory.getInstance().getArchType();
    private final Expression defaultAlignment = ExpressionFactory.getInstance().makeValue(8, archType);

    private int nextIndex = 1;

    // allocates null pointer, should be run only once and can't be derferenced
    // TODO ask about this

    public MemoryObject allocate_null() {
        final Expression sizeExpr = ExpressionFactory.getInstance().makeValue(0, archType);
        final MemoryObject memoryObject = new MemoryObject(-1, sizeExpr, defaultAlignment, null, ptrType);
        objects.add(memoryObject);
        return memoryObject;
    }



    // Generates a new, statically allocated memory object.
    public MemoryObject allocate(int size) {
        Preconditions.checkArgument(size > 0, "Illegal allocation. Size must be positive");
        final Expression sizeExpr = ExpressionFactory.getInstance().makeValue(BigInteger.valueOf(size), archType);
        final MemoryObject memoryObject = new MemoryObject(nextIndex++, sizeExpr, defaultAlignment, null, ptrType);
        objects.add(memoryObject);
        return memoryObject;
    }

    // Generates a new, dynamically allocated memory object.
    public MemoryObject allocate(Alloc allocationSite) {
        Preconditions.checkNotNull(allocationSite);
        final MemoryObject memoryObject = new MemoryObject(nextIndex++, allocationSite.getAllocationSize(),
                allocationSite.getAlignment(), allocationSite, ptrType);
        objects.add(memoryObject);
        return memoryObject;
    }

    public VirtualMemoryObject allocateVirtual(int size, boolean generic, VirtualMemoryObject alias) {
        Preconditions.checkArgument(size > 0, "Illegal allocation. Size must be positive");
        final Expression sizeExpr = ExpressionFactory.getInstance().makeValue(BigInteger.valueOf(size), archType);
        final VirtualMemoryObject address = new VirtualMemoryObject(nextIndex++, sizeExpr, defaultAlignment,
                generic, alias, ptrType);
        objects.add(address);
        return address;
    }

    public boolean deleteMemoryObject(MemoryObject obj) {
        return objects.remove(obj);
    }

    /**
     * Accesses all shared variables.
     * @return
     * Copy of the complete collection of allocated objects.
     */
    public ImmutableSet<MemoryObject> getObjects() {
        return ImmutableSet.copyOf(objects);
    }

}