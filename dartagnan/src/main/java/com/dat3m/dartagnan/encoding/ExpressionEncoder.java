package com.dat3m.dartagnan.encoding;

import com.dat3m.dartagnan.expression.*;
import com.dat3m.dartagnan.expression.aggregates.AggregateCmpExpr;
import com.dat3m.dartagnan.expression.aggregates.ConstructExpr;
import com.dat3m.dartagnan.expression.aggregates.ExtractExpr;
import com.dat3m.dartagnan.expression.aggregates.InsertExpr;
import com.dat3m.dartagnan.expression.booleans.BoolBinaryExpr;
import com.dat3m.dartagnan.expression.booleans.BoolLiteral;
import com.dat3m.dartagnan.expression.booleans.BoolUnaryExpr;
import com.dat3m.dartagnan.expression.booleans.BoolUnaryOp;
import com.dat3m.dartagnan.expression.integers.*;
import com.dat3m.dartagnan.expression.misc.ITEExpr;
import com.dat3m.dartagnan.expression.pointers.*;
import com.dat3m.dartagnan.expression.type.*;
import com.dat3m.dartagnan.expression.utils.ExpressionHelper;
import com.dat3m.dartagnan.program.Register;
import com.dat3m.dartagnan.program.event.Event;
import com.dat3m.dartagnan.program.memory.FinalMemoryValue;
import com.dat3m.dartagnan.program.memory.MemoryObject;
import com.dat3m.dartagnan.program.misc.NonDetValue;
import com.dat3m.dartagnan.smt.FormulaManagerExt;
import com.dat3m.dartagnan.smt.TupleFormula;
import com.dat3m.dartagnan.smt.TupleFormulaManager;
import com.google.common.base.Preconditions;
import org.sosy_lab.java_smt.api.*;
import org.sosy_lab.java_smt.api.NumeralFormula.IntegerFormula;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;

/*
    This class is responsible for doing all encoding related to IR types, in particular, all kinds of expressions.
 */
public class ExpressionEncoder {

    private static final TypeFactory types = TypeFactory.getInstance();

    private final EncodingContext context;
    private final FormulaManagerExt fmgr;
    private final BooleanFormulaManager bmgr;
    private final IntegerFormulaManager imgr;
    private final BitvectorFormulaManager bvmgr;
    private final TupleFormulaManager tmgr;
    private final ExpressionFactory ef;
    private final Visitor visitor = new Visitor();

    ExpressionEncoder(EncodingContext context) {
        this.context = context;
        this.fmgr = context.getFormulaManager();
        this.bmgr = fmgr.getBooleanFormulaManager();
        this.imgr = fmgr.getIntegerFormulaManager();
        this.bvmgr = fmgr.getBitvectorFormulaManager();
        this.tmgr = fmgr.getTupleFormulaManager();
        this.ef = context.getExpressionFactory();

    }

    private IntegerFormulaManager integerFormulaManager() {
        return fmgr.getIntegerFormulaManager();
    }

    private BitvectorFormulaManager bitvectorFormulaManager() {
        return fmgr.getBitvectorFormulaManager();
    }

    // ====================================================================================
    // Public API

    public TypedFormula<?, ?> encodeAt(Expression expression, Event at) {
        Preconditions.checkNotNull(at);
        visitor.setEvent(at);
        return expression.accept(visitor);
    }

    public TypedFormula<?, ?> encodeFinal(Expression expression) {
        visitor.setEvent(null);
        return expression.accept(visitor);
    }

    @SuppressWarnings("unchecked")
    public TypedFormula<BooleanType, BooleanFormula> encodeBooleanAt(Expression expression, Event at) {
        checkArgument(expression.getType() instanceof BooleanType);
        return (TypedFormula<BooleanType, BooleanFormula>) encodeAt(expression, at);
    }

    @SuppressWarnings("unchecked")
    public TypedFormula<BooleanType, BooleanFormula> encodeBooleanFinal(Expression expression) {
        checkArgument(expression.getType() instanceof BooleanType);
        return (TypedFormula<BooleanType, BooleanFormula>) encodeFinal(expression);
    }

