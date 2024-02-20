package net.royalur.lut.buffer;

import javax.annotation.Nonnull;

/**
 * Stores binary values and provides efficient methods to manipulate it.
 */
public abstract class IntValueBuffer extends ValueBuffer {

    public IntValueBuffer(@Nonnull ValueType type, int capacity) {
        super(type, capacity);
        if (!type.isInt())
            throw new IllegalArgumentException("Provided ValueType is not an integer!");
    }

    @Override
    public double set(int index, double value) {
        throw new IllegalStateException("Integer buffers do not support floating point values");
    }

    @Override
    public float set(int index, float value) {
        throw new IllegalStateException("Integer buffers do not support floating point values");
    }

    @Override
    public double getDouble(int index) {
        throw new IllegalStateException("Integer buffers do not support floating point values");
    }

    @Override
    public float getFloat(int index) {
        throw new IllegalStateException("Integer buffers do not support floating point values");
    }

    public abstract int indexOf(long value, int startIndex, int endIndex);

    public abstract int indexOf(int value, int startIndex, int endIndex);

    public abstract int indexOf(short value, int startIndex, int endIndex);

    public abstract int indexOf(byte value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(long value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(int value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(short value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(byte value, int startIndex, int endIndex);

    /**
     * Returns the final index that the value was moved to.
     */
    public abstract int moveIntoSortedPlace(int index);
}
