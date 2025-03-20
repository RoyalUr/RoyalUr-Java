package net.royalur.lut.buffer;

import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.DataSource;

import java.io.*;

/**
 * Stores binary values and provides efficient methods to manipulate it.
 */
public abstract class ValueBuffer {

    private final ValueType type;
    private final int capacity;

    public ValueBuffer(ValueType type, int capacity) {
        this.type = type;
        this.capacity = capacity;
    }

    public ValueType getType() {
        return type;
    }

    public int getCapacity() {
        return capacity;
    }

    public abstract long set(int index, long value);

    public abstract int set(int index, int value);

    public abstract short set(int index, short value);

    public abstract byte set(int index, byte value);

    public abstract double set(int index, double value);

    public abstract float set(int index, float value);

    public abstract long getLong(int index);

    public abstract int getInt(int index);

    public abstract short getShort(int index);

    public abstract byte getByte(int index);

    public abstract double getDouble(int index);

    public abstract float getFloat(int index);

    /**
     * Moves the value to targetIndex, and shifts all values in between.
     */
    public abstract void moveIntoPlace(int index, int targetIndex);

    public abstract void writeContents(
            DataSink output, int startIndex, int endIndex
    ) throws IOException;

    public void writeContents(DataSink output) throws IOException {
        writeContents(output, 0, capacity);
    }

    public abstract void readContents(
            DataSource input, int startIndex, int endIndex
    ) throws IOException;

    public void readContents(DataSource input) throws IOException {
        readContents(input, 0, capacity);
    }

    public ValueBuffer convertTo(ValueType valueType) {
        // Already the correct type, no conversion needed.
        if (getType() == valueType)
            return this;

        int capacity = getCapacity();
        ValueBuffer newBuffer = valueType.createBuffer(capacity);
        boolean bothIntbuffers = (valueType.isInt() && getType().isInt());
        for (int index = 0; index < capacity; ++index) {
            if (bothIntbuffers) {
                newBuffer.set(index, getLong(index));
            } else {
                newBuffer.set(index, getDouble(index));
            }
        }
        return newBuffer;
    }
}