    public <TType extends Type> TypedFormula<TType, ?> makeVariable(String name, TType type) {
        Formula variable = null;
        if (type instanceof BooleanType) {
            variable = bmgr.makeVariable(name);
        } else if (type instanceof IntegerType integerType) {
            variable = context.useIntegers
                    ? tmgr.makeTuple(imgr.makeNumber(0),imgr.makeVariable(name))
                    : tmgr.makeTuple(bvmgr.makeBitvector(integerType.getBitWidth(),BigInteger.ZERO),bvmgr.makeVariable(integerType.getBitWidth(), name)); // FIXME swap first one to arch type
        } else if (type instanceof PointerType pointerType) {
            switch (context.provenance) {
                case SIMPLE -> {
                    final Formula base = context.useIntegers
                            ? integerFormulaManager().makeVariable(name + "_base")
                            : bitvectorFormulaManager().makeVariable(types.getArchType().getBitWidth(), name + "_base");
                    final Formula offset = context.useIntegers
                            ? integerFormulaManager().makeVariable(name + "_offset")
                            : bitvectorFormulaManager().makeVariable(types.getArchType().getBitWidth(), name + "_offset");
                    variable = fmgr.getTupleFormulaManager().makeTuple(base, offset);
                }
                case PLAIN -> { // FIXME this is redundant but the name is important
                    final Formula base = context.useIntegers
                            ? integerFormulaManager().makeVariable(name + "_base")
                            : bitvectorFormulaManager().makeVariable(types.getArchType().getBitWidth(), name + "_base");
                    final Formula address = context.useIntegers
                            ? integerFormulaManager().makeVariable(name + "_address")
                            : bitvectorFormulaManager().makeVariable(types.getArchType().getBitWidth(), name + "_address");
                    variable = fmgr.getTupleFormulaManager().makeTuple(base, address);
                }
                case COMPLETE -> {
                    final Formula base = context.useIntegers
                            ? integerFormulaManager().makeVariable(name + "_base")
                            : bitvectorFormulaManager().makeVariable(types.getArchType().getBitWidth(), name + "_base");
                    final Formula offset = context.useIntegers
                            ? integerFormulaManager().makeVariable(name + "_offset")
                            : bitvectorFormulaManager().makeVariable(types.getArchType().getBitWidth(), name + "_offset");
                    final Formula address = context.useIntegers
                            ? integerFormulaManager().makeVariable(name + "_address")
                            : bitvectorFormulaManager().makeVariable(types.getArchType().getBitWidth(), name + "_address");
                    variable = fmgr.getTupleFormulaManager().makeTuple(base, offset, address);
                }
            }
        } else if (type instanceof AggregateType || type instanceof ArrayType) {
            final Map<Integer, Type> primitives = types.decomposeIntoPrimitives(type);
            if (primitives != null) {
                final List<Formula> elements = new ArrayList<>();
                for (Map.Entry<Integer, Type> entry : primitives.entrySet()) {
                    elements.add(makeVariable(name + "@" + entry.getKey(), entry.getValue()).formula());
                }
                variable = fmgr.getTupleFormulaManager().makeTuple(elements);
            }
        }

        if (variable == null) {
            throw new UnsupportedOperationException(String.format("Cannot make variable of type %s.", type));
        }
        return new TypedFormula<>(type, variable);
    }

    public TypedFormula<BooleanType, BooleanFormula> wrap(BooleanFormula formula) {
        return new TypedFormula<>(types.getBooleanType(), formula);
    }

    public <TType extends Type, TFormula extends Formula> TypedFormula<TType, TFormula> wrap(TType type, TFormula formula) {
        return new TypedFormula<>(type, formula);
    }

    // ====================================================================================
    // Utility

    // TODO: For conversion operations, we might want to have an universal intermediate type T with the following properties:
    //  (1) every other type has a lossless conversion to T
    //  (2) T can be converted to every other type (possibly with loss)
    //  (3) A round-trip through T is always lossless.
    //  See comments on TypedFormula class for more details.
    public enum ConversionMode {
        NO,
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT,
    }

    public BooleanFormula equal(Expression left, Expression right, ConversionMode cMode) {
        switch (cMode) {
            case NO -> {}
            case LEFT_TO_RIGHT -> left = ef.makeCast(left, right.getType());
            case RIGHT_TO_LEFT -> right = ef.makeCast(right, left.getType());
        }
        return encodeBooleanFinal(ef.makeEQ(left, right)).formula();
    }

    public BooleanFormula equal(Expression left, Expression right) {
        return equal(left, right, ConversionMode.NO);
    }

    public BooleanFormula equalAt(Expression left, Event leftAt, Expression right, Event rightAt, ConversionMode cMode) {
        return equal(encodeAt(left, leftAt), encodeAt(right, rightAt), cMode);
    }

    public BooleanFormula equalAt(Expression left, Event leftAt, Expression right, Event rightAt) {
        return equal(encodeAt(left, leftAt), encodeAt(right, rightAt));
    }

    // ====================================================================================
    // Private implementation

