package net.royalur.lut.buffer;

import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.DataSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Int8ValueBuffer extends IntValueBuffer {

    private static final int BINARY_TO_LINEAR_SEARCH_THRESHOLD = 32;

    private static final long BYTE_MASK = (1L << 8) - 1;
    private final byte[] buffer;

    public Int8ValueBuffer(int capacity) {
        super(ValueType.INT8, capacity);
        this.buffer = new byte[capacity];
    }

    private void checkValue(long value) {
        if ((value & (~BYTE_MASK)) != 0)
            throw new ArithmeticException("value cannot be represented in 8 bits");
    }

    @Override
    public long set(int index, long value) {
        checkValue(value);
        byte lastValue = set(index, (byte) value);
        return Byte.toUnsignedLong(lastValue);
    }

    @Override
    public int set(int index, int value) {
        checkValue(value);
        byte lastValue = set(index, (byte) value);
        return Byte.toUnsignedInt(lastValue);
    }

    @Override
    public short set(int index, short value) {
        checkValue(value);
        byte lastValue = set(index, (byte) value);
        return (short) Byte.toUnsignedInt(lastValue);
    }

    @Override
    public byte set(int index, byte value) {
        byte lastValue = buffer[index];
        buffer[index] = value;
        return lastValue;
    }

    @Override
    public long getLong(int index) {
        return Byte.toUnsignedLong(getByte(index));
    }

    @Override
    public int getInt(int index) {
        return Byte.toUnsignedInt(getByte(index));
    }

    @Override
    public short getShort(int index) {
        return (short) Byte.toUnsignedInt(getByte(index));
    }

    @Override
    public byte getByte(int index) {
        return buffer[index];
    }

    @Override
    public int indexOf(long value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOf((byte) value, startIndex, endIndex);
    }

    @Override
    public int indexOf(int value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOf((byte) value, startIndex, endIndex);
    }

    @Override
    public int indexOf(short value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOf((byte) value, startIndex, endIndex);
    }

    @Override
    public int indexOf(byte value, int startIndex, int endIndex) {
        byte[] buffer = this.buffer;
        for (int index = startIndex; index < endIndex; ++index) {
            if (buffer[index] == value)
                return index;
        }
        return -1;
    }

    @Override
    public int indexOfBinarySearch(long value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOfBinarySearch((byte) value, startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(int value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOfBinarySearch((byte) value, startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(short value, int startIndex, int endIndex) {
        checkValue(value);
        return indexOfBinarySearch((byte) value, startIndex, endIndex);
    }

    @Override
    public int indexOfBinarySearch(byte value, int startIndex, int endIndex) {
        int lower = startIndex;
        int upper = endIndex;
        while (upper > lower + BINARY_TO_LINEAR_SEARCH_THRESHOLD) {
            int middleIndex = lower + (upper - lower) / 2;
            byte current = buffer[middleIndex];
            if (current == value)
                return middleIndex;

            if (Byte.compareUnsigned(current, value) > 0) {
                upper = middleIndex;
            } else {
                lower = middleIndex + 1;
            }
        }
        return indexOf(value, lower, upper);
    }

    @Override
    public int moveIntoSortedPlace(int index) {
        byte value = buffer[index];
        for (int targetIndex = index; targetIndex > 0; targetIndex--) {
            byte compareValue = buffer[targetIndex - 1];
            if (Byte.compareUnsigned(value, compareValue) >= 0) {
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
        byte value = buffer[index];
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
            outputBuffer.put(buffer, fromIndex, toIndex - fromIndex);
        }, getType().getByteCount(), startIndex, endIndex);
    }

    @Override
    public void readContents(
            @Nonnull DataSource input, int startIndex, int endIndex
    ) throws IOException {

        for (int index = startIndex; index < endIndex; ++index) {
            buffer[index] = input.readByte();
        }
    }
}
