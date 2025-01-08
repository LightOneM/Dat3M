package com.dat3m.dartagnan.encoding;

import ap.parser.smtlib.FoldVisitor;
import com.dat3m.dartagnan.encoding.formulas.TupleFormula;
import com.dat3m.dartagnan.encoding.formulas.TupleFormulaManager;
import com.google.common.base.Preconditions;
import org.sosy_lab.java_smt.api.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class EncodingHelper {

    private final FormulaManager fmgr;
    private final TupleFormulaManager tfmgr;

    public EncodingHelper(FormulaManager fmgr, TupleFormulaManager tfmgr) {
        this.fmgr = fmgr;
        this.tfmgr = tfmgr;
    }
//    public TupleFormulaManager getTupleFormulaManager() {
//        return tupleFormulaManager;
//    }

    public BooleanFormula equals(Formula left, Formula right) {
        if (left instanceof NumeralFormula.IntegerFormula iLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            return fmgr.getIntegerFormulaManager().equal(iLeft, iRight);
        }

        if (left instanceof BitvectorFormula bvLeft && right instanceof BitvectorFormula bvRight) {
            final BitvectorFormulaManager bvmgr = fmgr.getBitvectorFormulaManager();
            Preconditions.checkState(bvmgr.getLength(bvLeft) == bvmgr.getLength(bvRight));
            return fmgr.getBitvectorFormulaManager().equal(bvLeft, bvRight);
        }
        // TODO more important additions here Needs revision
        if (left instanceof TupleFormula tpLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            IntegerFormulaManager ifm = fmgr.getIntegerFormulaManager();
            Formula left_sum = tpLeft.elements.get(0);
            for(int c = 1; tpLeft.elements.size() > c; c++ ) {
                left_sum = ifm.add((NumeralFormula.IntegerFormula)left_sum , (NumeralFormula.IntegerFormula)tpLeft.elements.get(c));
            }
            return equals(left_sum, iRight);
        }
        if(left instanceof TupleFormula tfLeft && right instanceof TupleFormula tfRight) {
            return tfmgr.equal(tfLeft,tfRight);
        }
        throw new UnsupportedOperationException("Mismatching types: " + left + " and " + right );
    }
    // TODO add pointer to equal?

    public BooleanFormula greaterThan(Formula left, Formula right, boolean signed) {
        if (left instanceof NumeralFormula.IntegerFormula iLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            return fmgr.getIntegerFormulaManager().greaterThan(iLeft, iRight);
        }

        if (left instanceof BitvectorFormula bvLeft && right instanceof BitvectorFormula bvRight) {
            final BitvectorFormulaManager bvmgr = fmgr.getBitvectorFormulaManager();
            Preconditions.checkState(bvmgr.getLength(bvLeft) == bvmgr.getLength(bvRight));
            return fmgr.getBitvectorFormulaManager().greaterThan(bvLeft, bvRight, signed);
        }

        throw new UnsupportedOperationException("Mismatching types: " + left + " and " + right);
    }

    public BooleanFormula greaterOrEquals(Formula left, Formula right, boolean signed) {
        if (left instanceof NumeralFormula.IntegerFormula iLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            return fmgr.getIntegerFormulaManager().greaterOrEquals(iLeft, iRight);
        }

        if (left instanceof BitvectorFormula bvLeft && right instanceof BitvectorFormula bvRight) {
            final BitvectorFormulaManager bvmgr = fmgr.getBitvectorFormulaManager();
            Preconditions.checkState(bvmgr.getLength(bvLeft) == bvmgr.getLength(bvRight));
            return fmgr.getBitvectorFormulaManager().greaterOrEquals(bvLeft, bvRight, signed);
        }

        throw new UnsupportedOperationException("Mismatching types: " + left + " and " + right);
    }

    public Formula add(Formula left, Formula right) {
        if (left instanceof NumeralFormula.IntegerFormula iLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            return fmgr.getIntegerFormulaManager().add(iLeft, iRight);
        }

        if (left instanceof BitvectorFormula bvLeft && right instanceof BitvectorFormula bvRight) {
            final BitvectorFormulaManager bvmgr = fmgr.getBitvectorFormulaManager();
            Preconditions.checkState(bvmgr.getLength(bvLeft) == bvmgr.getLength(bvRight));
            return fmgr.getBitvectorFormulaManager().add(bvLeft, bvRight);
        }
        if (left instanceof TupleFormula tpLeft && right instanceof TupleFormula tpRight) {
            // We dont support pointer addition ?? does it happen? throw exceptions?
            //TODO the second part should not be a base pointer. to be enforced later on
            IntegerFormulaManager ifm = fmgr.getIntegerFormulaManager();
            Formula sum = tpLeft.elements.get(0);
            for(int c = 1; tpLeft.elements.size() > c; c++) {
                sum = ifm.add((NumeralFormula.IntegerFormula)sum , (NumeralFormula.IntegerFormula)tpLeft.elements.get(c));
            }
            for(int c = 1; tpRight.elements.size() > c; c++) {
                sum = ifm.add((NumeralFormula.IntegerFormula)sum , (NumeralFormula.IntegerFormula)tpRight.elements.get(c));
            }
            return sum;
        }
        if (left instanceof TupleFormula tpLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            IntegerFormulaManager ifm = fmgr.getIntegerFormulaManager();
            Formula base = tpLeft.elements.get(0);
            Formula a = tpLeft.elements.get(1);
            a = ifm.add((NumeralFormula.IntegerFormula) a, iRight);
            return tfmgr.makeTuple(List.of(base,a));
        }
        if (left instanceof TupleFormula tpLeft && right instanceof BitvectorFormula iRight) {
            BitvectorFormulaManager bvfm = fmgr.getBitvectorFormulaManager();
            Formula base = tpLeft.elements.get(0);
            Formula a = tpLeft.elements.get(1);
            a = bvfm.add((BitvectorFormula) a, iRight);
            return tfmgr.makeTuple(List.of(base,a));
        }

        throw new UnsupportedOperationException("Mismatching types: " + left + " and " + right);
    }

    public Formula subtract(Formula left, Formula right) {
        if (left instanceof NumeralFormula.IntegerFormula iLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            return fmgr.getIntegerFormulaManager().subtract(iLeft, iRight);
        }

        if (left instanceof BitvectorFormula bvLeft && right instanceof BitvectorFormula bvRight) {
            final BitvectorFormulaManager bvmgr = fmgr.getBitvectorFormulaManager();
            Preconditions.checkState(bvmgr.getLength(bvLeft) == bvmgr.getLength(bvRight));
            return fmgr.getBitvectorFormulaManager().subtract(bvLeft, bvRight);
        }

        throw new UnsupportedOperationException("Mismatching types: " + left + " and " + right);
    }

    public Formula remainder(Formula left, Formula right) {
        //FIXME: integer modulo and BV modulo have different semantics, the former is always positive, the latter
        // returns a value whose sign depends on one of the two BVs.
        // The results in this implementation will match if the denominator <right> is positive which is the most usual case.
        if (left instanceof NumeralFormula.IntegerFormula iLeft && right instanceof NumeralFormula.IntegerFormula iRight) {
            return fmgr.getIntegerFormulaManager().modulo(iLeft, iRight);
        }

        if (left instanceof BitvectorFormula bvLeft && right instanceof BitvectorFormula bvRight) {
            final BitvectorFormulaManager bvmgr = fmgr.getBitvectorFormulaManager();
            Preconditions.checkState(bvmgr.getLength(bvLeft) == bvmgr.getLength(bvRight));
            return fmgr.getBitvectorFormulaManager().smodulo(bvLeft, bvRight);
        }

        throw new UnsupportedOperationException("Mismatching types: " + left + " and " + right);
    }

    public Formula value(BigInteger value, FormulaType<?> type) {
        if (type.isIntegerType()) {
            return fmgr.getIntegerFormulaManager().makeNumber(value);
        } else if (type.isBitvectorType()) {
            int size = getBitSize(type);
            return fmgr.getBitvectorFormulaManager().makeBitvector(size, value);
        }
        throw new UnsupportedOperationException("Cannot generate value of type " + type);
    }

    public int getBitSize(FormulaType<?> type) {
        if (type.isIntegerType()) {
            return -1;
        } else if (type.isBitvectorType()) {
            return ((FormulaType.BitvectorType)type).getSize();
        }
        throw new UnsupportedOperationException("Cannot get bit-size for type " + type);
    }

    public FormulaType<?> typeOf(Formula formula) {
        return fmgr.getFormulaType(formula);
    }
}
