package net.royalur.lut.buffer;

import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.DataSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public class UInt16ValueBuffer extends IntValueBuffer {

    private static final int BINARY_TO_LINEAR_SEARCH_THRESHOLD = 32;

    private static final long SHORT_MASK = (1L << 16) - 1;
    private final short[] buffer;

    public UInt16ValueBuffer(int capacity) {
        super(ValueType.UINT16, capacity);
        this.buffer = new short[capacity];
    }

    private void checkValue(long value) {
        if ((value & (~SHORT_MASK)) != 0)
            throw new ArithmeticException("value cannot be represented in 16 bits");
    }

    @Override
    public long set(int index, long value) {
        checkValue(value);
        short lastValue = set(index, (short) value);
        return Short.toUnsignedLong(lastValue);
    }

    @Override
    public int set(int index, int value) {
        checkValue(value);
        short lastValue = set(index, (short) value);
        return Short.toUnsignedInt(lastValue);
    }

    @Override
    public short set(int index, short value) {
        short lastValue = buffer[index];
        buffer[index] = value;
        return lastValue;
    }

    @Override
    public byte set(int index, byte value) {
        throw new UnsupportedOperationException("This is a short buffer");
    }

    @Override
    public long getLong(int index) {
        return Short.toUnsignedLong(getShort(index));
    }

    @Override
    public int getInt(int index) {
        return Short.toUnsignedInt(getShort(index));
    }

    @Override
    public short getShort(int index) {
        return buffer[index];
    }

    @Override
    public byte getByte(int index) {
        throw new UnsupportedOperationException("This is a short buffer");
    }

    @Override
    public int indexOf(long value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOf((short) value, startIndex, endIndex);
    }

    @Override
    public int indexOf(int value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOf((short) value, startIndex, endIndex);
    }

    @Override
    public int indexOf(short value, int startIndex, int endIndex) {
        short[] buffer = this.buffer;
        for (int index = startIndex; index < endIndex; ++index) {
            if (buffer[index] == value)
                return index;
        }
        return -1;
    }

    @Override
    public int indexOf(byte value, int startIndex, int endIndex) {
        return indexOf((short) Byte.toUnsignedInt(value), startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(long value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOfBinarySearch((short) value, startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(int value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOfBinarySearch((short) value, startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(short value, int startIndex, int endIndex) {
        int lower = startIndex;
        int upper = endIndex;
        while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
            int middleIndex = lower + (upper - lower) / 2;
            short current = buffer[middleIndex];
            if (current == value)
                return middleIndex;

            if (Short.compareUnsigned(current, value) > 0) {
                upper = middleIndex;
            } else {
                lower = middleIndex + 1;
            }
        }
        return indexOf(value, lower, upper);
    }

    @Override
    public int indexOfBinarySearch(byte value, int startIndex, int endIndex) {
        return indexOfBinarySearch(Byte.toUnsignedLong(value), startIndex, endIndex);
    }

    @Override
    public int moveIntoSortedPlace(int index) {
        short value = buffer[index];
        for (int targetIndex = index; targetIndex > 0; targetIndex--) {
            short compareValue = buffer[targetIndex - 1];
            if (Short.compareUnsigned(value, compareValue) >= 0) {
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
        short value = buffer[index];
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
                outputBuffer.putShort(buffer[index]);
            }
        }, getType().getByteCount(), startIndex, endIndex);
    }

    @Override
    public void readContents(
            @Nonnull DataSource input, int startIndex, int endIndex
    ) throws IOException {

        for (int index = startIndex; index < endIndex; ++index) {
            buffer[index] = input.readShort();
        }
    }
}
