package net.royalur.lut.store;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Manages a set of sorted chunks.
 */
public class ChunkSet {

    private static final int BINARY_TO_LINEAR_SEARCH_THRESHOLD = 8;

    private final @Nonnull BigEntryStore store;
    private final int entriesPerChunk;
    private final @Nonnull Chunk[] chunks;
    private int emptyChunkIndex = 0;
    private long lastKeyAdded = 0;

    public ChunkSet(
            @Nonnull BigEntryStore store,
            @Nonnull Chunk[] chunks
    ) {
        if (chunks.length == 0)
            throw new IllegalArgumentException("No chunks");

        this.store = store;
        this.entriesPerChunk = store.getEntriesPerChunk();
        this.chunks = chunks;
    }

    public int getChunkCount() {
        return chunks.length;
    }

    public @Nonnull Chunk[] getChunks() {
        return chunks;
    }

    public @Nonnull Chunk getChunk(int index) {
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

    private Chunk getNextChunk() {
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

    private void checkAddEntryIsSorted(long key) {
        if (chunks.length <= 1)
            return;

        if (Long.compareUnsigned(key, lastKeyAdded) < 0) {
            throw new IllegalArgumentException(
                    "Adding key could lead this ChunkSet to become unsorted. "
                            + "Keys should be added in sorted order if there is more "
                            + "than one chunk in a ChunkSet"
            );
        }
        lastKeyAdded = key;
    }

    public void addEntry(int key, int value) {
        checkAddEntryIsSorted(Integer.toUnsignedLong(key));
        getNextChunk().addEntry(key, value);
    }

    public void addEntry(long key, long value) {
        checkAddEntryIsSorted(key);
        getNextChunk().addEntry(key, value);
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

            if (Long.compareUnsigned(current.getMaxValue(), keyUnsigned) > 0) {
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

            if (Long.compareUnsigned(current.getMaxValue(), key) > 0) {
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
