package net.royalur.lut.buffer;

import java.util.function.Function;

public enum ValueType {
    UINT64(1, "u64", UInt64ValueBuffer::new, true, false, 8),
    UINT32(2, "u32", UInt32ValueBuffer::new, true, false, 4),
    UINT16(3, "u16", UInt16ValueBuffer::new, true, false, 2),
    UINT8(4, "u8", UInt8ValueBuffer::new, true, false, 1),
    FLOAT64(5, "f64", Float64ValueBuffer::new, false, true, 8),
    FLOAT32(6, "f32", Float32ValueBuffer::new, false, true, 4),
    PERCENT16(7, "percent16", Percent16ValueBuffer::new, false, true, 2);

    private final int id;
    private final String textID;
    private final Function<Integer, ValueBuffer> builderFn;
    private final boolean isIntType;
    private final boolean isFloatType;
    private final int byteCount;

    ValueType(
            int id,
            String textID,
            Function<Integer, ValueBuffer> builderFn,
            boolean isIntType,
            boolean isFloatType,
            int byteCount
    ) {
        this.id = id;
        this.textID = textID;
        this.builderFn = builderFn;
        this.isIntType = isIntType;
        this.isFloatType = isFloatType;
        this.byteCount = byteCount;
    }

    public int getID() {
        return id;
    }

    public String getTextID() {
        return textID;
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

    public FloatValueBuffer createFloatBuffer(int capacity) {
        ValueBuffer result = createBuffer(capacity);
        if (!(result instanceof FloatValueBuffer))
            throw new IllegalStateException("Buffer is not of floating-point type! " + name());

        return (FloatValueBuffer) result;
    }

    public static ValueType getByID(int id) {
        for (ValueType valueType : values()) {
            if (valueType.getID() == id)
                return valueType;
        }
        throw new IllegalArgumentException("Unknown value type " + id);
    }

    public static ValueType getByTextID(String textID) {
        for (ValueType valueType : values()) {
            if (valueType.getTextID().equals(textID))
                return valueType;
        }
        throw new IllegalArgumentException("Unknown value type " + textID);
    }
}
