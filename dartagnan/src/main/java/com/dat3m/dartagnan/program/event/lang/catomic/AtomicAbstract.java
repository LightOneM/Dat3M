package com.dat3m.dartagnan.program.event.lang.catomic;

import com.dat3m.dartagnan.expression.IExpr;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.core.MemEvent;
import com.dat3m.dartagnan.program.event.core.utils.RegReaderData;
import com.dat3m.dartagnan.program.event.core.utils.RegWriter;
import com.dat3m.dartagnan.program.event.visitors.EventVisitor;
import com.google.common.collect.ImmutableSet;

import static com.dat3m.dartagnan.program.event.Tag.*;

public abstract class AtomicAbstract extends MemEvent implements RegWriter, RegReaderData {

    protected final Register resultRegister;
    protected final IExpr value;
    protected ImmutableSet<Register> dataRegs;

    AtomicAbstract(IExpr address, Register register, IExpr value, String mo) {
        super(address, mo);
        this.resultRegister = register;
        this.value = value;
        this.dataRegs = value.getRegs();
        addFilters(ANY, VISIBLE, MEMORY, READ, WRITE, RMW, REG_WRITER, REG_READER);
    }

    AtomicAbstract(AtomicAbstract other) {
        super(other);
        this.resultRegister = other.resultRegister;
        this.value = other.value;
        this.dataRegs = other.dataRegs;
    }

    @Override
    public Register getResultRegister() {
        return resultRegister;
    }

    @Override
    public ImmutableSet<Register> getDataRegs() {
        return dataRegs;
    }

	// Visitor
	// -----------------------------------------------------------------------------------------------------------------

	@Override
	public <T> T accept(EventVisitor<T> visitor) {
		return visitor.visitAtomicAbstract(this);
	}
}