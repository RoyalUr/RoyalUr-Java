package net.royalur.lut.store;

import java.nio.ByteBuffer;

@FunctionalInterface
public interface ChunkedWriter {

    void write(ByteBuffer outputBuffer, int fromIndex, int toIndex);
}