    // TODO: We can probably just return plain formulas and let the outer class
    //  wrap them correctly.
    private class Visitor implements ExpressionVisitor<TypedFormula<?, ?>> {

        private Event event;
        public void setEvent(Event e) {
            this.event = e;
        }

        public TypedFormula<?, ?> encode(Expression expression) {
            return expression.accept(this);
        }

        @SuppressWarnings("unchecked")
        public TypedFormula<IntegerType, ?> encodeIntegerExpr(Expression expression) {
            checkArgument(expression.getType() instanceof IntegerType);
            final TypedFormula<?, ?> typedFormula = encode(expression);
            assert typedFormula.getType() == expression.getType();
            assert typedFormula.formula() instanceof TupleFormula;
            return (TypedFormula<IntegerType, ?>) typedFormula;
        }

        @SuppressWarnings("unchecked")
        public TypedFormula<PointerType, ?> encodePointerExpr(Expression expression) {
            checkArgument(expression.getType() instanceof PointerType);
            final TypedFormula<?, ?> typedFormula = encode(expression);
            assert typedFormula.type() == expression.getType();
            switch (context.provenance) {
                case SIMPLE,PLAIN -> {
                    assert typedFormula.formula() instanceof TupleFormula tuple && tuple.getSize() == 2;
                }
                case COMPLETE -> {
                    assert typedFormula.formula() instanceof TupleFormula tuple && tuple.getSize() == 3;
                }
            }
            return (TypedFormula<PointerType, ?>) typedFormula;
        }

        @SuppressWarnings("unchecked")
        public TypedFormula<BooleanType, BooleanFormula> encodeBooleanExpr(Expression expression) {
            checkArgument(expression.getType() instanceof BooleanType);
            final TypedFormula<?, ?> typedFormula = encode(expression);
            assert typedFormula.getType() == expression.getType();
            assert typedFormula.formula() instanceof BooleanFormula;
            return (TypedFormula<BooleanType, BooleanFormula>) typedFormula;
        }

        @SuppressWarnings("unchecked")
        public TypedFormula<?, TupleFormula> encodeAggregateExpr(Expression expression) {
            checkArgument(ExpressionHelper.isAggregateLike(expression));
            final TypedFormula<?, ?> typedFormula = encode(expression);
            assert typedFormula.getType() == expression.getType();
            assert typedFormula.formula() instanceof TupleFormula;
            return (TypedFormula<?, TupleFormula>) typedFormula;
        }

        @Override
        public TypedFormula<?, ?> visitLeafExpression(LeafExpression expr) {
            if (expr instanceof TypedFormula<?, ?> typedFormula) {
                return typedFormula;
            }
            return visitExpression(expr);
        }

        // ====================================================================================
        // Booleans

        @Override
        public TypedFormula<BooleanType, BooleanFormula> visitBoolLiteral(BoolLiteral boolLiteral) {
            return new TypedFormula<>(types.getBooleanType(), bmgr.makeBoolean(boolLiteral.getValue()));
        }

        @Override
        public TypedFormula<BooleanType, BooleanFormula> visitBoolBinaryExpression(BoolBinaryExpr bBin) {
            final TypedFormula<BooleanType, BooleanFormula> lhs = encodeBooleanExpr(bBin.getLeft());
            final TypedFormula<BooleanType, BooleanFormula> rhs = encodeBooleanExpr(bBin.getRight());
            final BooleanFormula result = switch (bBin.getKind()) {
                case AND -> bmgr.and(lhs.formula(), rhs.formula());
                case OR -> bmgr.or(lhs.formula(), rhs.formula());
                case IFF -> bmgr.equivalence(lhs.formula(), rhs.formula());
            };
            return new TypedFormula<>(types.getBooleanType(), result);
        }

        @Override
        public TypedFormula<BooleanType, BooleanFormula> visitBoolUnaryExpression(BoolUnaryExpr bUn) {
            final TypedFormula<BooleanType, BooleanFormula> inner = encodeBooleanExpr(bUn.getOperand());
            assert bUn.getKind() == BoolUnaryOp.NOT;
            return new TypedFormula<>(types.getBooleanType(), bmgr.not(inner.formula()));
        }

        // ====================================================================================
        // Integers

        @Override
        public TypedFormula<IntegerType, ?> visitIntLiteral(IntLiteral intLiteral) {
            final Formula result = context.useIntegers
                    ? tmgr.makeTuple(imgr.makeNumber(BigInteger.ZERO),imgr.makeNumber(intLiteral.getValue()))
                    : tmgr.makeTuple(bvmgr.makeBitvector(intLiteral.getType().getBitWidth(),BigInteger.ZERO),bvmgr.makeBitvector(intLiteral.getType().getBitWidth(), intLiteral.getValue()));
            return new TypedFormula<>(intLiteral.getType(), result);
        }

