package com.dat3m.dartagnan.program.processing;

import com.dat3m.dartagnan.exception.MalformedProgramException;
import com.dat3m.dartagnan.expression.Expression;
import com.dat3m.dartagnan.expression.ExpressionFactory;
import com.dat3m.dartagnan.expression.Type;
import com.dat3m.dartagnan.expression.integers.IntLiteral;
import com.dat3m.dartagnan.expression.pointers.GEPExpr;
import com.dat3m.dartagnan.expression.processing.ExprTransformer;
import com.dat3m.dartagnan.expression.type.*;
import com.dat3m.dartagnan.program.Function;
import com.dat3m.dartagnan.program.Program;
import com.dat3m.dartagnan.program.event.RegReader;
import com.dat3m.dartagnan.program.memory.MemoryObject;

import java.util.List;

/*
    Replaces GEP expressions by plain pointer arithmetic according to
    https://llvm.org/docs/LangRef.html#getelementptr-instruction

    WARNING:
    (1) Our GEPs have no attributes but LLVM's have.
        For attributed GEPs, the semantics may mismatch.
    (2) LLVM's LangRef has a strange special rule that we have not implemented:
       """
       The offsets are then added to the low bits of the base address up to the index type width,
       with silently-wrapping two’s complement arithmetic.
       If the pointer size is larger than the index size, this means that the bits outside
       the index type width will not be affected.
       """
       This rule says that only the lower bits of pointers are affected by GEP additions,
       if the index type is smaller than the pointer type.

    NOTE:
    This replacement might also match with SPIRV's OpAccessChain:
    https://registry.khronos.org/SPIR-V/specs/unified1/SPIRV.html#OpAccessChain
    However, the documentation of SPIRV's operation is unclear.
*/
public class GEPToAddition implements ProgramProcessor {

    private GEPToAddition() {}

    public static GEPToAddition newInstance() {
        return new GEPToAddition();
    }

    @Override
    public void run(Program program) {
        final var transformer = new GEPToAdditionTransformer();
        for (Function function : program.getFunctions()) {
            for (RegReader reader : function.getEvents(RegReader.class)) {
                reader.transformExpressions(transformer);
            }
        }

        for (MemoryObject memoryObject : program.getMemory().getObjects()) {
            for (int field : memoryObject.getInitializedFields()) {
                memoryObject.setInitialValue(field, memoryObject.getInitialValue(field).accept(transformer));
            }
        }
    }

    private static final class GEPToAdditionTransformer extends ExprTransformer {

        private final TypeFactory types = TypeFactory.getInstance();
        private final ExpressionFactory expressions = ExpressionFactory.getInstance();
        private final IntegerType archType = types.getArchType();

        @Override //TODO fix the calculations order and gen pointer type
        public Expression visitGEPExpression(GEPExpr getElementPointer) {
            Type type = getElementPointer.getIndexingType();
            Expression result = getElementPointer.getBase().accept(this);
            final List<Expression> offsets = getElementPointer.getOffsets();

            assert !offsets.isEmpty();
            Expression offset =
                    expressions.makeMul(
                            expressions.makeValue(types.getMemorySizeInBytes(type), archType),
                            expressions.makeIntegerCast(offsets.get(0).accept(this), archType, true));

            for (final Expression oldOffset : offsets.subList(1, offsets.size())) {
                final Expression _offset = oldOffset.accept(this);
                if (type instanceof ArrayType arrayType) {
                    type = arrayType.getElementType();
                    offset = expressions.makeAdd(offset,
                            expressions.makeMul(
                                    expressions.makeValue(types.getMemorySizeInBytes(arrayType.getElementType()), archType),
                                    expressions.makeIntegerCast(_offset, archType, true)));
                    continue;
                }
                if (!(type instanceof AggregateType aggregateType)) {
                    throw new MalformedProgramException(String.format("GEP from non-compound type %s.", type));
                }
                if (!(_offset instanceof IntLiteral constant)) {
                    throw new MalformedProgramException(
                            String.format("Non-constant field index %s for aggregate of type %s.", offset, type));
                }
                final TypeOffset typeOffset = TypeOffset.of(aggregateType, constant.getValueAsInt());
                type = typeOffset.type();
                offset = expressions.makeAdd(offset, expressions.makeValue(typeOffset.offset(), archType));
            }
            return expressions.makePtrAddOffset(result, offset);
        }
    }
}
