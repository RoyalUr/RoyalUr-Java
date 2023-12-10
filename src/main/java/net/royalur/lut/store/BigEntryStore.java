package net.royalur.lut.store;

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
 * A big store of key-value entries that is built to reduce memory usage
 * by packing the keys and values together tightly in arrays.
 */
public class BigEntryStore implements Iterable<BigEntryStore.Entry> {

    public static final int DEFAULT_ENTRIES_PER_CHUNK = 8 * 1024;

    private final int entriesPerChunk;
    private final @Nonnull ValueType keyType;
    private final @Nonnull ValueType valueType;
    private final @Nonnull List<ChunkSet> chunkSets;

    public BigEntryStore(
            @Nonnull ValueType keyType,
            @Nonnull ValueType valueType,
            int entriesPerChunk
    ) {
        this.entriesPerChunk = entriesPerChunk;
        this.keyType = keyType;
        this.valueType = valueType;
        this.chunkSets = new ArrayList<>();
    }

    public BigEntryStore(
            @Nonnull ValueType keyType,
            @Nonnull ValueType valueType
    ) {
        this(keyType, valueType, DEFAULT_ENTRIES_PER_CHUNK);
    }

    public int getEntriesPerChunk() {
        return entriesPerChunk;
    }

    public @Nonnull ValueType getKeyType() {
        return keyType;
    }

    public @Nonnull ValueType getValueType() {
        return valueType;
    }

    public boolean isKnownSorted() {
        return chunkSets.size() <= 1;
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
            chunkSet[index] = new Chunk(this);
        }
        return new ChunkSet(this, chunkSet);
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
                output.addEntry(key1, value);

                index1 += 1;
                if (index1 >= chunk1Size)
                    break;

                key1 = input1.getKeyLong(index1);

            } else {
                long value = input2.getValueLong(index2);
                output.addEntry(key2, value);

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
            output.addEntry(key, value);
        }
        while (index2 < chunk2Size) {
            long key = input2.getKeyLong(index2);
            long value = input2.getValueLong(index2);
            index2 += 1;
            output.addEntry(key, value);
        }
        return output;
    }

    private ChunkSet getNextChunkSet() {
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

    public void addEntry(int key, int value) {
        getNextChunkSet().addEntry(key, value);
    }

    public void addEntry(long key, long value) {
        getNextChunkSet().addEntry(key, value);
    }

    /**
     * Returns the last value.
     */
    public int updateEntry(int key, int newValue) {
        if (!isKnownSorted())
            throw new IllegalStateException("The entry store must be sorted to update entries");

        for (ChunkSet chunkSet : chunkSets) {
            Chunk chunk = chunkSet.getPossibleChunk(key);
            if (chunk != null)
                return chunk.updateEntry(key, newValue);
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
            for (Chunk chunk : chunkSet.getChunks()) {
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

            if (entryIndex + 1 >= chunk.getEntryCount()) {
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