        @Override
        public TypedFormula<IntegerType, ?> visitIntBinaryExpression(IntBinaryExpr iBin) {
            final TypedFormula<IntegerType, ?> lhs = encodeIntegerExpr(iBin.getLeft());
            final TypedFormula<IntegerType, ?> rhs = encodeIntegerExpr(iBin.getRight());
            final IntegerType type = iBin.getType();
            final int bitWidth = type.getBitWidth();
            // Integers are encoded as (provenance info , value)
            if (context.useIntegers) {
                final IntegerFormula i1_p = (IntegerFormula) ((TupleFormula) lhs.formula()).first();
                final IntegerFormula i1_v = (IntegerFormula)((TupleFormula) lhs.formula()).second();
                final IntegerFormula i2_v = (IntegerFormula)((TupleFormula) rhs.formula()).second();

                final TupleFormula result = switch (iBin.getKind()) {
                    case ADD -> tmgr.makeTuple(i1_p,imgr.add(i1_v, i2_v));
                    case SUB -> tmgr.makeTuple(i1_p,imgr.subtract(i1_v, i2_v));
                    case MUL -> tmgr.makeTuple(i1_p,imgr.multiply(i1_v, i2_v));
                    case DIV, UDIV -> tmgr.makeTuple(i1_p,imgr.divide(i1_v, i2_v));
                    case AND -> {
                        final BitvectorFormula resultBv = bvmgr.and(bvmgr.makeBitvector(bitWidth, i1_v), bvmgr.makeBitvector(bitWidth, i2_v));
                        yield tmgr.makeTuple(i1_p ,bvmgr.toIntegerFormula(resultBv, false));
                    }
                    case OR -> {
                        final BitvectorFormula resultBv = bvmgr.or(bvmgr.makeBitvector(bitWidth, i1_v), bvmgr.makeBitvector(bitWidth, i2_v));
                        yield tmgr.makeTuple(i1_p,bvmgr.toIntegerFormula(resultBv, false));
                    }
                    case XOR -> {
                        final BitvectorFormula resultBv = bvmgr.xor(bvmgr.makeBitvector(bitWidth, i1_v), bvmgr.makeBitvector(bitWidth, i2_v));
                        yield tmgr.makeTuple(i1_p,bvmgr.toIntegerFormula(resultBv, false));
                    }
                    case LSHIFT -> {
                        final BitvectorFormula resultBv = bvmgr.shiftLeft(bvmgr.makeBitvector(bitWidth, i1_v), bvmgr.makeBitvector(bitWidth, i2_v));
                        yield tmgr.makeTuple(i1_p,bvmgr.toIntegerFormula(resultBv, false));
                    }
                    case RSHIFT -> {
                        final BitvectorFormula resultBv = bvmgr.shiftRight(bvmgr.makeBitvector(bitWidth, i1_v), bvmgr.makeBitvector(bitWidth, i2_v), false);
                        yield tmgr.makeTuple(i1_p,bvmgr.toIntegerFormula(resultBv, false));
                    }
                    case ARSHIFT -> {
                        final BitvectorFormula resultBv = bvmgr.shiftRight(bvmgr.makeBitvector(bitWidth, i1_v), bvmgr.makeBitvector(bitWidth, i2_v), true);
                        yield tmgr.makeTuple(i1_p,bvmgr.toIntegerFormula(resultBv, false));
                    }
                    case SREM, UREM -> {
                        final IntegerFormula zero = imgr.makeNumber(0);
                        final IntegerFormula modulo = imgr.modulo(i1_v, i2_v);
                        final BooleanFormula cond = bmgr.and(imgr.distinct(asList(modulo, zero)), imgr.lessThan(i1_v, zero));
                        yield tmgr.makeTuple(i1_p,fmgr.ifThenElse(cond, imgr.subtract(modulo, i2_v), modulo));
                    }
                };
                return new TypedFormula<>(type, result);
            } else {
                final BitvectorFormula bv1_p = (BitvectorFormula) ((TupleFormula) lhs.formula()).first();
                final BitvectorFormula bv1_v = (BitvectorFormula) ((TupleFormula) lhs.formula()).second();
                final BitvectorFormula bv2_v = (BitvectorFormula)((TupleFormula) rhs.formula()).second();
                final TupleFormula result = switch (iBin.getKind()) {
                    case ADD -> tmgr.makeTuple(bv1_p,bvmgr.add(bv1_v, bv2_v));
                    case SUB -> tmgr.makeTuple(bv1_p,bvmgr.subtract(bv1_v, bv2_v));
                    case MUL -> tmgr.makeTuple(bv1_p,bvmgr.multiply(bv1_v, bv2_v));
                    case DIV -> tmgr.makeTuple(bv1_p,bvmgr.divide(bv1_v, bv2_v, true));
                    case UDIV -> tmgr.makeTuple(bv1_p,bvmgr.divide(bv1_v, bv2_v, false));
                    case SREM -> tmgr.makeTuple(bv1_p,bvmgr.remainder(bv1_v, bv2_v, true));
                    case UREM -> tmgr.makeTuple(bv1_p,bvmgr.remainder(bv1_v, bv2_v, false));
                    case AND -> tmgr.makeTuple(bv1_p,bvmgr.and(bv1_v, bv2_v));
                    case OR -> tmgr.makeTuple(bv1_p,bvmgr.or(bv1_v, bv2_v));
                    case XOR -> tmgr.makeTuple(bv1_p,bvmgr.xor(bv1_v, bv2_v));
                    case LSHIFT -> tmgr.makeTuple(bv1_p,bvmgr.shiftLeft(bv1_v, bv2_v));
                    case RSHIFT -> tmgr.makeTuple(bv1_p,bvmgr.shiftRight(bv1_v, bv2_v, false));
                    case ARSHIFT -> tmgr.makeTuple(bv1_p,bvmgr.shiftRight(bv1_v, bv2_v, true));
                };
                return new TypedFormula<>(type, result);
            }
        }

