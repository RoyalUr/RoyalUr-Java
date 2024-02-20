package net.royalur.lut.buffer;

import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.DataSource;

import javax.annotation.Nonnull;
import java.io.IOException;

public class Percent16ValueBuffer extends FloatValueBuffer {

    private static final double MAX_VALUE = (1 << 16) - 1;

    private final UInt16ValueBuffer buffer;

    public Percent16ValueBuffer(int capacity) {
        super(ValueType.PERCENT16, capacity);
        this.buffer = new UInt16ValueBuffer(capacity);
    }

    private static double clampPercentage(double value) {
        return Math.min(Math.max(0.0, value), 100.0);
    }

    private static short encodePercent16(double value) {
        value = clampPercentage(value);
        return (short) Math.round(value / 100.0 * MAX_VALUE);
    }

    private static double decodePercent16(short encoded) {
        return Short.toUnsignedLong(encoded) * 100L / MAX_VALUE;
    }

    @Override
    public double set(int index, double value) {
        double lastValue = getDouble(index);
        buffer.set(index, encodePercent16(value));
        return lastValue;
    }

    @Override
    public float set(int index, float value) {
        return (float) set(index, (double) value);
    }

    @Override
    public double getDouble(int index) {
        return decodePercent16(buffer.getShort(index));
    }

    @Override
    public float getFloat(int index) {
        return (float) getDouble(index);
    }

    @Override
    public void moveIntoPlace(int index, int targetIndex) {
        buffer.moveIntoPlace(index, targetIndex);
    }

    @Override
    public void writeContents(
            @Nonnull DataSink output, int startIndex, int endIndex
    ) throws IOException {

         buffer.writeContents(output, startIndex, endIndex);
    }

    @Override
    public void readContents(
            @Nonnull DataSource input, int startIndex, int endIndex
    ) throws IOException {

        buffer.readContents(input, startIndex, endIndex);
    }
}
