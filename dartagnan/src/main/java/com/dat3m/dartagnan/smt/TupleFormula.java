package com.dat3m.dartagnan.smt;

import com.google.common.collect.ImmutableList;
import org.sosy_lab.java_smt.api.BitvectorFormula;
import org.sosy_lab.java_smt.api.Formula;

import java.util.List;
import java.util.stream.Collectors;

/*
    Implementation Note:
    We implement JavaSMT's Formula interface so this appears like a normal formula,
    however, it won't support many of JavaSMT's features like formula traversal.
 */
public class TupleFormula implements Formula {

    public final ImmutableList<Formula> elements;

    TupleFormula(List<Formula> elements) {
        this.elements = ImmutableList.copyOf(elements);
    }

    public int getSize() { return elements.size(); }

    @Override
    public String toString() {
        return elements.stream()
                .map(Object::toString)
                .collect(Collectors.joining(",", "{ ", " }"));
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        } else {
            final TupleFormula other = (TupleFormula) obj;
            return this.elements.equals(other.elements);
        }
    }

    // TODO swap this to accommodate ints too
    // TODO this is bad code: define base and offset for pointer type only ( in typed formula if pointer type )

    public Formula first(){
        return elements.get(0);
    }
    public Formula second(){
        return elements.get(1);
    }
    public Formula third(){
        return elements.get(2);
    }

}
