package net.royalur.lut;

/**
 * A simple generic array buffer that casts down from long.
 */
public abstract class ArrayBuffer {

    private final int capacity;

    public ArrayBuffer(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    public abstract void set(int index, long value);

    public abstract void set(int index, int value);

    public abstract void set(int index, short value);

    public abstract void set(int index, byte value);

    public abstract long getLong(int index);

    public abstract int getInt(int index);

    public abstract short getShort(int index);

    public abstract byte getByte(int index);

    public abstract int indexOf(long value, int startIndex, int endIndex);

    public abstract int indexOf(int value, int startIndex, int endIndex);

    public abstract int indexOf(short value, int startIndex, int endIndex);

    public abstract int indexOf(byte value, int startIndex, int endIndex);

    public static class LongArrayBuffer extends ArrayBuffer {
        private final long[] buffer;

        public LongArrayBuffer(int capacity) {
            super(capacity);
            this.buffer = new long[capacity];
        }

        @Override
        public void set(int index, long value) {
            buffer[index] = value;
        }

        @Override
        public void set(int index, int value) {
            set(index, (long) value);
        }

        @Override
        public void set(int index, short value) {
            set(index, (long) value);
        }

        @Override
        public void set(int index, byte value) {
            set(index, (long) value);
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
        public void set(int index, long value) {
            checkValue(value);
            set(index, (int) value);
        }

        @Override
        public void set(int index, int value) {
            buffer[index] = value;
        }

        @Override
        public void set(int index, short value) {
            set(index, (int) value);
        }

        @Override
        public void set(int index, byte value) {
            set(index, (int) value);
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
            throw new UnsupportedOperationException("This is a long buffer");
        }

        @Override
        public byte getByte(int index) {
            throw new UnsupportedOperationException("This is a long buffer");
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
        public void set(int index, long value) {
            checkValue(value);
            set(index, (short) value);
        }

        @Override
        public void set(int index, int value) {
            checkValue(value);
            set(index, (short) value);
        }

        @Override
        public void set(int index, short value) {
            checkValue(value);
            buffer[index] = value;
        }

        @Override
        public void set(int index, byte value) {
            set(index, (short) value);
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
            throw new UnsupportedOperationException("This is a long buffer");
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
        public void set(int index, long value) {
            checkValue(value);
            set(index, (byte) value);
        }

        @Override
        public void set(int index, int value) {
            checkValue(value);
            set(index, (byte) value);
        }

        @Override
        public void set(int index, short value) {
            checkValue(value);
            set(index, (byte) value);
        }

        @Override
        public void set(int index, byte value) {
            buffer[index] = value;
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
    }
}