        @Override
        public TypedFormula<IntegerType, ?> visitIntSizeCastExpression(IntSizeCast expr) {
            final TypedFormula<IntegerType, ?> inner = encodeIntegerExpr(expr.getOperand());

            if (expr.isNoop()) {
                return inner;
            } else if (context.useIntegers) {
                //TODO If narrowing, constrain the value.
                return new TypedFormula<>(expr.getType(), inner.formula());
            } else {
                assert inner.formula() instanceof TupleFormula;
                final TupleFormula formula = (TupleFormula) inner.formula();
                final BitvectorFormula innerBv =(BitvectorFormula) formula.second();
                final int targetBitWidth = expr.getTargetType().getBitWidth();
                final int sourceBitWidth = expr.getSourceType().getBitWidth();
                assert (sourceBitWidth == bvmgr.getLength(innerBv));

                final BitvectorFormula resultBv = expr.isExtension()
                        ? bvmgr.extend(innerBv, targetBitWidth - sourceBitWidth, expr.preservesSign())
                        : bvmgr.extract(innerBv, targetBitWidth - 1, 0);
                return new TypedFormula<IntegerType, Formula>(expr.getType(), tmgr.insert(formula,resultBv,1));
            }
        }

        @Override
        public TypedFormula<IntegerType, ?> visitIntUnaryExpression(IntUnaryExpr iUn) {
            final TypedFormula<IntegerType, ?> inner = encodeIntegerExpr(iUn.getOperand());
            final TupleFormula formula = (TupleFormula) inner.formula();
            if (context.useIntegers) {
                final IntegerFormula innerForm = (IntegerFormula) formula.second();
                final IntegerFormula resultVal = switch (iUn.getKind()) {
                    case MINUS -> imgr.negate(innerForm);
                    default ->
                            throw new UnsupportedOperationException("Unsupported operation on mathematical integers: " + iUn.getKind());
                };
                return new TypedFormula<IntegerType, Formula>(iUn.getType(), tmgr.insert(formula,resultVal,1));
            } else {
                final BitvectorFormula bv = (BitvectorFormula) formula.second();
                final BitvectorFormula resultVal = switch (iUn.getKind()) {
                    case MINUS -> bvmgr.negate(bv);
                    case CTLZ -> {
                        final int bvLength = bvmgr.getLength(bv);
                        final BitvectorFormula bv1 = bvmgr.makeBitvector(1, 1);
                        // enc = extract(bv, 63, 63) == 1 ? 0 : (extract(bv, 62, 62) == 1 ? 1 : extract ... extract(bv, 0, 0) == 1 ? 63 : 64)
                        BitvectorFormula ctlz = bvmgr.makeBitvector(bvLength, bvLength);
                        for (int i = bvLength - 1; i >= 0; i--) {
                            BitvectorFormula bvi = bvmgr.makeBitvector(bvLength, i);
                            BitvectorFormula bvbit = bvmgr.extract(bv, bvLength - (i + 1), bvLength - (i + 1));
                            ctlz = fmgr.ifThenElse(bvmgr.equal(bvbit, bv1), bvi, ctlz);
                        }
                        yield ctlz;
                    }
                    case CTTZ -> {
                        final int bvLength = bvmgr.getLength(bv);
                        final BitvectorFormula bv1 = bvmgr.makeBitvector(1, 1);
                        // enc = extract(bv, 0, 0) == 1 ? 0 : (extract(bv, 1, 1) == 1 ? 1 : extract ... extract(bv, 63, 63) == 1? 63 : 64)
                        BitvectorFormula cttz = bvmgr.makeBitvector(bvLength, bvLength);
                        for (int i = bvLength - 1; i >= 0; i--) {
                            BitvectorFormula bvi = bvmgr.makeBitvector(bvLength, i);
                            BitvectorFormula bvbit = bvmgr.extract(bv, i, i);
                            cttz = fmgr.ifThenElse(bvmgr.equal(bvbit, bv1), bvi, cttz);
                        }
                        yield cttz;
                    }
                };
                return new TypedFormula<IntegerType, Formula>(iUn.getType(), tmgr.insert(formula,resultVal,1));
            }
        }

