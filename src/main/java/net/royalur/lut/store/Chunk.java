package net.royalur.lut.store;

import net.royalur.lut.buffer.IntValueBuffer;
import net.royalur.lut.buffer.ValueBuffer;
import net.royalur.lut.buffer.ValueType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * A chunk of memory for storing keys and values.
 */
public class Chunk {

    private final int entryCapacity;
    private final @Nonnull IntValueBuffer keyBuffer;
    private final @Nonnull ValueBuffer valueBuffer;
    private int entryCount = 0;
    private long minValue = 0;
    private long maxValue = 0;

    public Chunk(int entryCapacity, ValueType keyType, ValueType valueType) {
        this.entryCapacity = entryCapacity;
        this.keyBuffer = keyType.createIntBuffer(entryCapacity);
        this.valueBuffer = valueType.createBuffer(entryCapacity);
    }

    public void clear() {
        entryCount = 0;
    }

    public ValueType getKeyType() {
        return keyBuffer.getType();
    }

    public ValueType getValueType() {
        return valueBuffer.getType();
    }

    public int getEntryCapacity() {
        return entryCapacity;
    }

    public boolean isFull() {
        return entryCount >= entryCapacity;
    }

    public int getEntryCount() {
        return entryCount;
    }

    private void updateStatistics() {
        minValue = keyBuffer.getLong(0);
        maxValue = keyBuffer.getLong(entryCount - 1);
    }

    public long getMinValue() {
        return minValue;
    }

    public long getMaxValue() {
        return maxValue;
    }

    public boolean mayContain(long value) {
        return Long.compareUnsigned(value, minValue) >= 0
                && Long.compareUnsigned(value, maxValue) <= 0;
    }

    public void addEntry(int key, int value) {
        if (entryCount >= entryCapacity)
            throw new IllegalStateException("Chunk is full!");

        int index = entryCount;
        entryCount += 1;
        keyBuffer.set(index, key);
        int targetIndex = keyBuffer.moveIntoSortedPlace(index);
        valueBuffer.set(index, value);
        valueBuffer.moveIntoPlace(index, targetIndex);
        updateStatistics();
    }

    public void addEntry(long key, long value) {
        if (entryCount >= entryCapacity)
            throw new IllegalStateException("Chunk is full!");

        int index = entryCount;
        entryCount += 1;
        keyBuffer.set(index, key);
        int targetIndex = keyBuffer.moveIntoSortedPlace(index);
        valueBuffer.set(index, value);
        valueBuffer.moveIntoPlace(index, targetIndex);
        updateStatistics();
    }

    public void addEntryWithoutSorting(long key, double value) {
        if (entryCount >= entryCapacity)
            throw new IllegalStateException("Chunk is full!");

        int index = entryCount;
        entryCount += 1;
        keyBuffer.set(index, key);
        valueBuffer.set(index, value);
        updateStatistics();
    }

    public boolean overlaps(Chunk other) {
        return Long.compareUnsigned(getMinValue(), other.getMaxValue()) <= 0
                && Long.compareUnsigned(getMaxValue(), other.getMinValue()) >= 0;
    }

    public int indexOfKey(int key) {
        return keyBuffer.indexOfBinarySearch(key, 0, entryCount);
    }

    public int indexOfKey(long key) {
        return keyBuffer.indexOfBinarySearch(key, 0, entryCount);
    }

    public @Nullable Integer getInt(int key) {
        int entryIndex = indexOfKey(key);
        return entryIndex >= 0 ? valueBuffer.getInt(entryIndex) : null;
    }

    public @Nullable Long getLong(long key) {
        int entryIndex = indexOfKey(key);
        return entryIndex >= 0 ? valueBuffer.getLong(entryIndex) : null;
    }

    public int updateEntry(int key, int value) {
        int entryIndex = indexOfKey(key);
        if (entryIndex < 0)
            throw new NoSuchElementException();

        return valueBuffer.set(entryIndex, value);
    }

    public long getKeyLong(int index) {
        if (index < 0 || index >= entryCount)
            throw new IndexOutOfBoundsException();

        return keyBuffer.getLong(index);
    }

    public long getValueLong(int index) {
        if (index < 0 || index >= entryCount)
            throw new IndexOutOfBoundsException();

        return valueBuffer.getLong(index);
    }

    public int getValueInt(int index) {
        if (index < 0 || index >= entryCount)
            throw new IndexOutOfBoundsException();

        return valueBuffer.getInt(index);
    }

    public void get(int index, @Nonnull ChunkStore.Entry entry) {
        if (index < 0 || index >= entryCount)
            throw new IndexOutOfBoundsException();

        entry.key = keyBuffer.getLong(index);
        entry.value = valueBuffer.getLong(index);
    }

    public void writeKeys(@Nonnull DataSink output) throws IOException {
        keyBuffer.writeContents(output, 0, entryCount);
    }

    public void writeValues(@Nonnull DataSink output) throws IOException {
        valueBuffer.writeContents(output, 0, entryCount);
    }

    public void read(@Nonnull DataSource input, int entryCount) throws IOException {
        // TODO : Replace with commented out code below
        this.entryCount = input.readInt();
        minValue = input.readLong();
        maxValue = input.readLong();
        // END TODO

        keyBuffer.readContents(input, 0, entryCapacity); // TODO use entryCount, not capacity
        valueBuffer.readContents(input, 0, entryCapacity); // TODO use entryCount, not capacity
//        this.entryCount = entryCount;
//        if (entryCount > 0) {
//            this.minValue = keyBuffer.getLong(0);
//            this.maxValue = keyBuffer.getLong(entryCount - 1);
//        } else {
//            this.minValue = 0;
//            this.maxValue = 0;
//        }
    }
}
