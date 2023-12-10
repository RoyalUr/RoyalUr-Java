package net.royalur.lut;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.function.Consumer;

/**
 * Provides an interface to write binary data.
 */
public abstract class DataSink {

    public abstract void write(@Nonnull Consumer<ByteBuffer> writeFn) throws IOException;

    /**
     * Provides an interface to write binary data to a file.
     */
    public static class FileDataSink extends DataSink {

        private final @Nonnull FileChannel channel;
        private final @Nonnull ByteBuffer workingBuffer;

        public FileDataSink(
                @Nonnull FileChannel channel,
                @Nonnull ByteBuffer workingBuffer
        ) {
            this.channel = channel;
            this.workingBuffer = workingBuffer;
        }

        public void write(@Nonnull Consumer<ByteBuffer> writeFn) throws IOException {
            workingBuffer.position(0);
            workingBuffer.limit(workingBuffer.capacity());

            writeFn.accept(workingBuffer);

            workingBuffer.limit(workingBuffer.position());
            workingBuffer.position(0);
            int written = channel.write(workingBuffer);
            if (written != workingBuffer.limit())
                throw new IOException("Whole buffer was not written!");
        }
    }
}