        @Override
        public TypedFormula<BooleanType, BooleanFormula> visitIntCmpExpression(IntCmpExpr cmp) {
            final TypedFormula<?, ?> lhs = encode(cmp.getLeft());
            final TypedFormula<?, ?> rhs = encode(cmp.getRight());
            final IntCmpOp op = cmp.getKind();

            if (context.useIntegers) {
                final IntegerFormula l = (IntegerFormula)((TupleFormula) lhs.formula()).second();
                final IntegerFormula r = (IntegerFormula)((TupleFormula) rhs.formula()).second();
                final BooleanFormula result = switch (op) {
                    case EQ -> imgr.equal(l, r);
                    case NEQ -> bmgr.not(imgr.equal(l, r));
                    case LT, ULT -> imgr.lessThan(l, r);
                    case LTE, ULTE -> imgr.lessOrEquals(l, r);
                    case GT, UGT -> imgr.greaterThan(l, r);
                    case GTE, UGTE -> imgr.greaterOrEquals(l, r);
                };
                return new TypedFormula<>(types.getBooleanType(), result);
            } else {
                final BitvectorFormula l = (BitvectorFormula)((TupleFormula) lhs.formula()).second();
                final BitvectorFormula r = (BitvectorFormula)((TupleFormula) rhs.formula()).second();
                final boolean isSigned = op.isSigned();
                final BooleanFormula result = switch (op) {
                    case EQ -> bvmgr.equal(l, r);
                    case NEQ -> bmgr.not(bvmgr.equal(l, r));
                    case LT, ULT -> bvmgr.lessThan(l, r, isSigned);
                    case LTE, ULTE -> bvmgr.lessOrEquals(l, r, isSigned);
                    case GT, UGT -> bvmgr.greaterThan(l, r, isSigned);
                    case GTE, UGTE -> bvmgr.greaterOrEquals(l, r, isSigned);
                };
                return new TypedFormula<>(types.getBooleanType(), result);
            }
        }

        // ====================================================================================
        // Aggregates

        @Override
        public TypedFormula<?, TupleFormula> visitConstructExpression(ConstructExpr construct) {
            final List<Formula> elements = new ArrayList<>();
            for (Expression inner : construct.getOperands()) {
                elements.add(encode(inner).formula());
            }
            return new TypedFormula<>(construct.getType(), fmgr.getTupleFormulaManager().makeTuple(elements));
        }

        @Override
        public TypedFormula<BooleanType, BooleanFormula> visitAggregateCmpExpression(AggregateCmpExpr expr) {
            final TypedFormula<?, TupleFormula> left = encodeAggregateExpr(expr.getLeft());
            final TypedFormula<?, TupleFormula> right = encodeAggregateExpr(expr.getRight());

            final BooleanFormula eq = fmgr.equal(left.formula(), right.formula());
            final BooleanFormula result = switch (expr.getKind()) {
                case EQ -> eq;
                case NEQ -> context.getBooleanFormulaManager().not(eq);
            };
            return new TypedFormula<>(types.getBooleanType(), result);
        }

        @Override
        public TypedFormula<?, ?> visitExtractExpression(ExtractExpr extract) {
            final TypedFormula<?, TupleFormula> inner = encodeAggregateExpr(extract.getOperand());
            final Formula extractForm = fmgr.getTupleFormulaManager().extract(inner.formula(), extract.getIndices());
            return new TypedFormula<>(extract.getType(), extractForm);
        }

