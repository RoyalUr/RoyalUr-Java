package net.royalur.lut.buffer;

import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.DataSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Float64ValueBuffer extends FloatValueBuffer {

    private final double[] buffer;

    public Float64ValueBuffer(int capacity) {
        super(ValueType.FLOAT64, capacity);
        this.buffer = new double[capacity];
    }

    @Override
    public double set(int index, double value) {
        double lastValue = buffer[index];
        buffer[index] = value;
        return lastValue;
    }

    @Override
    public float set(int index, float value) {
        return (float) set(index, (double) value);
    }

    @Override
    public double getDouble(int index) {
        return Byte.toUnsignedLong(getByte(index));
    }

    @Override
    public float getFloat(int index) {
        return Byte.toUnsignedInt(getByte(index));
    }

    @Override
    public void moveIntoPlace(int index, int targetIndex) {
        double value = buffer[index];
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
                outputBuffer.putDouble(buffer[index]);
            }
        }, getType().getByteCount(), startIndex, endIndex);
    }

    @Override
    public void readContents(
            @Nonnull DataSource input, int startIndex, int endIndex
    ) throws IOException {

        for (int index = startIndex; index < endIndex; ++index) {
            buffer[index] = input.readDouble();
        }
    }
}
