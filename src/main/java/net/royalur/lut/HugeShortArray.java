package net.royalur.lut;

/**
 * Stores one huge array as many large arrays that can be individually serialised to disk.
 */
public class HugeShortArray {

    private final long CHUNK_SIZE = 1024 * 1024;

    private final Chunk[] chunks;

    public HugeShortArray(long length) {
        long chunkCountLong = (length + CHUNK_SIZE - 1) / CHUNK_SIZE;
        if (chunkCountLong > CHUNK_SIZE)
            throw new IllegalArgumentException("Too big!");

        int chunkCount = Math.toIntExact(chunkCountLong);
        this.chunks = new Chunk[chunkCount];
        for (int index = 0; index < chunkCount; ++index) {
            long startIndex = index * CHUNK_SIZE;
            long endIndex = Math.min(length - 1, (index + 1) * CHUNK_SIZE);
            long chunkLengthLong = endIndex - startIndex;
            int chunkLength = Math.toIntExact(chunkLengthLong);
            this.chunks[index] = new Chunk(startIndex, new short[chunkLength]);
        }
    }

    public short get(long index) {
        int chunkNo = Math.toIntExact(index / CHUNK_SIZE);
        Chunk chunk = chunks[chunkNo];
        return chunk.data[Math.toIntExact(index - chunk.startIndex)];
    }

    public void set(long index, short value) {
        int chunkNo = Math.toIntExact(index / CHUNK_SIZE);
        Chunk chunk = chunks[chunkNo];
        chunk.data[Math.toIntExact(index - chunk.startIndex)] = value;
    }

    private static class Chunk {

        public final long startIndex;
        public final short[] data;

        public Chunk(long startIndex, short[] data) {
            this.startIndex = startIndex;
            this.data = data;
        }
    }
}
