package com.dat3m.dartagnan.encoding;

import com.dat3m.dartagnan.encoding.formulas.PointerFormula;
import com.dat3m.dartagnan.encoding.formulas.TupleFormula;
import java.math.BigInteger;
import static java.util.Arrays.asList;

import com.dat3m.dartagnan.encoding.formulas.TupleFormulaManager;
import com.dat3m.dartagnan.expression.integers.*;
import com.dat3m.dartagnan.expression.pointers.IntToPtrCast;
import com.dat3m.dartagnan.expression.type.IntegerType;
import com.dat3m.dartagnan.expression.type.NullLiteral;
import com.dat3m.dartagnan.expression.pointers.PtrAddOffsetExpr;
import com.dat3m.dartagnan.expression.pointers.PtrCmpExpr;
import com.dat3m.dartagnan.expression.type.PointerType;
import com.dat3m.dartagnan.program.memory.Memory;
import com.google.common.collect.ImmutableSet;
import org.sosy_lab.java_smt.api.*;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionVisitor;
import com.dat3m.dartagnan.expression.Type;
import com.dat3m.dartagnan.expression.aggregates.AggregateCmpExpr;
import com.dat3m.dartagnan.expression.aggregates.ConstructExpr;
import com.dat3m.dartagnan.expression.aggregates.ExtractExpr;
import com.dat3m.dartagnan.expression.aggregates.InsertExpr;
import com.dat3m.dartagnan.expression.booleans.BoolBinaryExpr;
import com.dat3m.dartagnan.expression.booleans.BoolLiteral;
import com.dat3m.dartagnan.expression.booleans.BoolUnaryExpr;
import com.dat3m.dartagnan.expression.misc.ITEExpr;
import com.dat3m.dartagnan.expression.type.TypeFactory;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.memory.FinalMemoryValue;
import com.dat3m.dartagnan.program.memory.MemoryObject;
import com.dat3m.dartagnan.program.misc.NonDetValue;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

class ExpressionEncoder implements ExpressionVisitor<Formula> {
    private static final TypeFactory types = TypeFactory.getInstance();

    private final EncodingContext context;
    private final FormulaManager formulaManager;
    private final TupleFormulaManager tupleFormulaManager;
    private final BooleanFormulaManager booleanFormulaManager;
    private final BitvectorFormulaManager bitvectorFormulaManager;
    private final Event event;
    // TODO bad structuring but works
    private final EncodingHelper helper;

    ExpressionEncoder(EncodingContext context, Event event) {
        this.context = context;
        this.formulaManager = context.getFormulaManager();
        this.tupleFormulaManager = context.getTupleFormulaManager();
        this.booleanFormulaManager = formulaManager.getBooleanFormulaManager();
        this.bitvectorFormulaManager = formulaManager.getBitvectorFormulaManager();
        this.event = event;
        this.helper = new EncodingHelper(formulaManager, tupleFormulaManager);
    }
    // TODO ?
    private IntegerFormulaManager integerFormulaManager() {
        return formulaManager.getIntegerFormulaManager();
    }


    BooleanFormula encodeAsBoolean(Expression expression) {
        Formula formula = expression.accept(this);
        if (formula instanceof BooleanFormula bForm) {
            return bForm;
        }
        if (formula instanceof BitvectorFormula bvForm) {
            BitvectorFormula zero = bitvectorFormulaManager.makeBitvector(bitvectorFormulaManager.getLength(bvForm), 0);
            return bitvectorFormulaManager.greaterThan(bvForm, zero, false);
        }
        assert formula instanceof IntegerFormula;
        IntegerFormulaManager imgr = integerFormulaManager();
        IntegerFormula zero = imgr.makeNumber(0);
        return imgr.greaterThan((IntegerFormula) formula, zero);
    }

