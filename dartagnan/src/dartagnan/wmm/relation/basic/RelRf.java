package dartagnan.wmm.relation.basic;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import dartagnan.program.memory.Location;
import dartagnan.program.Program;
import dartagnan.program.event.Event;
import dartagnan.program.utils.EventRepository;
import dartagnan.wmm.relation.Relation;
import dartagnan.wmm.utils.Tuple;
import dartagnan.wmm.utils.TupleSet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static dartagnan.utils.Utils.edge;

public class RelRf extends Relation {

    public RelRf(){
        term = "rf";
    }

    @Override
    public void initialise(Program program, Context ctx, int encodingMode){
        super.initialise(program, ctx, encodingMode);
        encodeTupleSet.addAll(getMaxTupleSet());
    }

    @Override
    public TupleSet getMaxTupleSet(){
        if(maxTupleSet == null){
            maxTupleSet = new TupleSet();
            Collection<Event> eventsInit = program.getEventRepository().getEvents(EventRepository.INIT);
            Collection<Event> eventsStore = program.getEventRepository().getEvents(EventRepository.STORE);
            Collection<Event> eventsLoad = program.getEventRepository().getEvents(EventRepository.LOAD);

            for(Event e1 : eventsInit){
                for(Event e2 : eventsLoad){
                    if(e1.getLoc() == e2.getLoc()){
                        maxTupleSet.add(new Tuple(e1, e2));
                    }
                }
            }

            for(Event e1 : eventsStore){
                for(Event e2 : eventsLoad){
                    if(e1.getLoc() == e2.getLoc()){
                        maxTupleSet.add(new Tuple(e1, e2));
                    }
                }
            }
        }
        return maxTupleSet;
    }

    @Override
    protected BoolExpr encodeApprox() {
        BoolExpr enc = ctx.mkTrue();

        if(!maxTupleSet.isEmpty()){
            for(Tuple tuple : maxTupleSet){
                BoolExpr rel = edge("rf", tuple.getFirst(), tuple.getSecond(), ctx);
                enc = ctx.mkAnd(enc, ctx.mkImplies(rel, ctx.mkAnd(tuple.getFirst().executes(ctx), tuple.getSecond().executes(ctx))));
            }

            Collection<Event> eventsLoad = program.getEventRepository().getEvents(EventRepository.LOAD);
            Collection<Event> eventsStoreInit = program.getEventRepository().getEvents(EventRepository.INIT | EventRepository.STORE);
            Collection<Location> locations = eventsLoad.stream().map(Event::getLoc).collect(Collectors.toSet());

            for(Location loc : locations) {
                for(Event r : eventsLoad){
                    if(r.getLoc() == loc){
                        Set<BoolExpr> rfPairs = new HashSet<>();
                        for(Event w : eventsStoreInit) {
                            if(w.getLoc() == loc){
                                rfPairs.add(edge("rf", w, r, ctx));
                            }
                        }
                        enc = ctx.mkAnd(enc, ctx.mkImplies(r.executes(ctx), encodeEO(rfPairs)));
                    }
                }
            }
        }

        return enc;
    }

    private BoolExpr encodeEO(Collection<BoolExpr> set) {
        BoolExpr enc = ctx.mkFalse();
        for(BoolExpr exp : set) {
            BoolExpr thisYesOthersNot = exp;
            for(BoolExpr x : set.stream().filter(x -> x != exp).collect(Collectors.toSet())) {
                thisYesOthersNot = ctx.mkAnd(thisYesOthersNot, ctx.mkNot(x));
            }
            enc = ctx.mkOr(enc, thisYesOthersNot);
        }
        return enc;
    }
}
