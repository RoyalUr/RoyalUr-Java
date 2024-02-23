package net.royalur.lut.buffer;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum ValueType {
    UINT64(1, UInt64ValueBuffer::new, true, false, 8),
    UINT32(2, UInt32ValueBuffer::new, true, false, 4),
    UINT16(3, UInt16ValueBuffer::new, true, false, 2),
    UINT8(4, UInt8ValueBuffer::new, true, false, 1),
    FLOAT64(5, Float64ValueBuffer::new, false, true, 8),
    FLOAT32(6, Float32ValueBuffer::new, false, true, 4),
    PERCENT16(7, Percent16ValueBuffer::new, false, true, 2);

    private final int id;
    private final Function<Integer, ValueBuffer> builderFn;
    private final boolean isIntType;
    private final boolean isFloatType;
    private final int byteCount;

    ValueType(
            int id,
            Function<Integer, ValueBuffer> builderFn,
            boolean isIntType,
            boolean isFloatType,
            int byteCount
    ) {
        this.id = id;
        this.builderFn = builderFn;
        this.isIntType = isIntType;
        this.isFloatType = isFloatType;
        this.byteCount = byteCount;
    }

    public int getID() {
        return id;
    }

    public int getByteCount() {
        return byteCount;
    }

    public boolean isInt() {
        return isIntType;
    }

    public boolean isFloat() {
        return isFloatType;
    }

    public ValueBuffer createBuffer(int capacity) {
        ValueBuffer result = builderFn.apply(capacity);
        if (result == null)
            throw new NullPointerException();

        return result;
    }

    public IntValueBuffer createIntBuffer(int capacity) {
        ValueBuffer result = createBuffer(capacity);
        if (!(result instanceof IntValueBuffer))
            throw new IllegalStateException("Buffer is not of integer type! " + name());

        return (IntValueBuffer) result;
    }

    static ValueType getByID(int id) {
        for (ValueType valueType : values()) {
            if (valueType.getID() == id)
                return valueType;
        }
        throw new IllegalArgumentException("Unknown value type " + id);
    }
}
