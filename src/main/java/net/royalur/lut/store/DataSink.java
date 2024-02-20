package net.royalur.lut.store;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.Consumer;

/**
 * Provides an interface to write binary data.
 */
public abstract class DataSink {

    public abstract void write(
            Consumer<ByteBuffer> writeFn
    ) throws IOException;

    public abstract void writeChunked(
            ChunkedWriter chunkWriter,
            int bytesPerValue,
            int startIndex,
            int endIndex
    ) throws IOException;

    /**
     * Provides an interface to write binary data to a file.
     */
    public static class FileDataSink extends DataSink {

        private final FileChannel channel;
        private final ByteBuffer workingBuffer;

        public FileDataSink(
                FileChannel channel,
                ByteBuffer workingBuffer
        ) {
            this.channel = channel;
            this.workingBuffer = workingBuffer;
        }

        public void write(Consumer<ByteBuffer> writeFn) throws IOException {
            workingBuffer.position(0);
            workingBuffer.limit(workingBuffer.capacity());

            writeFn.accept(workingBuffer);

            workingBuffer.limit(workingBuffer.position());
            workingBuffer.position(0);
            int written = channel.write(workingBuffer);
            if (written != workingBuffer.limit())
                throw new IOException("Whole buffer was not written!");
        }

        public void writeChunked(
                ChunkedWriter chunkWriter,
                int bytesPerValue,
                int startIndex,
                int endIndex
        ) throws IOException {

            int valuesPerChunk = workingBuffer.capacity() / bytesPerValue;

            int index = startIndex;
            while (index < endIndex) {
                int fromIndex = index;
                int toIndex = Math.min(endIndex, index + valuesPerChunk);
                write(outputBuffer -> {
                    chunkWriter.write(outputBuffer, fromIndex, toIndex);
                });
                index += valuesPerChunk;
            }
        }
    }
}
