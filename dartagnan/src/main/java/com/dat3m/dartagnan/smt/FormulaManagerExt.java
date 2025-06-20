package com.dat3m.dartagnan.smt;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.sosy_lab.java_smt.api.*;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.IntStream;

/*
    Enriches JavaSMT's FormulaManager with
        - new theories (TupleFormulaManager)
        - utility functions
 */
public class FormulaManagerExt {

    private final FormulaManager fmgr;
    private final BooleanFormulaManager bgr;
    private final TupleFormulaManager tmgr;
    private final BitvectorFormulaManager bvgr;

    public FormulaManagerExt(FormulaManager fmgr) {
        this.fmgr = fmgr;
        this.tmgr = new TupleFormulaManager(this);
        this.bvgr = fmgr.getBitvectorFormulaManager();
        this.bgr = fmgr.getBooleanFormulaManager();
    }

    public FormulaManager getUnderlyingFormulaManager() { return fmgr; }
    public BooleanFormulaManager getBooleanFormulaManager() { return fmgr.getBooleanFormulaManager(); }
    public IntegerFormulaManager getIntegerFormulaManager() { return fmgr.getIntegerFormulaManager(); }
    public BitvectorFormulaManager getBitvectorFormulaManager() { return fmgr.getBitvectorFormulaManager(); }
    public TupleFormulaManager getTupleFormulaManager() { return tmgr; }

    // ====================================================================================================
    // Convenience

    public String escape(String varName) {
        return fmgr.escape(varName);
    }

    public String dumpFormula(BooleanFormula formula) {
        return fmgr.dumpFormula(formula).toString();
    }

    // ====================================================================================================
    // Utility

    public boolean hasSameType(Formula left, Formula right) {
        if (left instanceof NumeralFormula.IntegerFormula && right instanceof NumeralFormula.IntegerFormula) {
            return true;
        } else if (left instanceof BooleanFormula && right instanceof BooleanFormula) {
            return true;
        } else if (left instanceof BitvectorFormula x && right instanceof BitvectorFormula y) {
            final BitvectorFormulaManager bvmgr = getBitvectorFormulaManager();
            return bvmgr.getLength(x) == bvmgr.getLength(y);
        } else if (left instanceof TupleFormula x && right instanceof TupleFormula y) {
            if (x.elements.size() != y.elements.size()) {
                return false;
            }
            return IntStream.range(0, x.elements.size()).allMatch(
                    i -> hasSameType(x.elements.get(i), y.elements.get(i))
            );
        }

        return false;
    }

    public BooleanFormula equal(Formula left, Formula right) {
        Preconditions.checkArgument(hasSameType(left, right));

        if (left instanceof NumeralFormula.IntegerFormula l) {
            return getIntegerFormulaManager().equal(l, (NumeralFormula.IntegerFormula) right);
        } else if (left instanceof BitvectorFormula l) {
            return getBitvectorFormulaManager().equal(l, (BitvectorFormula) right);
        } else if (left instanceof BooleanFormula l) {
            return getBooleanFormulaManager().equivalence(l, (BooleanFormula) right);
        } else if (left instanceof TupleFormula l && right instanceof TupleFormula r) {
            Preconditions.checkArgument(l.elements.size() == r.elements.size());
            return IntStream.range(0, l.elements.size())
                    .mapToObj(i -> equal(l.elements.get(i), r.elements.get(i)))
                    .reduce(bgr.makeTrue(), bgr::and);
        }

        throw new UnsupportedOperationException(String.format("Unknown types for equal(%s, %s)", left, right));
    }

