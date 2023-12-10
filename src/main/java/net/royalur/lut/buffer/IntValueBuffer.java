package net.royalur.lut.buffer;

import net.royalur.lut.DataSink;
import net.royalur.lut.DataSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public class IntValueBuffer extends ValueBuffer {

    private static final int BINARY_TO_LINEAR_SEARCH_THRESHOLD = 32;

    private static final long INT_MASK = (1L << 32) - 1;
    private final int[] buffer;

    public IntValueBuffer(int capacity) {
        super(ValueType.INT, capacity);
        this.buffer = new int[capacity];
    }

    private void checkValue(long value) {
        if ((value & (~INT_MASK)) != 0)
            throw new ArithmeticException("value cannot be represented in 32 bits");
    }

    @Override
    public long set(int index, long value) {
        checkValue(value);
        int lastValue = set(index, (int) value);
        return Integer.toUnsignedLong(lastValue);
    }

    @Override
    public int set(int index, int value) {
        int lastValue = buffer[index];
        buffer[index] = value;
        return lastValue;
    }

    @Override
    public short set(int index, short value) {
        throw new UnsupportedOperationException("This is an int array");
    }

    @Override
    public byte set(int index, byte value) {
        throw new UnsupportedOperationException("This is an int array");
    }

    @Override
    public long getLong(int index) {
        return Integer.toUnsignedLong(getInt(index));
    }

    @Override
    public int getInt(int index) {
        return buffer[index];
    }

    @Override
    public short getShort(int index) {
        throw new UnsupportedOperationException("This is an int buffer");
    }

    @Override
    public byte getByte(int index) {
        throw new UnsupportedOperationException("This is an int buffer");
    }

    @Override
    public int indexOf(long value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOf((int) value, startIndex, endIndex);
    }

    @Override
    public int indexOf(int value, int startIndex, int endIndex) {
        int[] buffer = this.buffer;
        for (int index = startIndex; index < endIndex; ++index) {
            if (buffer[index] == value)
                return index;
        }
        return -1;
    }

    @Override
    public int indexOf(short value, int startIndex, int endIndex) {
        return indexOf(Short.toUnsignedInt(value), startIndex, endIndex);
    }

    @Override
    public int indexOf(byte value, int startIndex, int endIndex) {
        return indexOf(Byte.toUnsignedInt(value), startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(long value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOfBinarySearch((int) value, startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(int value, int startIndex, int endIndex) {
        int lower = startIndex;
        int upper = endIndex;
        while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
            int middleIndex = lower + (upper - lower) / 2;
            int current = buffer[middleIndex];
            if (current == value)
                return middleIndex;

            if (Integer.compareUnsigned(current, value) > 0) {
                upper = middleIndex;
            } else {
                lower = middleIndex + 1;
            }
        }
        return indexOf(value, lower, upper);
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
        int value = buffer[index];
        for (int targetIndex = index; targetIndex > 0; targetIndex--) {
            int compareValue = buffer[targetIndex - 1];
            if (Integer.compareUnsigned(value, compareValue) >= 0) {
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
        int value = buffer[index];
        for (int moveIndex = index; moveIndex > targetIndex; moveIndex--) {
            buffer[moveIndex] = buffer[moveIndex - 1];
        }
        buffer[targetIndex] = value;
    }

    @Override
    public void writeContents(@Nonnull DataSink output) throws IOException {
        output.write((outputBuffer) -> {
            for (int value : buffer) {
                outputBuffer.putInt(value);
            }
        });
    }

    @Override
    public void readContents(@Nonnull DataSource input) throws IOException {
        for (int index = 0; index < buffer.length; ++index) {
            buffer[index] = input.readInt();
        }
    }
}
