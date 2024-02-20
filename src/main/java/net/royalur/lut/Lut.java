package net.royalur.lut;

import net.royalur.lut.store.Chunk;
import net.royalur.lut.store.DataSink;
import net.royalur.model.dice.Roll;
import net.royalur.notation.JsonNotation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Lut<R extends Roll> {

    private final LutMetadata<R> metadata;
    private final Chunk entries;

    public Lut(
            LutMetadata<R> metadata,
            Chunk entries
    ) {
        this.metadata = metadata;
        this.entries = entries;
    }

    public void write(JsonNotation<?, ?, R> notation, FileChannel channel) throws IOException {
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        outputBuffer.order(ByteOrder.BIG_ENDIAN);
        DataSink output = new DataSink.FileDataSink(channel, outputBuffer);
        write(notation, output);
    }

    public void write(JsonNotation<?, ?, R> notation, DataSink output) throws IOException {
        output.write(buffer -> {
            buffer.put(new byte[] {0x52, 0x47, 0x55, 0x00});

            String metadataStr = metadata.encode(notation);
            byte[] metadataBytes = metadataStr.getBytes(StandardCharsets.UTF_8);
            buffer.putInt(metadataBytes.length);
            buffer.put(metadataBytes);
        });

        output.write(buffer -> {
            buffer.putInt(entries.getKeyType().ordinal());
            buffer.putInt(entries.getValueType().ordinal());
            buffer.putInt(entries.getEntryCount());
        });
        entries.writeKeys(output);
        entries.writeValues(output);
    }
}