        @Override
        public TypedFormula<?, TupleFormula> visitInsertExpression(InsertExpr insert) {
            final TupleFormula agg = encodeAggregateExpr(insert.getAggregate()).formula();
            final Formula value = encode(insert.getInsertedValue()).formula();
            final TupleFormula insertForm = fmgr.getTupleFormulaManager().insert(agg, value, insert.getIndices());
            return new TypedFormula<>(insert.getType(), insertForm);
        }

        // ====================================================================================
        // Misc

        @Override
        public TypedFormula<?, ?> visitITEExpression(ITEExpr iteExpr) {
            final BooleanFormula guard = encodeBooleanExpr(iteExpr.getCondition()).formula();
            final Formula tBranch = encode(iteExpr.getTrueCase()).formula();
            final Formula fBranch = encode(iteExpr.getFalseCase()).formula();
            final Formula ite = fmgr.ifThenElse(guard, tBranch, fBranch);
            return new TypedFormula<>(iteExpr.getType(), ite);
        }

        // ====================================================================================
        // Pointers

        @Override
        public TypedFormula<PointerType, ?> visitPointerAddExpression(PointerAddExpr expr) {
            final TypedFormula<PointerType, ?> base = encodePointerExpr(expr.getBase());
            final TypedFormula<IntegerType, ?> offset = encodeIntegerExpr(expr.getOffset());
            final TupleFormula baseTuple = (TupleFormula) base.formula();
            final TupleFormula offsetTuple = (TupleFormula) offset.formula();
            switch (context.provenance) {
                case SIMPLE, PLAIN -> {
                    assert baseTuple.getSize() == 2;
                    final Formula new_ = fmgr.add(baseTuple.second(), offsetTuple.second());
                    Formula f = tmgr.insert(baseTuple, new_, 1);
                    return new TypedFormula<>(base.getType(), f);
                }
                case COMPLETE -> {
                    assert baseTuple.getSize() == 3;
                    final Formula newOffset = fmgr.add(baseTuple.second(), offsetTuple.second());
                    final Formula newAdr = fmgr.add(baseTuple.third(), offsetTuple.second());
                    Formula f = tmgr.insert(tmgr.insert(baseTuple, newOffset, 1),newAdr,2);
                    return new TypedFormula<>(
                            base.getType(),f
                    );
                }
            }

            throw new UnsupportedOperationException("Unreachable");
        }

        @Override
        public TypedFormula<IntegerType, ?> visitPtrToIntCastExpression(PtrToIntCast expr) {
            final TypedFormula<PointerType, ?> ptr = encodePointerExpr(expr.getOperand());
            final TupleFormula formula = (TupleFormula) ptr.formula();
            return switch (context.provenance) {
                case SIMPLE -> encodeIntegerExpr(wrap(expr.getType(),tmgr.makeTuple(formula.first(),fmgr.add(formula.first(),formula.second()))));
                case PLAIN -> encodeIntegerExpr(wrap(expr.getType(), formula ));
                case COMPLETE -> encodeIntegerExpr(wrap(expr.getType(), tmgr.makeTuple(formula.first(),formula.third())));
            };
        }

        @Override
        public TypedFormula<PointerType, ?> visitIntToPtrCastExpression(IntToPtrCast expr) {
            final TypedFormula<IntegerType, ?> address = encodeIntegerExpr(expr.getOperand());
            final TupleFormula formula = (TupleFormula) address.formula();
            return switch (context.provenance) {
                case SIMPLE -> encodePointerExpr(wrap(expr.getType(), tmgr.makeTuple(formula.first(),fmgr.subtract(formula.second(),formula.first()))));
                case PLAIN -> encodePointerExpr(wrap(expr.getType(),formula));
                case COMPLETE -> encodePointerExpr(wrap(expr.getType(),
                        tmgr.makeTuple(formula.first(),fmgr.subtract(formula.second(),formula.first()),formula.third())
                ));
            };
        }

        @Override
        public TypedFormula<BooleanType, BooleanFormula> visitPtrCmpExpression(PtrCmpExpr expr) {
            final TypedFormula<PointerType, ?> left = encodePointerExpr(expr.getLeft());
            final TypedFormula<PointerType, ?> right = encodePointerExpr(expr.getRight());

            final BooleanFormula result = switch (expr.getKind()) {
                case EQ -> fmgr.equal(left.formula(), right.formula());
                case NEQ -> bmgr.not(fmgr.equal(left.formula(), right.formula()));
            };
            return new TypedFormula<>(types.getBooleanType(), result);
        }


