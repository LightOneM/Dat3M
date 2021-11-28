package com.dat3m.dartagnan.solver.newcaat.domain;

import java.util.Collection;

public interface Domain<T> {

    int size();
    Collection<T> getElements();

    int getId(Object obj);
    T getObjectById(int id);
}

