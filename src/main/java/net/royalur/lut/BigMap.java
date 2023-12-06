package net.royalur.lut;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A big binary map with keys and values packed together.
 */
public class BigMap implements Iterable<BigMap.Entry> {

    public static final int DEFAULT_ENTRIES_PER_CHUNK = 16 * 1024;

    public static ArrayBufferBuilder LONG = ArrayBuffer.LongArrayBuffer::new;
    public static ArrayBufferBuilder INT = ArrayBuffer.IntArrayBuffer::new;
    public static ArrayBufferBuilder SHORT = ArrayBuffer.ShortArrayBuffer::new;
    public static ArrayBufferBuilder BYTE = ArrayBuffer.ByteArrayBuffer::new;

    private final int entriesPerChunk;
    private final ArrayBufferBuilder keyBufferBuilder;
    private final ArrayBufferBuilder valueBufferBuilder;
    private final List<Chunk> chunks;

    public BigMap(
            ArrayBufferBuilder keyBufferBuilder,
            ArrayBufferBuilder valueBufferBuilder,
            int entriesPerChunk
    ) {
        this.entriesPerChunk = entriesPerChunk;
        this.keyBufferBuilder = keyBufferBuilder;
        this.valueBufferBuilder = valueBufferBuilder;
        this.chunks = new ArrayList<>();
    }

    public BigMap(
            ArrayBufferBuilder keyBufferBuilder,
            ArrayBufferBuilder valueBufferBuilder
    ) {
        this(keyBufferBuilder, valueBufferBuilder, DEFAULT_ENTRIES_PER_CHUNK);
    }

    public int getEntryCount() {
        if (chunks.isEmpty())
            return 0;

        int chunkCount = chunks.size();
        Chunk lastChunk = chunks.get(chunkCount - 1);
        return entriesPerChunk * (chunkCount - 1) + lastChunk.getEntryCount();
    }

    private Chunk getNextChunkForPut() {
        if (chunks.isEmpty()) {
            chunks.add(new Chunk());
        }

        Chunk lastChunk = chunks.get(chunks.size() - 1);
        if (!lastChunk.isFull())
            return lastChunk;

        Chunk chunk = new Chunk();
        chunks.add(chunk);
        return chunk;
    }

    public void put(int key, int value) {
        getNextChunkForPut().put(key, value);
    }

    public void put(long key, long value) {
        getNextChunkForPut().put(key, value);
    }

    public @Nullable Integer getInt(int key) {
        return getInt(Integer.toUnsignedLong(key));
    }

    public @Nullable Integer getInt(long key) {
        for (Chunk chunk : chunks) {
            int entryIndex = chunk.indexOfKey(key);
            if (entryIndex >= 0)
                return chunk.getValueInt(entryIndex);
        }
        return null;
    }

    public @Nullable Long getLong(int key) {
        return getLong(Integer.toUnsignedLong(key));
    }

    public @Nullable Long getLong(long key) {
        for (Chunk chunk : chunks) {
            int entryIndex = chunk.indexOfKey(key);
            if (entryIndex >= 0)
                return chunk.getValueLong(entryIndex);
        }
        return null;
    }

    public void sort() {
        for (Chunk chunk : chunks) {
            chunk.sort();
        }
    }

    @Override
    public @Nonnull Iterator<Entry> iterator() {
        return new BigMapIterator();
    }

    /**
     * A chunk of memory for storing keys and values.
     */
    private class Chunk {

        private final int entryCapacity;
        private final ArrayBuffer keyBuffer;
        private final ArrayBuffer valueBuffer;
        private int entryCount = 0;

        /**
         * Unsigned.
         */
        private Long keyLowerBound = null;

        /**
         * Unsigned.
         */
        private Long keyUpperBound = null;

        private boolean knownSorted = true;

        public Chunk() {
            this.entryCapacity = entriesPerChunk;
            this.keyBuffer = keyBufferBuilder.create(entryCapacity);
            this.valueBuffer = valueBufferBuilder.create(entryCapacity);
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

        private void checkCapacityForPut() {
            if (entryCount >= entryCapacity)
                throw new IllegalStateException("Chunk is full!");
        }

        public void put(int key, int value) {
            checkCapacityForPut();
            int index = entryCount;
            entryCount += 1;
            keyBuffer.set(index, key);
            valueBuffer.set(index, value);
            moveIntoPlace(index);
        }

        public void put(long key, long value) {
            checkCapacityForPut();
            int index = entryCount;
            entryCount += 1;
            keyBuffer.set(index, key);
            valueBuffer.set(index, value);
            moveIntoPlace(index);
        }

        private void moveIntoPlace(int index) {
            long currentValue = keyBuffer.getLong(index);
            for (int j = index; j > 0; j--) {
                if (Long.compareUnsigned(currentValue, keyBuffer.getLong(j - 1)) >= 0)
                    break;

                swap(j, j - 1);
            }
            keyLowerBound = keyBuffer.getLong(0);
            keyUpperBound = keyBuffer.getLong(entryCount - 1);
            knownSorted = true;
        }

        public int indexOfKey(long key) {
            if (entryCount == 0 || key < keyLowerBound || key > keyUpperBound)
                return -1;

            if (knownSorted) {
                return keyBuffer.indexOfBinarySearch(key, 0, entryCount);
            } else {
                return keyBuffer.indexOf(key, 0, entryCount);
            }
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

        public void get(int index, @Nonnull Entry entry) {
            if (index < 0 || index >= entryCount)
                throw new IndexOutOfBoundsException();

            entry.key = keyBuffer.getLong(index);
            entry.value = valueBuffer.getLong(index);
        }

        public void sort() {
            if (knownSorted)
                return;

            for (int i = 1; i < entryCount; i++) {
                long currentValue = keyBuffer.getLong(i);
                for (int j = i; j > 0; j--) {
                    if (Long.compareUnsigned(currentValue, keyBuffer.getLong(j - 1)) >= 0)
                        break;

                    swap(j, j - 1);
                }
            }
            knownSorted = true;
            keyLowerBound = keyBuffer.getLong(0);
            keyUpperBound = keyBuffer.getLong(entryCount - 1);
        }

        private void swap(int index1, int index2) {
            if (index1 == index2)
                return;

            keyBuffer.swap(index1, index2);
            valueBuffer.swap(index1, index2);
        }
    }

    public static final class Entry {
        public long key;
        public long value;
    }

    public class BigMapIterator implements Iterator<Entry> {

        private int chunkIndex = 0;
        private int entryIndex = 0;
        private final @Nonnull Entry entry = new Entry();

        @Override
        public boolean hasNext() {
            return chunkIndex < chunks.size();
        }

        @Override
        public Entry next() {
            if (chunkIndex >= chunks.size())
                throw new NoSuchElementException();

            Chunk chunk = chunks.get(chunkIndex);
            Entry entry = this.entry;
            chunk.get(entryIndex, entry);

            if (entryIndex + 1 >= chunk.entryCount) {
                entryIndex = 0;
                chunkIndex += 1;
            } else {
                entryIndex += 1;
            }
            return entry;
        }
    }
}
