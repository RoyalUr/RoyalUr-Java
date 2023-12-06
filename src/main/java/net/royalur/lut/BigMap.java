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

    public static final int DEFAULT_ENTRIES_PER_CHUNK = 128 * 1024;

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
        private Long keyLowerBound = null;
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
            set(index, key, value);
        }

        public void put(long key, long value) {
            checkCapacityForPut();
            int index = entryCount;
            entryCount += 1;
            set(index, key, value);
        }

        private void updateStatisticsAfterSet(long key) {
            if (keyLowerBound == null || key < keyLowerBound) {
                keyLowerBound = key;
            }
            if (keyUpperBound == null || key > keyUpperBound) {
                keyUpperBound = key;
            }
            knownSorted = false;
        }

        public void set(int entryIndex, int key, int value) {
            keyBuffer.set(entryIndex, value);
            valueBuffer.set(entryIndex, value);
            updateStatisticsAfterSet(key);
        }

        public void set(int entryIndex, long key, long value) {
            keyBuffer.set(entryIndex, value);
            valueBuffer.set(entryIndex, value);
            updateStatisticsAfterSet(key);
        }

        public int indexOfKey(long key) {
            if (entryCount == 0 || key < keyLowerBound || key > keyUpperBound)
                return -1;

            return keyBuffer.indexOf(key, 0, entryCount);
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
