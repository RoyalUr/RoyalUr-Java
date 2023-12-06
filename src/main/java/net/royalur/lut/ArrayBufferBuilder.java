package net.royalur.lut;

public interface ArrayBufferBuilder {

    static ArrayBufferBuilder LONG = ArrayBuffer.LongArrayBuffer::new;
    static ArrayBufferBuilder INT = ArrayBuffer.IntArrayBuffer::new;
    static ArrayBufferBuilder SHORT = ArrayBuffer.ShortArrayBuffer::new;
    static ArrayBufferBuilder BYTE = ArrayBuffer.ByteArrayBuffer::new;

    ArrayBuffer create(int capacity);
}
