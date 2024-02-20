package net.royalur.lut.store;

import javax.annotation.Nonnull;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Provides an interface to read binary data.
 */
public abstract class DataSource {

    public abstract byte[] readBytes(int length) throws IOException;

    public abstract double readDouble() throws IOException;

    public abstract float readFloat() throws IOException;

    public abstract long readLong() throws IOException;

    public abstract int readInt() throws IOException;

    public abstract short readShort() throws IOException;

    public abstract byte readByte() throws IOException;

    /**
     * Provides an interface to write binary data to a file.
     */
    public static class FileDataSource extends DataSource {

        private final @Nonnull FileChannel channel;
        private final @Nonnull ByteBuffer workingBuffer;

        public FileDataSource(
                @Nonnull FileChannel channel,
                @Nonnull ByteBuffer workingBuffer
        ) {
            this.channel = channel;
            this.workingBuffer = workingBuffer;
            workingBuffer.limit(0);
        }

        private void ensureAvailable(int byteCount) throws IOException {
            int remaining = workingBuffer.remaining();
            if (byteCount <= remaining)
                return;

            for (int index = 0; index < remaining; ++index) {
                workingBuffer.put(index, workingBuffer.get());
            }

            workingBuffer.position(remaining);
            workingBuffer.limit(workingBuffer.capacity());
            int read = channel.read(workingBuffer);
            if (read < 0)
                throw new EOFException();

            workingBuffer.position(0);
            workingBuffer.limit(remaining + read);
            if (workingBuffer.remaining() < byteCount)
                throw new BufferUnderflowException();
        }

        @Override
        public byte[] readBytes(int length) throws IOException {
            if (length > workingBuffer.capacity())
                throw new IOException("Working buffer is not large enough to read " + length + " bytes");

            ensureAvailable(length);
            byte[] dest = new byte[length];
            workingBuffer.get(dest);
            return dest;
        }

        @Override
        public double readDouble() throws IOException {
            ensureAvailable(8);
            return workingBuffer.getDouble();
        }

        @Override
        public float readFloat() throws IOException {
            ensureAvailable(4);
            return workingBuffer.getFloat();
        }

        @Override
        public long readLong() throws IOException {
            ensureAvailable(8);
            return workingBuffer.getLong();
        }

        @Override
        public int readInt() throws IOException {
            ensureAvailable(4);
            return workingBuffer.getInt();
        }

        @Override
        public short readShort() throws IOException {
            ensureAvailable(2);
            return workingBuffer.getShort();
        }

        @Override
        public byte readByte() throws IOException {
            ensureAvailable(1);
            return workingBuffer.get();
        }
    }
}