        @Override
        public TypedFormula<BooleanType,BooleanFormula> visitPointerValidationExpression(PointerValidation pv){
            BooleanFormulaManager bfm = fmgr.getBooleanFormulaManager();
            return switch(context.provenance){
                case SIMPLE, COMPLETE -> {
                    final TupleFormula ptr = (TupleFormula) encodePointerExpr(pv.getOperand()).formula();
                    BooleanFormula valid = bfm.makeFalse();
                    final Set<MemoryObject> memoryObjects = context.getTask().getProgram().getMemory().getObjects();
                    for (MemoryObject memoryObject : memoryObjects) {
                        final TupleFormula mem = (TupleFormula) encodePointerExpr(memoryObject).formula();
                        final BitvectorFormula size  = (BitvectorFormula) ((TupleFormula)context.size(memoryObject).formula()).second();
                        BooleanFormula condition = bfm.and(fmgr.lessThan(ptr.second(), size),fmgr.equal(ptr.first(), mem.first()));
                        valid = bfm.or(valid, condition);
                    }
                    valid = bfm.and(valid,fmgr.greaterOrEquals(ptr.second(),fmgr.makeConstant(ptr.second(), BigInteger.ZERO)));
                    yield new TypedFormula<>(types.getBooleanType(), valid);}
                case PLAIN -> {
                    final TupleFormula ptr = (TupleFormula) encodePointerExpr(pv.getOperand()).formula();
                    BooleanFormula valid = bfm.makeFalse();
                    final Set<MemoryObject> memoryObjects = context.getTask().getProgram().getMemory().getObjects();
                    for (MemoryObject memoryObject : memoryObjects) {
                        final TupleFormula mem = (TupleFormula) encodePointerExpr(memoryObject).formula();
                        final BitvectorFormula size  = (BitvectorFormula) ((TupleFormula)context.size(memoryObject).formula()).second();
                        BooleanFormula condition = bfm.and(fmgr.lessThan(fmgr.subtract(ptr.second(),ptr.first()), size),fmgr.equal(ptr.first(), mem.first()));
                        valid = bfm.or(valid, condition);
                    }
                    valid = bfm.and(valid,fmgr.greaterOrEquals(ptr.second(),fmgr.makeConstant(ptr.second(), BigInteger.ZERO)));
                    yield new TypedFormula<>(types.getBooleanType(), valid);
                }
            };
        }


        @Override
        public TypedFormula<PointerType, ?> visitNullLiteral(NullLiteral lit) {
            final Formula zero = context.useIntegers ? imgr.makeNumber(0) : bvmgr.makeBitvector(types.getArchType().getBitWidth(), BigInteger.ZERO);
            return switch (context.provenance) {
                case SIMPLE , PLAIN -> new TypedFormula<>(lit.getType(), tmgr.makeTuple(zero, zero));
                case COMPLETE -> new TypedFormula<>(lit.getType(), tmgr.makeTuple(zero, zero, zero));
            };
        }

        // ====================================================================================
        // Program primitives

        @Override
        public TypedFormula<?, ?> visitNonDetValue(NonDetValue nonDet) {
            return makeVariable(nonDet.toString(), nonDet.getType());
        }

        @Override
        public TypedFormula<?, ?> visitRegister(Register reg) {
            final String name = event == null ?
                    reg.getName() + "_" + reg.getFunction().getId() + "_final" :
                    reg.getName() + "(" + event.getGlobalId() + ")";
            return makeVariable(name, reg.getType());
        }

        @Override
        public TypedFormula<PointerType, ?> visitMemoryObject(MemoryObject memObj) {
            Formula b = context.useIntegers ? imgr.makeVariable(String.format("baseof(%s)", memObj)): bvmgr.makeVariable(types.getArchType().getBitWidth(),String.format("baseof(%s)", memObj));
            final Formula result = switch (context.provenance) {
                case SIMPLE -> fmgr.getTupleFormulaManager().makeTuple(b,fmgr.makeConstant(b,BigInteger.ZERO));
                case PLAIN -> fmgr.getTupleFormulaManager().makeTuple(List.of(b,b));
                case COMPLETE -> fmgr.getTupleFormulaManager().makeTuple(List.of(b,fmgr.makeConstant(b,BigInteger.ZERO),b));
            };
            return new TypedFormula<>(memObj.getType(), result);
        }

        @Override
        public TypedFormula<?, ?> visitFinalMemoryValue(FinalMemoryValue val) {
            checkState(event == null, "Cannot evaluate final memory value of %s at event %s.", val, event);
            final MemoryObject base = val.getMemoryObject();
            final int offset = val.getOffset();
            checkArgument(base.isInRange(offset), "Array index out of bounds");
            final String name = String.format("last_val_at_%s_%d", base, offset);
            return makeVariable(name, val.getType());
        }
    }
}

