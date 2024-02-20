package net.royalur.lut.buffer;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum ValueType {
    INT64(1, Int64ValueBuffer::new, true, false, 8),
    INT32(2, Int32ValueBuffer::new, true, false, 4),
    INT16(3, Int16ValueBuffer::new, true, false, 2),
    INT8(4, Int8ValueBuffer::new, true, false, 1),
    FLOAT64(5, Float64ValueBuffer::new, false, true, 8),
    FLOAT32(6, Float32ValueBuffer::new, false, true, 4);

    private final int id;
    private final @Nonnull Function<Integer, ValueBuffer> builderFn;
    private final boolean isIntType;
    private final boolean isFloatType;
    private final int byteCount;

    ValueType(
            int id,
            @Nonnull Function<Integer, ValueBuffer> builderFn,
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

    public @Nonnull ValueBuffer createBuffer(int capacity) {
        ValueBuffer result = builderFn.apply(capacity);
        if (result == null)
            throw new NullPointerException();

        return result;
    }

    public @Nonnull IntValueBuffer createIntBuffer(int capacity) {
        ValueBuffer result = createBuffer(capacity);
        if (!(result instanceof IntValueBuffer))
            throw new IllegalStateException("Buffer is not of integer type! " + name());

        return (IntValueBuffer) result;
    }

    static @Nonnull ValueType getByID(int id) {
        for (ValueType valueType : values()) {
            if (valueType.getID() == id)
                return valueType;
        }
        throw new IllegalArgumentException("Unknown value type " + id);
    }
}
