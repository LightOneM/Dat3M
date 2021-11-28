package com.dat3m.dartagnan.solver.newcaat.predicates.relationGraphs;

import com.dat3m.dartagnan.solver.newcaat.domain.Domain;
import com.dat3m.dartagnan.solver.newcaat.misc.EdgeDirection;
import com.dat3m.dartagnan.solver.newcaat.predicates.CAATPredicate;
import com.dat3m.dartagnan.solver.newcaat.predicates.Derivable;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

/*
    As opposed to a "virtualized graph", a materialized graph actually stores its nodes/edges explicitly.
    MaterializedGraph simply encapsulates a SimpleGraph and delegates all its methods
    to the underlying SimpleGraph (similar to a forwarding decorator).
 */
public abstract class MaterializedGraph extends BaseGraph {

    protected final SimpleGraph simpleGraph;

    protected MaterializedGraph() {
        this.simpleGraph = new SimpleGraph();
    }

    @Override
    public Edge get(Edge edge) {
        return simpleGraph.get(edge);
    }

    @Override
    public boolean containsById(int a, int b) {
        return simpleGraph.containsById(a, b);
    }

    @Override
    public boolean contains(Edge edge) {
        return simpleGraph.contains(edge);
    }

    @Override
    public void backtrackTo(int time) {
        simpleGraph.backtrackTo(time);
    }

    @Override
    public void initializeToDomain(Domain<?> domain) {
        super.initializeToDomain(domain);
        simpleGraph.initializeToDomain(domain);
    }

    @Override
    public int getEstimatedSize() {
        return simpleGraph.getEstimatedSize();
    }

    @Override
    public int getEstimatedSize(int e, EdgeDirection dir) {
        return simpleGraph.getEstimatedSize(e, dir);
    }

    @Override
    public int getMinSize() {
        return simpleGraph.getMinSize();
    }

    @Override
    public int getMinSize(int e, EdgeDirection dir) {
        return simpleGraph.getMinSize(e, dir);
    }

    @Override
    public int getMaxSize() {
        return simpleGraph.getMaxSize();
    }

    @Override
    public int getMaxSize(int e, EdgeDirection dir) {
        return simpleGraph.getMaxSize(e, dir);
    }

    @Override
    public Stream<Edge> edgeStream() {
        return simpleGraph.edgeStream();
    }

    @Override
    public Stream<Edge> edgeStream(int e, EdgeDirection dir) {
        return simpleGraph.edgeStream(e, dir);
    }

    @Override
    public Iterator<Edge> edgeIterator() {
        return simpleGraph.edgeIterator();
    }

    @Override
    public Iterator<Edge> edgeIterator(int e, EdgeDirection dir) {
        return simpleGraph.edgeIterator(e, dir);
    }

    @Override
    public int size() {
        return simpleGraph.size();
    }

    @Override
    public Collection<Edge> forwardPropagate(CAATPredicate changedSource, Collection<? extends Derivable> added) {
        return simpleGraph.forwardPropagate(changedSource, added);
    }

    @Override
    public int size(int e, EdgeDirection dir) { return simpleGraph.size(e, dir); }

    @Override
    public boolean isEmpty() {
        return simpleGraph.isEmpty();
    }

}