    @SuppressWarnings("unchecked")
    public <TFormula extends Formula> TFormula ifThenElse(BooleanFormula guard, TFormula thenF, TFormula elseF) {
        Preconditions.checkArgument(hasSameType(thenF, elseF));

        if (thenF instanceof TupleFormula thenT && elseF instanceof TupleFormula elseT) {
            final List<Formula> inner = IntStream.range(0, thenT.elements.size())
                    .mapToObj(i -> ifThenElse(guard, thenT.elements.get(i), elseT.elements.get(i)))
                    .collect(ImmutableList.toImmutableList());
            return (TFormula) getTupleFormulaManager().makeTuple(inner);
        }

        return getBooleanFormulaManager().ifThenElse(guard, thenF, elseF);
    }


// when comparing bitvectors signed is hardcoded false
    public BooleanFormula lessThan(Formula left, Formula right) {
        Preconditions.checkArgument(hasSameType(left, right));
        if (left instanceof NumeralFormula.IntegerFormula l) {
            return getIntegerFormulaManager().lessThan(l, (NumeralFormula.IntegerFormula) right);
        } else if (left instanceof BitvectorFormula l) {
            return getBitvectorFormulaManager().lessThan(l, (BitvectorFormula) right, false);
        }else if (left instanceof TupleFormula l) {
            return bgr.and(bvgr.lessThan(l.second(),((TupleFormula)right).second(), false),bvgr.equal(l.first(),((TupleFormula)right).first()));
        }
        throw new UnsupportedOperationException(String.format("Unknown types for lessThan(%s, %s)", left, right));
    }

    public BooleanFormula greaterOrEquals(Formula left, Formula right) {
        Preconditions.checkArgument(hasSameType(left, right));
        if (left instanceof NumeralFormula.IntegerFormula l) {
            return getIntegerFormulaManager().greaterOrEquals(l, (NumeralFormula.IntegerFormula) right);
        } else if (left instanceof BitvectorFormula l) {
            return getBitvectorFormulaManager().greaterOrEquals(l, (BitvectorFormula) right, false);
        }else if (left instanceof TupleFormula l) {
            return bgr.and(bvgr.greaterOrEquals(l.second(),((TupleFormula)right).second(), false),bvgr.equal(l.first(),((TupleFormula)right).first()));
        }
        throw new UnsupportedOperationException(String.format("Unknown types for greaterOrEqual(%s, %s)", left, right));
    }
    public Formula makeConstant(Formula ty, BigInteger val) {

        if (ty instanceof NumeralFormula.IntegerFormula l) {
            return getIntegerFormulaManager().makeNumber(val);
        } else if (ty instanceof BitvectorFormula l) {
            return getBitvectorFormulaManager().makeBitvector(getBitvectorFormulaManager().getLength(l), val);
        }
        throw new UnsupportedOperationException("Unknown types");
    }
    public Formula subtract(Formula left, Formula right) {
        Preconditions.checkArgument(hasSameType(left, right));
        if (left instanceof NumeralFormula.IntegerFormula l) {
            return getIntegerFormulaManager().subtract(l, (NumeralFormula.IntegerFormula) right);
        } else if (left instanceof BitvectorFormula l) {
            return getBitvectorFormulaManager().subtract(l, (BitvectorFormula) right);
        }
        throw new UnsupportedOperationException(String.format("Unknown types for substract(%s, %s)", left, right));
    }

    public BooleanFormula greaterThan(Formula left, Formula right) {
        Preconditions.checkArgument(hasSameType(left, right));
        if (left instanceof NumeralFormula.IntegerFormula l) {
            return getIntegerFormulaManager().greaterThan(l, (NumeralFormula.IntegerFormula) right);
        } else if (left instanceof BitvectorFormula l) {
            return getBitvectorFormulaManager().greaterThan(l, (BitvectorFormula) right, false);
        }else if (left instanceof TupleFormula l) {
            return bgr.and(bvgr.greaterThan(l.second(),((TupleFormula)right).second(), false),bvgr.equal(l.first(),((TupleFormula)right).first()));
        }
        throw new UnsupportedOperationException(String.format("Unknown types for greaterThan(%s, %s)", left, right));
    }

    public BooleanFormula lessOrEquals(Formula left, Formula right) {
        Preconditions.checkArgument(hasSameType(left, right));
        if (left instanceof NumeralFormula.IntegerFormula l) {
            return getIntegerFormulaManager().lessOrEquals(l, (NumeralFormula.IntegerFormula) right);
        } else if (left instanceof BitvectorFormula l) {
            return getBitvectorFormulaManager().lessOrEquals(l, (BitvectorFormula) right, false);
        }else if (left instanceof TupleFormula l) {
            return bgr.and(bvgr.lessOrEquals(l.second(),((TupleFormula)right).second(), false),bvgr.equal(l.first(),((TupleFormula)right).first()));
        }
        throw new UnsupportedOperationException(String.format("Unknown types for lessOrEqual(%s, %s)", left, right));
    }
}
