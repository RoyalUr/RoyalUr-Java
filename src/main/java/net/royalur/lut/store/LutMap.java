package net.royalur.lut.store;

import net.royalur.lut.buffer.FloatValueBuffer;
import net.royalur.lut.buffer.UInt32ValueBuffer;

/**
 * A big map of compact keys and values for a Lut.
 */
public class LutMap {

    private final int entryCount;
    private final UInt32ValueBuffer keyBuffer;
    private final FloatValueBuffer valueBuffer;

    public LutMap(int entryCount, UInt32ValueBuffer keyBuffer, FloatValueBuffer valueBuffer) {
        if (entryCount > keyBuffer.getCapacity())
            throw new IllegalArgumentException("keyBuffer is smaller than entryCount");
        if (entryCount > valueBuffer.getCapacity())
            throw new IllegalArgumentException("valueBuffer is smaller than entryCount");

        this.entryCount = entryCount;
        this.keyBuffer = keyBuffer;
        this.valueBuffer = valueBuffer;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public UInt32ValueBuffer getKeyBuffer() {
        return keyBuffer;
    }

    public FloatValueBuffer getValueBuffer() {
        return valueBuffer;
    }

    public int indexOfKey(int lowerKey) {
        return keyBuffer.indexOfBinarySearch(lowerKey, 0, entryCount);
    }

    public long getLong(int lowerKey) {
        return valueBuffer.getLong(indexOfKey(lowerKey));
    }

    public int getInt(int lowerKey) {
        return valueBuffer.getInt(indexOfKey(lowerKey));
    }

    public short getShort(int lowerKey) {
        return valueBuffer.getShort(indexOfKey(lowerKey));
    }

    public byte getByte(int lowerKey) {
        return valueBuffer.getByte(indexOfKey(lowerKey));
    }

    public double getDouble(int lowerKey) {
        return valueBuffer.getDouble(indexOfKey(lowerKey));
    }

    public float getFloat(int lowerKey) {
        return valueBuffer.getFloat(indexOfKey(lowerKey));
    }

    public long set(int lowerKey, long value) {
        return valueBuffer.set(indexOfKey(lowerKey), value);
    }

    public int set(int lowerKey, int value) {
        return valueBuffer.set(indexOfKey(lowerKey), value);
    }

    public short set(int lowerKey, short value) {
        return valueBuffer.set(indexOfKey(lowerKey), value);
    }

    public byte set(int lowerKey, byte value) {
        return valueBuffer.set(indexOfKey(lowerKey), value);
    }

    public double set(int lowerKey, double value) {
        return valueBuffer.set(indexOfKey(lowerKey), value);
    }

    public float set(int lowerKey, float value) {
        return valueBuffer.set(indexOfKey(lowerKey), value);
    }
}
