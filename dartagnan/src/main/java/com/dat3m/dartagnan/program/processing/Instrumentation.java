package com.dat3m.dartagnan.program.processing;
import com.dat3m.dartagnan.expression.*;
import com.dat3m.dartagnan.expression.type.TypeFactory;
import com.dat3m.dartagnan.program.Function;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.event.core.Label;
import com.dat3m.dartagnan.program.event.core.MemoryCoreEvent;
import com.dat3m.dartagnan.program.event.EventFactory;
import com.dat3m.dartagnan.program.Thread;
import com.dat3m.dartagnan.program.event.metadata.Metadata;

import java.util.ArrayList;
import java.util.List;

public class Instrumentation implements ProgramProcessor {
    static private final ExpressionFactory ef = ExpressionFactory.getInstance();
    private Instrumentation() {}
    public static Instrumentation newInstance() {
        return new Instrumentation();
    }
    public Integer gid = -1; // just for printing after instrumentation.



    private void instrument(List<MemoryCoreEvent> memoryEvents){
        for (MemoryCoreEvent e : memoryEvents) {
            ArrayList<Event> events = new java.util.ArrayList<>();
            Label pass = EventFactory.newLabel("pass" + gid);
            events.add(EventFactory.newJump(ef.makePointerValidation(e.getAddress(), TypeFactory.getInstance().getBooleanType()),pass));
            events.add(EventFactory.newAssert(ef.makeFalse(),"Invalid pointer usage possibly OOB"));
            events.add(EventFactory.newAbortIf(ef.makeTrue()));
            events.add(pass);
            for (Event event : events) {event.setGlobalId(gid--);}
            e.insertBefore(events);
        }

    }

    @Override
    public void run(Program program) {
        // TODO add the break events to the end and goto whenever needed
        for (Function function : program.getFunctions()) {instrument(function.getEvents(MemoryCoreEvent.class));}
        for (Thread thread : program.getThreads()){instrument(thread.getEvents(MemoryCoreEvent.class));}
    }




}





