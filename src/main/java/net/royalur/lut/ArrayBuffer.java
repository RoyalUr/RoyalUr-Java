package net.royalur.lut;

/**
 * A simple generic array buffer that casts down from long.
 */
public abstract class ArrayBuffer {

    private static final int BINARY_TO_LINEAR_SEARCH_THRESHOLD = 32;

    private final int capacity;

    public ArrayBuffer(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public abstract long set(int index, long value);

    public abstract int set(int index, int value);

    public abstract short set(int index, short value);

    public abstract byte set(int index, byte value);

    public abstract long getLong(int index);

    public abstract int getInt(int index);

    public abstract short getShort(int index);

    public abstract byte getByte(int index);

    public abstract int indexOf(long value, int startIndex, int endIndex);

    public abstract int indexOf(int value, int startIndex, int endIndex);

    public abstract int indexOf(short value, int startIndex, int endIndex);

    public abstract int indexOf(byte value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(long value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(int value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(short value, int startIndex, int endIndex);

    /**
     * Expects the buffer to be sorted in unsigned ascending order.
     */
    public abstract int indexOfBinarySearch(byte value, int startIndex, int endIndex);

    /**
     * Returns the final index that the value was moved to.
     */
    public abstract int moveIntoSortedPlace(int index);

    /**
     * Moves the value to targetIndex, and shifts all values in between.
     */
    public abstract void moveIntoPlace(int index, int targetIndex);

    public abstract void swap(int index1, int index2);

    public static class LongArrayBuffer extends ArrayBuffer {
        private final long[] buffer;

        public LongArrayBuffer(int capacity) {
            super(capacity);
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
        public void swap(int index1, int index2) {
            long temp = buffer[index1];
            buffer[index1] = buffer[index2];
            buffer[index2] = temp;
        }
    }

    public static class IntArrayBuffer extends ArrayBuffer {
        private static final long INT_MASK = (1L << 32) - 1;
        private final int[] buffer;

        public IntArrayBuffer(int capacity) {
            super(capacity);
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
        public void swap(int index1, int index2) {
            int temp = buffer[index1];
            buffer[index1] = buffer[index2];
            buffer[index2] = temp;
        }
    }

    public static class ShortArrayBuffer extends ArrayBuffer {
        private static final long SHORT_MASK = (1L << 16) - 1;
        private final short[] buffer;

        public ShortArrayBuffer(int capacity) {
            super(capacity);
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
        public void swap(int index1, int index2) {
            short temp = buffer[index1];
            buffer[index1] = buffer[index2];
            buffer[index2] = temp;
        }
    }

    public static class ByteArrayBuffer extends ArrayBuffer {
        private static final long BYTE_MASK = (1L << 8) - 1;
        private final byte[] buffer;

        public ByteArrayBuffer(int capacity) {
            super(capacity);
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
        public void swap(int index1, int index2) {
            byte temp = buffer[index1];
            buffer[index1] = buffer[index2];
            buffer[index2] = temp;
        }
    }
}
