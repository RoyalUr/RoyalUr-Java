package net.royalur.lut.store;

import net.royalur.lut.buffer.UInt32ValueBuffer;

/**
 * A sorted buffer of uint32 values. Values are sorted as they are added.
 */
public class OrderedUInt32Buffer {

    private final UInt32ValueBuffer buffer;
    private int entryCount;

    public OrderedUInt32Buffer(int capacity, int entryCount) {
        this.buffer = new UInt32ValueBuffer(capacity);
        this.entryCount = entryCount;
    }

    public OrderedUInt32Buffer(int capacity) {
        this(capacity, 0);
    }

    public boolean isFull() {
        return entryCount >= buffer.getCapacity();
    }

    public UInt32ValueBuffer getBuffer() {
        return buffer;
    }

    public int getEntryCount() {
        return entryCount;
    }

    public void add(int value) {
        int index = entryCount;
        entryCount += 1;
        buffer.set(index, value);
        buffer.moveIntoSortedPlace(index);
    }

    public int get(int index) {
        if (index < 0 || index >= entryCount)
            throw new IndexOutOfBoundsException();

        return buffer.getInt(index);
    }

    public OrderedUInt32Buffer compress() {
        if (entryCount == buffer.getCapacity())
            return this;

        OrderedUInt32Buffer copy = new OrderedUInt32Buffer(entryCount, entryCount);
        for (int index = 0; index < entryCount; ++index) {
            copy.buffer.set(index, buffer.getInt(index));
        }
        return copy;
    }
}
