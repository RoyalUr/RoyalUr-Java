package net.royalur.lut.buffer;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum ValueType {
    LONG(LongValueBuffer::new, 8),
    INT(IntValueBuffer::new, 4),
    SHORT(ShortValueBuffer::new, 2),
    BYTE(ByteValueBuffer::new, 1);

    private final @Nonnull Function<Integer, ValueBuffer> builderFn;
    private final int byteCount;

    ValueType(
            @Nonnull Function<Integer, ValueBuffer> builderFn,
            int byteCount
    ) {
        this.builderFn = builderFn;
        this.byteCount = byteCount;
    }

    public @Nonnull ValueBuffer create(int capacity) {
        ValueBuffer result = builderFn.apply(capacity);
        if (result == null)
            throw new NullPointerException();

        return result;
    }

    public int getByteCount() {
        return byteCount;
    }
}
