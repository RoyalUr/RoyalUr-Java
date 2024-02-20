package net.royalur.lut.buffer;

import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.DataSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Int64ValueBuffer extends IntValueBuffer {

    private static final int BINARY_TO_LINEAR_SEARCH_THRESHOLD = 32;

    private final long[] buffer;

    public Int64ValueBuffer(int capacity) {
        super(ValueType.INT64, capacity);
        this.buffer = new long[capacity];
    }

    @Override
    public long set(int index, long value) {
        long lastValue = buffer[index];
        buffer[index] = value;
        return lastValue;
    }

    @Override
    public int set(int index, int value) {
        throw new UnsupportedOperationException("This is a long array");
    }

    @Override
    public short set(int index, short value) {
        throw new UnsupportedOperationException("This is a long array");
    }

    @Override
    public byte set(int index, byte value) {
        throw new UnsupportedOperationException("This is a long array");
    }

    @Override
    public long getLong(int index) {
        return buffer[index];
    }

    @Override
    public int getInt(int index) {
        throw new UnsupportedOperationException("This is a long buffer");
    }

    @Override
    public short getShort(int index) {
        throw new UnsupportedOperationException("This is a long buffer");
    }

    @Override
    public byte getByte(int index) {
        throw new UnsupportedOperationException("This is a long buffer");
    }

    @Override
    public int indexOf(long value, int startIndex, int endIndex) {
        long[] buffer = this.buffer;
        for (int index = startIndex; index < endIndex; ++index) {
            if (buffer[index] == value)
                return index;
        }
        return -1;
    }

    @Override
    public int indexOf(int value, int startIndex, int endIndex) {
        return indexOf(Integer.toUnsignedLong(value), startIndex, endIndex);
    }

    @Override
    public int indexOf(short value, int startIndex, int endIndex) {
        return indexOf(Short.toUnsignedLong(value), startIndex, endIndex);
    }

    @Override
    public int indexOf(byte value, int startIndex, int endIndex) {
        return indexOf(Byte.toUnsignedLong(value), startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(long value, int startIndex, int endIndex) {
        int lower = startIndex;
        int upper = endIndex;
        while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
            int middleIndex = lower + (upper - lower) / 2;
            long current = buffer[middleIndex];
            if (current == value)
                return middleIndex;

            if (Long.compareUnsigned(current, value) > 0) {
                upper = middleIndex;
            } else {
                lower = middleIndex + 1;
            }
        }
        return indexOf(value, lower, upper);
    }

    @Override
    public int indexOfBinarySearch(int value, int startIndex, int endIndex) {
        return indexOfBinarySearch(Integer.toUnsignedLong(value), startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(short value, int startIndex, int endIndex) {
        return indexOfBinarySearch(Short.toUnsignedLong(value), startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(byte value, int startIndex, int endIndex) {
        return indexOfBinarySearch(Byte.toUnsignedLong(value), startIndex, endIndex);
    }

    @Override
    public int moveIntoSortedPlace(int index) {
        long value = buffer[index];
        for (int targetIndex = index; targetIndex > 0; targetIndex--) {
            long compareValue = buffer[targetIndex - 1];
            if (Long.compareUnsigned(value, compareValue) >= 0) {
                buffer[targetIndex] = value;
                return targetIndex;
            }
            buffer[targetIndex] = compareValue;
        }
        buffer[0] = value;
        return 0;
    }

    @Override
    public void moveIntoPlace(int index, int targetIndex) {
        long value = buffer[index];
        for (int moveIndex = index; moveIndex > targetIndex; moveIndex--) {
            buffer[moveIndex] = buffer[moveIndex - 1];
        }
        buffer[targetIndex] = value;
    }

    @Override
    public void writeContents(
            @Nonnull DataSink output, int startIndex, int endIndex
    ) throws IOException {

        output.writeChunked((outputBuffer, fromIndex, toIndex) -> {
            for (int index = fromIndex; index < toIndex; ++index) {
                outputBuffer.putLong(buffer[index]);
            }
        }, getType().getByteCount(), startIndex, endIndex);
    }

    @Override
    public void readContents(
            @Nonnull DataSource input, int startIndex, int endIndex
    ) throws IOException {

        for (int index = startIndex; index < endIndex; ++index) {
            buffer[index] = input.readLong();
        }
    }
}
