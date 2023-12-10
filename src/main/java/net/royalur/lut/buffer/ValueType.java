package net.royalur.lut.buffer;

import javax.annotation.Nonnull;
import java.util.function.Function;

public enum ValueType {
    LONG(LongValueBuffer::new),
    INT(IntValueBuffer::new),
    SHORT(ShortValueBuffer::new),
    BYTE(ByteValueBuffer::new);

    private final @Nonnull Function<Integer, ValueBuffer> builderFn;

    ValueType(@Nonnull Function<Integer, ValueBuffer> builderFn) {
        this.builderFn = builderFn;
    }

    public @Nonnull ValueBuffer create(int capacity) {
        ValueBuffer result = builderFn.apply(capacity);
        if (result == null)
            throw new NullPointerException();

        return result;
    }
}
