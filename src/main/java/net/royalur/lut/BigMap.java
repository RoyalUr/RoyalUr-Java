package net.royalur.lut;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A big binary map with keys and values packed together.
 */
public class BigMap implements Iterable<BigMap.Entry> {

    public static final int DEFAULT_ENTRIES_PER_CHUNK = 8 * 1024;

    public static ArrayBufferBuilder LONG = ArrayBuffer.LongArrayBuffer::new;
    public static ArrayBufferBuilder INT = ArrayBuffer.IntArrayBuffer::new;
    public static ArrayBufferBuilder SHORT = ArrayBuffer.ShortArrayBuffer::new;
    public static ArrayBufferBuilder BYTE = ArrayBuffer.ByteArrayBuffer::new;

    private final int entriesPerChunk;
    private final ArrayBufferBuilder keyBufferBuilder;
    private final ArrayBufferBuilder valueBufferBuilder;
    private final List<ChunkSet> chunks;

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
        int count = 0;
        for (ChunkSet set : chunks) {
            count += set.getEntryCount();
        }
        return count;
    }

    public int getChunkCount() {
        int count = 0;
        for (ChunkSet set : chunks) {
            count += set.getChunkCount();
        }
        return count;
    }

    private ChunkSet allocateChunkSet(int chunkCount) {
        Chunk[] chunkSet = new Chunk[chunkCount];
        for (int index = 0; index < chunkCount; ++index) {
            chunkSet[index] = new Chunk();
        }
        return new ChunkSet(chunkSet);
    }

    private ChunkSet mergeSortedChunkSets(ChunkSet input1, ChunkSet input2) {
        int chunkCount = input1.getChunkCount() + input2.getChunkCount();
        ChunkSet output = allocateChunkSet(chunkCount);

        int chunk1Size = input1.getEntryCount();
        int chunk2Size = input2.getEntryCount();

        int index1 = 0;
        int index2 = 0;
        long key1 = input1.getKeyLong(index1);
        long key2 = input2.getKeyLong(index2);
        int outputSize = chunk1Size + chunk2Size;

        for (int outputIndex = 0; outputIndex < outputSize; ++outputIndex) {
            if (Long.compareUnsigned(key1, key2) <= 0) {
                long value = input1.getValueLong(index1);
                output.put(key1, value);

                index1 += 1;
                if (index1 >= chunk1Size)
                    break;

                key1 = input1.getKeyLong(index1);

            } else {
                long value = input2.getValueLong(index2);
                output.put(key2, value);

                index2 += 1;
                if (index2 >= chunk2Size)
                    break;

                key2 = input2.getKeyLong(index2);
            }
        }

        while (index1 < chunk1Size) {
            long key = input1.getKeyLong(index1);
            long value = input1.getValueLong(index1);
            index1 += 1;
            output.put(key, value);
        }
        while (index2 < chunk2Size) {
            long key = input2.getKeyLong(index2);
            long value = input2.getValueLong(index2);
            index2 += 1;
            output.put(key, value);
        }
        return output;
    }

    private ChunkSet getNextChunkSetForPut() {
        if (chunks.isEmpty()) {
            chunks.add(allocateChunkSet(1));
        }

        ChunkSet last = chunks.get(chunks.size() - 1);
        if (!last.isFull())
            return last;

        // Merge sort full chunks!
        while (chunks.size() >= 2) {
            int size = chunks.size();
            ChunkSet set1 = chunks.get(size - 1);
            ChunkSet set2 = chunks.get(size - 2);
            if (set1.getChunkCount() != set2.getChunkCount())
                break;

            ChunkSet sorted = mergeSortedChunkSets(set1, set2);
            chunks.remove(size - 1);
            chunks.remove(size - 2);
            chunks.add(sorted);
        }

        // Add a new chunk.
        ChunkSet next = allocateChunkSet(1);
        chunks.add(next);
        return next;
    }

    public void sort() {
        while (chunks.size() >= 2) {
            int size = chunks.size();
            ChunkSet set1 = chunks.get(size - 1);
            ChunkSet set2 = chunks.get(size - 2);
            ChunkSet sorted = mergeSortedChunkSets(set1, set2);
            chunks.remove(size - 1);
            chunks.remove(size - 2);
            chunks.add(sorted);
        }
    }

    public void put(int key, int value) {
        getNextChunkSetForPut().put(key, value);
    }

    public void put(long key, long value) {
        getNextChunkSetForPut().put(key, value);
    }

    public @Nullable Integer getInt(int key) {
        for (ChunkSet chunkSet : chunks) {
            Integer value = chunkSet.getInt(key);
            if (value != null)
                return value;
        }
        return null;
    }

    public @Nullable Long getLong(long key) {
        for (ChunkSet chunkSet : chunks) {
            Long value = chunkSet.getLong(key);
            if (value != null)
                return value;
        }
        return null;
    }

    private void loopChunks(Consumer<Chunk> chunkConsumer) {
        for (ChunkSet chunkSet : chunks) {
            for (Chunk chunk : chunkSet.chunkSet) {
                chunkConsumer.accept(chunk);
            }
        }
    }

    public double getOverlapsPerChunk() {
        AtomicInteger overlappingChunks = new AtomicInteger(0);
        loopChunks((chunk1) -> {
            loopChunks((chunk2) -> {
                if (chunk1 == chunk2)
                    return;

                if (chunk1.overlaps(chunk2)) {
                    overlappingChunks.incrementAndGet();
                }
            });
        });
        return  (double) overlappingChunks.get() / chunks.size();
    }

    @Override
    public @Nonnull Iterator<Entry> iterator() {
        return new BigMapIterator();
    }

    /**
     * Manages a set of sorted chunks.
     */
    private class ChunkSet {

        private static final int BINARY_TO_LINEAR_SEARCH_THRESHOLD = 8;

        private final Chunk[] chunkSet;
        private int emptyChunkIndex = 0;

        public ChunkSet(Chunk[] chunkSet) {
            if (chunkSet.length == 0)
                throw new IllegalArgumentException("No chunks");

            this.chunkSet = chunkSet;
        }

        public int getChunkCount() {
            return chunkSet.length;
        }

        public Chunk getChunk(int index) {
            return chunkSet[index];
        }

        private @Nullable Chunk getNextChunkWithSpace() {
            if (emptyChunkIndex >= chunkSet.length)
                return null;

            Chunk lastChunk = chunkSet[emptyChunkIndex];
            if (!lastChunk.isFull())
                return lastChunk;

            Chunk chunk;
            do {
                emptyChunkIndex += 1;
                if (emptyChunkIndex >= chunkSet.length)
                    return null;

                chunk = chunkSet[emptyChunkIndex];
            } while (chunk.isFull());

            return chunk;
        }

        private Chunk getNextChunkForPut() {
            Chunk chunk = getNextChunkWithSpace();
            if (chunk == null)
                throw new IllegalStateException("ChunkSet is full!");

            return chunk;
        }

        public boolean isFull() {
            return getNextChunkWithSpace() == null;
        }

        public int getEntryCount() {
            Chunk lastChunk = getNextChunkWithSpace();
            int lastEntryCount = (lastChunk == null ? 0 : lastChunk.getEntryCount());
            return emptyChunkIndex * entriesPerChunk + lastEntryCount;
        }

        public void put(int key, int value) {
            getNextChunkForPut().put(key, value);
        }

        public void put(long key, long value) {
            getNextChunkForPut().put(key, value);
        }

        public long getKeyLong(int index) {
            int chunkIndex = index / entriesPerChunk;
            int entryIndex = index - chunkIndex * entriesPerChunk;
            return chunkSet[chunkIndex].getKeyLong(entryIndex);
        }

        public int getValueInt(int index) {
            int chunkIndex = index / entriesPerChunk;
            int entryIndex = index - chunkIndex * entriesPerChunk;
            return chunkSet[chunkIndex].getValueInt(entryIndex);
        }

        public long getValueLong(int index) {
            int chunkIndex = index / entriesPerChunk;
            int entryIndex = index - chunkIndex * entriesPerChunk;
            return chunkSet[chunkIndex].getValueLong(entryIndex);
        }

        private @Nullable Integer getIntLinearSearch(int key, int startIndex, int endIndex) {
            long keyUnsigned = Integer.toUnsignedLong(key);

            for (int index = startIndex; index < endIndex; ++index) {
                Chunk chunk = chunkSet[index];
                if (chunk.mayContain(keyUnsigned)) {
                    int entryIndex = chunk.indexOfKey(key);
                    return entryIndex >= 0 ? chunk.getValueInt(entryIndex) : null;
                }
            }
            return null;
        }

        private @Nullable Long getLongLinearSearch(long key, int startIndex, int endIndex) {
            for (int index = startIndex; index < endIndex; ++index) {
                Chunk chunk = chunkSet[index];
                if (chunk.mayContain(key)) {
                    int entryIndex = chunk.indexOfKey(key);
                    return entryIndex >= 0 ? chunk.getValueLong(entryIndex) : null;
                }
            }
            return null;
        }

        public @Nullable Integer getInt(int key) {
            long keyUnsigned = Integer.toUnsignedLong(key);

            int lower = 0;
            int upper = chunkSet.length;
            while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
                int middleIndex = lower + (upper - lower) / 2;
                Chunk current = chunkSet[middleIndex];
                if (current.mayContain(keyUnsigned))
                    return current.getInt(key);

                if (Long.compareUnsigned(current.maxValue, keyUnsigned) > 0) {
                    upper = middleIndex;
                } else {
                    lower = middleIndex + 1;
                }
            }
            return getIntLinearSearch(key, lower, upper);
        }

        public @Nullable Long getLong(long key) {
            int lower = 0;
            int upper = chunkSet.length;
            while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
                int middleIndex = lower + (upper - lower) / 2;
                Chunk current = chunkSet[middleIndex];
                if (current.mayContain(key))
                    return current.getLong(key);

                if (Long.compareUnsigned(current.maxValue, key) > 0) {
                    upper = middleIndex;
                } else {
                    lower = middleIndex + 1;
                }
            }
            return getLongLinearSearch(key, lower, upper);
        }
    }

    /**
     * A chunk of memory for storing keys and values.
     */
    private class Chunk {

        private final int entryCapacity;
        private final ArrayBuffer keyBuffer;
        private final ArrayBuffer valueBuffer;
        private int entryCount = 0;
        private long minValue = 0;
        private long maxValue = 0;

        public Chunk() {
            this.entryCapacity = entriesPerChunk;
            this.keyBuffer = keyBufferBuilder.create(entryCapacity);
            this.valueBuffer = valueBufferBuilder.create(entryCapacity);
        }

        public void clear() {
            entryCount = 0;
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

        public void put(int key, int value) {
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

        public void put(long key, long value) {
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

        public void get(int index, @Nonnull Entry entry) {
            if (index < 0 || index >= entryCount)
                throw new IndexOutOfBoundsException();

            entry.key = keyBuffer.getLong(index);
            entry.value = valueBuffer.getLong(index);
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

        private int setIndex = 0;
        private int chunkIndex = 0;
        private int entryIndex = 0;
        private final @Nonnull Entry entry = new Entry();

        @Override
        public boolean hasNext() {
            return setIndex < chunks.size();
        }

        @Override
        public Entry next() {
            if (setIndex >= chunks.size())
                throw new NoSuchElementException();

            ChunkSet chunkSet = chunks.get(setIndex);
            Chunk chunk = chunkSet.getChunk(chunkIndex);
            chunk.get(entryIndex, entry);

            if (entryIndex + 1 >= chunk.entryCount) {
                entryIndex = 0;
                if (chunkIndex + 1 >= chunkSet.getChunkCount()) {
                    chunkIndex = 0;
                    setIndex += 1;
                } else {
                    chunkIndex += 1;
                }
            } else {
                entryIndex += 1;
            }
            return entry;
        }
    }
}
