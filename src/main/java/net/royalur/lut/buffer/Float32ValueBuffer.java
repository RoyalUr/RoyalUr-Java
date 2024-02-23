package net.royalur.lut.buffer;

import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.DataSource;

import java.io.IOException;

public class Float32ValueBuffer extends FloatValueBuffer {

    private final float[] buffer;

    public Float32ValueBuffer(int capacity) {
        super(ValueType.FLOAT32, capacity);
        this.buffer = new float[capacity];
    }

    @Override
    public double set(int index, double value) {
        return set(index, (float) value);
    }

    @Override
    public float set(int index, float value) {
        float lastValue = buffer[index];
        buffer[index] = value;
        return lastValue;
    }

    @Override
    public double getDouble(int index) {
        return buffer[index];
    }

    @Override
    public float getFloat(int index) {
        return buffer[index];
    }

    @Override
    public void moveIntoPlace(int index, int targetIndex) {
        float value = buffer[index];
        for (int moveIndex = index; moveIndex > targetIndex; moveIndex--) {
            buffer[moveIndex] = buffer[moveIndex - 1];
        }
        buffer[targetIndex] = value;
    }

    @Override
    public void writeContents(
            DataSink output, int startIndex, int endIndex
    ) throws IOException {

        output.writeChunked((outputBuffer, fromIndex, toIndex) -> {
            for (int index = fromIndex; index < toIndex; ++index) {
                outputBuffer.putFloat(buffer[index]);
            }
        }, getType().getByteCount(), startIndex, endIndex);
    }

    @Override
    public void readContents(
            DataSource input, int startIndex, int endIndex
    ) throws IOException {

        for (int index = startIndex; index < endIndex; ++index) {
            buffer[index] = input.readFloat();
        }
    }
}