    Formula encode(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public Formula visitIntCmpExpression(IntCmpExpr cmp) {
        Formula lhs = encode(cmp.getLeft());
        Formula rhs = encode(cmp.getRight());
        return context.encodeComparison(cmp.getKind(), lhs, rhs);
    }

    @Override
    public Formula visitBoolLiteral(BoolLiteral boolLiteral) {
        return booleanFormulaManager.makeBoolean(boolLiteral.getValue());
    }

    @Override
    public Formula visitBoolBinaryExpression(BoolBinaryExpr bBin) {
        BooleanFormula lhs = encodeAsBoolean(bBin.getLeft());
        BooleanFormula rhs = encodeAsBoolean(bBin.getRight());
        return switch (bBin.getKind()) {
            case AND -> booleanFormulaManager.and(lhs, rhs);
            case OR -> booleanFormulaManager.or(lhs, rhs);
            case IFF -> booleanFormulaManager.equivalence(lhs, rhs);
        };
    }

    @Override
    public Formula visitBoolUnaryExpression(BoolUnaryExpr bUn) {
        BooleanFormula inner = encodeAsBoolean(bUn.getOperand());
        return booleanFormulaManager.not(inner);
    }

    @Override
    public Formula visitNonDetValue(NonDetValue nonDet) {
        return context.makeVariable(nonDet.toString(), nonDet.getType());
    }

    @Override
    public Formula visitIntLiteral(IntLiteral intLiteral) {
        BigInteger value = intLiteral.getValue();
        Type type = intLiteral.getType();
        return context.makeLiteral(type, value);
    }

    @Override
    public Formula visitIntBinaryExpression(IntBinaryExpr iBin) {
        final Formula lhs = encode(iBin.getLeft());
        final Formula rhs = encode(iBin.getRight());
        final int bitWidth = iBin.getType().getBitWidth();

        if (lhs instanceof IntegerFormula i1 && rhs instanceof IntegerFormula i2) {
            BitvectorFormulaManager bvmgr = bitvectorFormulaManager;
            IntegerFormulaManager imgr = integerFormulaManager();
            switch (iBin.getKind()) {
                case ADD:
                    return imgr.add(i1, i2);
                case SUB:
                    return imgr.subtract(i1, i2);
                case MUL:
                    return imgr.multiply(i1, i2);
                case DIV:
                case UDIV:
                    return imgr.divide(i1, i2);
                case AND:

                    return bvmgr.toIntegerFormula(
                            bvmgr.and(
                                    bvmgr.makeBitvector(bitWidth, i1),
                                    bvmgr.makeBitvector(bitWidth, i2)),
                            false);
                case OR:

                    return bvmgr.toIntegerFormula(
                            bvmgr.or(
                                    bvmgr.makeBitvector(bitWidth, i1),
                                    bvmgr.makeBitvector(bitWidth, i2)),
                            false);
                case XOR:

                    return bvmgr.toIntegerFormula(
                            bvmgr.xor(
                                    bvmgr.makeBitvector(bitWidth, i1),
                                    bvmgr.makeBitvector(bitWidth, i2)),
                            false);
                case LSHIFT:

                    return bvmgr.toIntegerFormula(
                            bvmgr.shiftLeft(
                                    bvmgr.makeBitvector(bitWidth, i1),
                                    bvmgr.makeBitvector(bitWidth, i2)),
                            false);
                case RSHIFT:

                    return bvmgr.toIntegerFormula(
                            bvmgr.shiftRight(
                                    bvmgr.makeBitvector(bitWidth, i1),
                                    bvmgr.makeBitvector(bitWidth, i2),
                                    false),
                            false);
                case ARSHIFT:

                    return bvmgr.toIntegerFormula(
                            bvmgr.shiftRight(
                                    bvmgr.makeBitvector(bitWidth, i1),
                                    bvmgr.makeBitvector(bitWidth, i2),
                                    true),
                            false);
                case SREM:
                case UREM:
                    IntegerFormula zero = imgr.makeNumber(0);
                    IntegerFormula modulo = imgr.modulo(i1, i2);
                    BooleanFormula cond = booleanFormulaManager.and(
                            imgr.distinct(asList(modulo, zero)),
                            imgr.lessThan(i1, zero));
                    return booleanFormulaManager.ifThenElse(cond, imgr.subtract(modulo, i2), modulo);
                default:
                    throw new UnsupportedOperationException("Encoding of IntBinaryOp operation " + iBin.getKind() + " not supported on integer formulas.");
            }
        } else if (lhs instanceof BitvectorFormula bv1 && rhs instanceof BitvectorFormula bv2) {
            BitvectorFormulaManager bvmgr = bitvectorFormulaManager;
            return switch (iBin.getKind()) {
                case ADD -> bvmgr.add(bv1, bv2);
                case SUB -> bvmgr.subtract(bv1, bv2);
                case MUL -> bvmgr.multiply(bv1, bv2);
                case DIV -> bvmgr.divide(bv1, bv2, true);
                case UDIV -> bvmgr.divide(bv1, bv2, false);
                case SREM -> bvmgr.remainder(bv1, bv2, true);
                case UREM -> bvmgr.remainder(bv1, bv2, false);
                case AND -> bvmgr.and(bv1, bv2);
                case OR -> bvmgr.or(bv1, bv2);
                case XOR -> bvmgr.xor(bv1, bv2);
                case LSHIFT -> bvmgr.shiftLeft(bv1, bv2);
                case RSHIFT -> bvmgr.shiftRight(bv1, bv2, false);
                case ARSHIFT -> bvmgr.shiftRight(bv1, bv2, true);
            };
        } else {
            throw new UnsupportedOperationException("Encoding of IntBinaryOp operation " + iBin.getKind() + " not supported on formulas of mismatching type.");
        }
    }

    @Override
    public Formula visitIntSizeCastExpression(IntSizeCast expr) {
        Formula inner = encode(expr.getOperand());
        if (inner instanceof IntegerFormula || expr.isNoop()) {
            //TODO If narrowing, constrain the value.
            return inner;
        }

        if (inner instanceof BitvectorFormula number) {
            final int targetBitWidth = expr.getTargetType().getBitWidth();
            final int sourceBitWidth = expr.getSourceType().getBitWidth();
            assert (sourceBitWidth == bitvectorFormulaManager.getLength(number));

            if (expr.isExtension()) {
                return bitvectorFormulaManager.extend(number, targetBitWidth - sourceBitWidth, expr.preservesSign());
            } else {
                return bitvectorFormulaManager.extract(number, targetBitWidth - 1, 0);
            }
        }

        throw new UnsupportedOperationException(String.format("Encoding of (%s) not supported.", expr));
    }

    @Override
    public Formula visitIntUnaryExpression(IntUnaryExpr iUn) {
        Formula inner = encode(iUn.getOperand());
        switch (iUn.getKind()) {
            case MINUS -> {
                if (inner instanceof IntegerFormula number) {
                    return integerFormulaManager().negate(number);
                }
                if (inner instanceof BitvectorFormula number) {
                    return bitvectorFormulaManager.negate(number);
                }
            }
            case CTLZ -> {
                if (inner instanceof BitvectorFormula bv) {
                    BitvectorFormulaManager bvmgr = bitvectorFormulaManager;
                    // enc = extract(bv, 63, 63) == 1 ? 0 : (extract(bv, 62, 62) == 1 ? 1 : extract ... extract(bv, 0, 0) == 1 ? 63 : 64)
                    int bvLength = bvmgr.getLength(bv);
                    BitvectorFormula bv1 = bvmgr.makeBitvector(1, 1);
                    BitvectorFormula enc = bvmgr.makeBitvector(bvLength, bvLength);
                    for(int i = bvLength - 1; i >= 0; i--) {
                        BitvectorFormula bvi = bvmgr.makeBitvector(bvLength, i);
                        BitvectorFormula bvbit = bvmgr.extract(bv, bvLength - (i + 1), bvLength - (i + 1));
                        enc = booleanFormulaManager.ifThenElse(bvmgr.equal(bvbit, bv1), bvi, enc);
                    }
                    return enc;
                }
            }
            case CTTZ -> {
                if (inner instanceof BitvectorFormula bv) {
                    BitvectorFormulaManager bvmgr = bitvectorFormulaManager;
                    // enc = extract(bv, 0, 0) == 1 ? 0 : (extract(bv, 1, 1) == 1 ? 1 : extract ... extract(bv, 63, 63) == 1? 63 : 64)
                    int bvLength = bvmgr.getLength(bv);
                    BitvectorFormula bv1 = bvmgr.makeBitvector(1, 1);
                    BitvectorFormula enc = bvmgr.makeBitvector(bvLength, bvLength);
                    for(int i = bvLength - 1; i >= 0; i--) {
                        BitvectorFormula bvi = bvmgr.makeBitvector(bvLength, i);
                        BitvectorFormula bvbit = bvmgr.extract(bv, i, i);
                        enc = booleanFormulaManager.ifThenElse(bvmgr.equal(bvbit, bv1), bvi, enc);
                    }
                    return enc;
                }
            }
        }
        throw new UnsupportedOperationException(
                String.format("Encoding of (%s) %s %s not supported.", iUn.getType(), iUn.getKind(), inner));
    }

    @Override
    public Formula visitITEExpression(ITEExpr iteExpr) {
        BooleanFormula guard = encodeAsBoolean(iteExpr.getCondition());
        Formula tBranch = encode(iteExpr.getTrueCase());
        Formula fBranch = encode(iteExpr.getFalseCase());
        return booleanFormulaManager.ifThenElse(guard, tBranch, fBranch);
    }

    @Override
    public Formula visitConstructExpression(ConstructExpr construct) {
        final List<Formula> elements = new ArrayList<>();
        for (Expression inner : construct.getOperands()) {
            elements.add(encode(inner));
        }
        return context.getTupleFormulaManager().makeTuple(elements);
    }

    @Override
    public Formula visitAggregateCmpExpression(AggregateCmpExpr expr) {
        final Formula left = encode(expr.getLeft());
        final Formula right = encode(expr.getRight());
        final BooleanFormula eq = context.equal(left, right);
        return switch (expr.getKind())
        {
            case EQ -> eq;
            case NEQ -> context.getBooleanFormulaManager().not(eq);
        };
    }

    @Override
    public Formula visitExtractExpression(ExtractExpr extract) {
        final TupleFormula inner = (TupleFormula) encode(extract.getOperand());
        return context.getTupleFormulaManager().extract(inner, extract.getIndices());
    }

    @Override
    public Formula visitInsertExpression(InsertExpr insert) {
        final TupleFormula agg = (TupleFormula) encode(insert.getAggregate());
        final Formula value = encode(insert.getInsertedValue());
        return context.getTupleFormulaManager().insert(agg, value, insert.getIndices());
    }

    @Override
    public Formula visitRegister(Register reg) {
        String name = event == null ?
                reg.getName() + "_" + reg.getFunction().getId() + "_final" :
                reg.getName() + "(" + event.getGlobalId() + ")";
        Type type = reg.getType();
        return context.makeVariable(name, type);
    }

    @Override
    public Formula visitMemoryObject(MemoryObject memObj) {
        return context.address(memObj);
    }

    @Override
    public Formula visitFinalMemoryValue(FinalMemoryValue val) {
        checkState(event == null, "Cannot evaluate final memory value of %s at event %s.", val, event);
        int size = types.getMemorySizeInBits(val.getType());
        return context.lastValue(val.getMemoryObject(), val.getOffset(), size);
    }

//    @Override
//    public Formula visitFinalMemoryValue(FinalMemoryValue val) {
//        checkState(event == null, "Cannot evaluate final memory value of %s at event %s.", val, event);
//        int size = types.getMemorySizeInBits(val.getType());
//        return context.lastValue(val.getMemoryObject(), val.getOffset(), size);
//    }


    @Override
    public Formula visitPtrCmpExpression(PtrCmpExpr expr) {
        final Formula left = encode(expr.getLeft());
        final Formula right = encode(expr.getRight());
        return switch (expr.getKind())
        {
            case EQ -> context.equal(left, right);
            case NEQ -> context.getBooleanFormulaManager().not(context.equal(left, right));
        }; }

    public Formula visitPtrAddOffsetExpression(PtrAddOffsetExpr expr) {
        if (context.useBVPointers){
            return bitvectorFormulaManager.add((BitvectorFormula) encode(expr.getBasePointerVal()), (BitvectorFormula) encode(expr.getAddedOffset()));
        }


        final TupleFormula base_pointer =(TupleFormula) encode(expr.getBasePointerVal()); //(base,offset)
        BitvectorFormula added_offset = (BitvectorFormula) encode(expr.getAddedOffset());// offset to be added
        BitvectorFormula new_offset = bitvectorFormulaManager.add((BitvectorFormula) tupleFormulaManager.extract(base_pointer,1),added_offset);
        return tupleFormulaManager.insert(base_pointer,new_offset,List.of(1));
    }
    @Override
    public Formula visitPtrToIntCastExpression(PtrToIntCast expr) {
        if (context.useBVPointers){
            return encode(expr.getOperand());
        }
        TupleFormula encoded =(TupleFormula) encode(expr.getOperand());
        BitvectorFormula base =(BitvectorFormula) tupleFormulaManager.extract(encoded,0);
        BitvectorFormula offset =(BitvectorFormula) tupleFormulaManager.extract(encoded,1);
        return bitvectorFormulaManager.add(base,offset);
    }

    @Override
    public Formula visitIntToPtrCastExpression(IntToPtrCast expr){
        if (context.useBVPointers){
            BitvectorFormula int_addr = (BitvectorFormula) encode(expr.getOperand());
            return int_addr;
        }
        BitvectorFormulaManager bvfm = bitvectorFormulaManager;
        BooleanFormulaManager bfm = booleanFormulaManager;
        BitvectorFormula int_addr = (BitvectorFormula) encode(expr.getOperand());
        BitvectorFormula baseE = bvfm.makeBitvector(64,0);
        BitvectorFormula offsetE = int_addr;
        for (MemoryObject obj : context.getTask().getProgram().getMemory().getObjects()) {
            BitvectorFormula object_base =(BitvectorFormula) context.baseAddress(obj);
            BitvectorFormula object_size =(BitvectorFormula) context.size(obj);
            BooleanFormula located = bfm.and(bvfm.greaterOrEquals(int_addr,object_base,false),bvfm.lessThan(int_addr,bvfm.add(object_base,object_size),false));

            baseE = bfm.ifThenElse(located,object_base,baseE);
            offsetE = bfm.ifThenElse(located,bvfm.subtract(int_addr, object_base),offsetE);

        }
        return  tupleFormulaManager.makeTuple(List.of(baseE,offsetE))  ;
    }


    @Override
    public Formula visitNullPointerLiteral(NullLiteral nullptr){
        if (context.useBVPointers){
            return context.getBitvectorFormulaManager().makeBitvector(TypeFactory.getInstance().getArchType().getBitWidth(),BigInteger.ZERO);
        }
        BitvectorFormula zero = context.getBitvectorFormulaManager().makeBitvector(TypeFactory.getInstance().getArchType().getBitWidth(),BigInteger.ZERO);
        return tupleFormulaManager.makeTuple(List.of(zero,zero));
    }
}
