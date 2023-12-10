package net.royalur.lut;

import net.royalur.lut.buffer.ValueBuffer;
import net.royalur.lut.buffer.ValueType;

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

    private final int entriesPerChunk;
    private final ValueType keyType;
    private final ValueType valueType;
    private final List<ChunkSet> chunkSets;

    public BigMap(
            ValueType keyType,
            ValueType valueType,
            int entriesPerChunk
    ) {
        this.entriesPerChunk = entriesPerChunk;
        this.keyType = keyType;
        this.valueType = valueType;
        this.chunkSets = new ArrayList<>();
    }

    public BigMap(ValueType keyType, ValueType valueType) {
        this(keyType, valueType, DEFAULT_ENTRIES_PER_CHUNK);
    }

    public int getEntryCount() {
        int count = 0;
        for (ChunkSet set : chunkSets) {
            count += set.getEntryCount();
        }
        return count;
    }

    public int getChunkCount() {
        int count = 0;
        for (ChunkSet set : chunkSets) {
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
        if (chunkSets.isEmpty()) {
            chunkSets.add(allocateChunkSet(1));
        }

        ChunkSet last = chunkSets.get(chunkSets.size() - 1);
        if (!last.isFull())
            return last;

        // Merge sort full chunks!
        while (chunkSets.size() >= 2) {
            int size = chunkSets.size();
            ChunkSet set1 = chunkSets.get(size - 1);
            ChunkSet set2 = chunkSets.get(size - 2);
            if (set1.getChunkCount() != set2.getChunkCount())
                break;

            ChunkSet sorted = mergeSortedChunkSets(set1, set2);
            chunkSets.remove(size - 1);
            chunkSets.remove(size - 2);
            chunkSets.add(sorted);
        }

        // Add a new chunk.
        ChunkSet next = allocateChunkSet(1);
        chunkSets.add(next);
        return next;
    }

    public void sort() {
        while (chunkSets.size() >= 2) {
            int size = chunkSets.size();
            ChunkSet set1 = chunkSets.get(size - 1);
            ChunkSet set2 = chunkSets.get(size - 2);
            ChunkSet sorted = mergeSortedChunkSets(set1, set2);
            chunkSets.remove(size - 1);
            chunkSets.remove(size - 2);
            chunkSets.add(sorted);
        }
    }

    public void put(int key, int value) {
        getNextChunkSetForPut().put(key, value);
    }

    public void put(long key, long value) {
        getNextChunkSetForPut().put(key, value);
    }

    /**
     * Returns the last value.
     * Requires that the map has been sorted.
     */
    public int set(int key, int value) {
        for (ChunkSet chunkSet : chunkSets) {
            Chunk chunk = chunkSet.getPossibleChunk(key);
            if (chunk != null)
                return chunk.set(key, value);
        }
        throw new NoSuchElementException();
    }

    public @Nullable Integer getInt(int key) {
        for (ChunkSet chunkSet : chunkSets) {
            Integer value = chunkSet.getInt(key);
            if (value != null)
                return value;
        }
        return null;
    }

    public @Nullable Long getLong(long key) {
        for (ChunkSet chunkSet : chunkSets) {
            Long value = chunkSet.getLong(key);
            if (value != null)
                return value;
        }
        return null;
    }

    private void loopChunks(Consumer<Chunk> chunkConsumer) {
        for (ChunkSet chunkSet : chunkSets) {
            for (Chunk chunk : chunkSet.chunks) {
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
        return  (double) overlappingChunks.get() / chunkSets.size();
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

        private final Chunk[] chunks;
        private int emptyChunkIndex = 0;

        public ChunkSet(Chunk[] chunks) {
            if (chunks.length == 0)
                throw new IllegalArgumentException("No chunks");

            this.chunks = chunks;
        }

        public int getChunkCount() {
            return chunks.length;
        }

        public Chunk getChunk(int index) {
            return chunks[index];
        }

        private @Nullable Chunk getNextChunkWithSpace() {
            if (emptyChunkIndex >= chunks.length)
                return null;

            Chunk lastChunk = chunks[emptyChunkIndex];
            if (!lastChunk.isFull())
                return lastChunk;

            Chunk chunk;
            do {
                emptyChunkIndex += 1;
                if (emptyChunkIndex >= chunks.length)
                    return null;

                chunk = chunks[emptyChunkIndex];
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
            return chunks[chunkIndex].getKeyLong(entryIndex);
        }

        public int getValueInt(int index) {
            int chunkIndex = index / entriesPerChunk;
            int entryIndex = index - chunkIndex * entriesPerChunk;
            return chunks[chunkIndex].getValueInt(entryIndex);
        }

        public long getValueLong(int index) {
            int chunkIndex = index / entriesPerChunk;
            int entryIndex = index - chunkIndex * entriesPerChunk;
            return chunks[chunkIndex].getValueLong(entryIndex);
        }

        private @Nullable Chunk getPossibleChunkLinearSearch(int key, int startIndex, int endIndex) {
            long keyUnsigned = Integer.toUnsignedLong(key);

            for (int index = startIndex; index < endIndex; ++index) {
                Chunk chunk = chunks[index];
                if (chunk.mayContain(keyUnsigned))
                    return chunk;
            }
            return null;
        }

        private @Nullable Chunk getPossibleChunkLinearSearch(long key, int startIndex, int endIndex) {
            for (int index = startIndex; index < endIndex; ++index) {
                Chunk chunk = chunks[index];
                if (chunk.mayContain(key))
                    return chunk;
            }
            return null;
        }

        public @Nullable Chunk getPossibleChunk(int key) {
            long keyUnsigned = Integer.toUnsignedLong(key);

            int lower = 0;
            int upper = chunks.length;
            while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
                int middleIndex = lower + (upper - lower) / 2;
                Chunk current = chunks[middleIndex];
                if (current.mayContain(keyUnsigned))
                    return current;

                if (Long.compareUnsigned(current.maxValue, keyUnsigned) > 0) {
                    upper = middleIndex;
                } else {
                    lower = middleIndex + 1;
                }
            }
            return getPossibleChunkLinearSearch(key, lower, upper);
        }

        public @Nullable Chunk getPossibleChunk(long key) {
            int lower = 0;
            int upper = chunks.length;
            while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
                int middleIndex = lower + (upper - lower) / 2;
                Chunk current = chunks[middleIndex];
                if (current.mayContain(key))
                    return current;

                if (Long.compareUnsigned(current.maxValue, key) > 0) {
                    upper = middleIndex;
                } else {
                    lower = middleIndex + 1;
                }
            }
            return getPossibleChunkLinearSearch(key, lower, upper);
        }

        public @Nullable Integer getInt(int key) {
            Chunk chunk = getPossibleChunk(key);
            return chunk == null ? null : chunk.getInt(key);
        }

        public @Nullable Long getLong(long key) {
            Chunk chunk = getPossibleChunk(key);
            return chunk == null ? null : chunk.getLong(key);
        }
    }

    /**
     * A chunk of memory for storing keys and values.
     */
    private class Chunk {

        private final int entryCapacity;
        private final ValueBuffer keyBuffer;
        private final ValueBuffer valueBuffer;
        private int entryCount = 0;
        private long minValue = 0;
        private long maxValue = 0;

        public Chunk() {
            this.entryCapacity = entriesPerChunk;
            this.keyBuffer = keyType.create(entryCapacity);
            this.valueBuffer = valueType.create(entryCapacity);
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

        public int set(int key, int value) {
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

        private int setIndex = 0;
        private int chunkIndex = 0;
        private int entryIndex = 0;
        private final @Nonnull Entry entry = new Entry();

        @Override
        public boolean hasNext() {
            return setIndex < chunkSets.size();
        }

        @Override
        public Entry next() {
            if (setIndex >= chunkSets.size())
                throw new NoSuchElementException();

            ChunkSet chunkSet = chunkSets.get(setIndex);
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
