package net.royalur.lut.buffer;

import javax.annotation.Nonnull;

/**
 * Stores binary values and provides efficient methods to manipulate it.
 */
public abstract class FloatValueBuffer extends ValueBuffer {

    public FloatValueBuffer(@Nonnull ValueType type, int capacity) {
        super(type, capacity);
        if (!type.isFloat())
            throw new IllegalArgumentException("Provided ValueType is not floating-point!");
    }

    @Override
    public long set(int index, long value) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }

    @Override
    public int set(int index, int value) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }

    @Override
    public short set(int index, short value) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }

    @Override
    public byte set(int index, byte value) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }

    @Override
    public long getLong(int index) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }

    @Override
    public int getInt(int index) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }

    @Override
    public short getShort(int index) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }

    @Override
    public byte getByte(int index) {
        throw new IllegalStateException("Floating-point buffers do not support integer values");
    }
}
