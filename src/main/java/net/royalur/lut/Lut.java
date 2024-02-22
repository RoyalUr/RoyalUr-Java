package net.royalur.lut;

import net.royalur.lut.buffer.Float32ValueBuffer;
import net.royalur.lut.buffer.FloatValueBuffer;
import net.royalur.lut.buffer.Percent16ValueBuffer;
import net.royalur.lut.buffer.UInt32ValueBuffer;
import net.royalur.lut.store.DataSource;
import net.royalur.lut.store.LutMap;
import net.royalur.lut.store.DataSink;
import net.royalur.model.dice.Roll;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleBoard;
import net.royalur.rules.simple.fast.FastSimpleGame;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

public class Lut<R extends Roll> {

    public static final byte[] MAGIC = new byte[] {0x52, 0x47, 0x55};
    public static final byte VERSION_0 = (byte) 0;
    public static final byte LATEST_VERSION = VERSION_0;

    private final GameStateEncoding encoding;
    private final LutMetadata<R> metadata;
    private final LutMap[] maps;

    public Lut(
            GameStateEncoding encoding,
            LutMetadata<R> metadata,
            LutMap[] maps
    ) {
        this.encoding = encoding;
        this.metadata = metadata;
        this.maps = maps;
    }

    public GameStateEncoding getGameStateEncoding() {
        return encoding;
    }

    public LutMetadata<R> getMetadata() {
        return metadata;
    }

    public LutMap getMap(int upperKey) {
        return maps[upperKey];
    }

    private static void reversePlayers(FastSimpleGame input, FastSimpleGame output) {
        output.isLightTurn = !input.isLightTurn;
        output.isFinished = input.isFinished;
        output.rollValue = input.rollValue;
        output.dark.score = input.light.score;
        output.dark.pieces = input.light.pieces;
        output.light.score = input.dark.score;
        output.light.pieces = input.dark.pieces;

        FastSimpleBoard inputBoard = input.board;
        FastSimpleBoard outputBoard = output.board;

        int width = input.board.width;
        int height = input.board.height;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                int fromIndex = inputBoard.calcTileIndex(x, y);
                int toIndex = outputBoard.calcTileIndex(width - x - 1, y);
                outputBoard.set(toIndex, -1 * inputBoard.get(fromIndex));
            }
        }
    }

    private long calcSymmetricalKey(FastSimpleGame game, @Nullable FastSimpleGame tempGame) {
        FastSimpleGame keyGame = game;
        if (!game.isLightTurn) {
            if (tempGame == null) {
                tempGame = new FastSimpleGame(metadata.getGameSettings());
            }
            reversePlayers(game, tempGame);
            keyGame = tempGame;
        }
        return encoding.encodeGameState(keyGame);
    }

    /**
     * Assumes that the game is using symmetrical paths.
     * This function is much slower than if you provide a tempGame.
     */
    public double getLightWinPercent(FastSimpleGame game) {
        return getLightWinPercent(game, null);
    }

    /**
     * Assumes that the game is using symmetrical paths.
     */
    public double getLightWinPercent(FastSimpleGame game, @Nullable FastSimpleGame tempGame) {
        long key = calcSymmetricalKey(game, tempGame);
        int upperKey = GameStateEncoding.calcUpperKey(key);
        int lowerKey = GameStateEncoding.calcLowerKey(key);
        double winPercent = maps[upperKey].getDouble(lowerKey);
        return (game.isLightTurn ? winPercent : 100.0 - winPercent);
    }

    public double updateLightWinPercent(FastSimpleGame game, double winPercent) {
        if (!game.isLightTurn) {
            throw new IllegalArgumentException(
                    "Only game states where it is the light player's turn are supported by this method"
            );
        }
        long key = encoding.encodeGameState(game);
        int upperKey = GameStateEncoding.calcUpperKey(key);
        int lowerKey = GameStateEncoding.calcLowerKey(key);
        return maps[upperKey].set(lowerKey, winPercent);
    }

    private static Percent16ValueBuffer convertToPercent16(FloatValueBuffer buffer) {
        if (buffer instanceof Percent16ValueBuffer)
            return (Percent16ValueBuffer) buffer;

        int capacity = buffer.getCapacity();
        Percent16ValueBuffer newBuffer = new Percent16ValueBuffer(capacity);
        for (int index = 0; index < capacity; ++index) {
            newBuffer.set(index, buffer.getDouble(index));
        }
        return newBuffer;
    }

    private static Float32ValueBuffer copyAsFloat32(FloatValueBuffer buffer) {
        int capacity = buffer.getCapacity();
        Float32ValueBuffer newBuffer = new Float32ValueBuffer(capacity);
        for (int index = 0; index < capacity; ++index) {
            newBuffer.set(index, buffer.getDouble(index));
        }
        return newBuffer;
    }

    public Lut<R> copyValuesToFloat32() {
        LutMap[] newMaps = new LutMap[maps.length];
        for (int index = 0; index < maps.length; ++index) {
            LutMap oldMap = maps[index];
            Float32ValueBuffer valueBuffer = copyAsFloat32(oldMap.getValueBuffer());
            newMaps[index] = new LutMap(
                    oldMap.getEntryCount(), oldMap.getKeyBuffer(), valueBuffer
            );
        }
        return new Lut<>(
                encoding,
                metadata,
                newMaps
        );
    }

    public void write(JsonNotation<?, ?, R> notation, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            write(notation, fos.getChannel());
        }
    }

    public void write(JsonNotation<?, ?, R> notation, FileChannel channel) throws IOException {
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        outputBuffer.order(ByteOrder.BIG_ENDIAN);
        DataSink output = new DataSink.FileDataSink(channel, outputBuffer);
        write(notation, output);
    }

    public void write(JsonNotation<?, ?, R> notation, DataSink output) throws IOException {
        output.write(buffer -> {
            buffer.put(MAGIC);
            buffer.put(LATEST_VERSION);

            String metadataStr = metadata.encode(notation);
            byte[] metadataBytes = metadataStr.getBytes(StandardCharsets.UTF_8);
            buffer.putInt(metadataBytes.length);
            buffer.put(metadataBytes);
        });

        output.write(buffer -> {
            buffer.putInt(maps.length);
            for (LutMap map : maps) {
                buffer.putInt(map.getEntryCount());
            }
        });

        for (LutMap map : maps) {
            map.getKeyBuffer().writeContents(output);
        }
        for (LutMap map : maps) {
            FloatValueBuffer buffer = map.getValueBuffer();
            Percent16ValueBuffer percentBuffer = convertToPercent16(buffer);
            percentBuffer.writeContents(output);
        }
    }

    public static <R extends Roll> Lut<R> read(
            JsonNotation<?, ?, R> jsonNotation,
            GameStateEncoding encoding,
            File file
    ) throws IOException {

        try (FileInputStream fis = new FileInputStream(file)) {
            return read(jsonNotation, encoding, fis.getChannel());
        }
    }

    public static <R extends Roll> Lut<R> read(
            JsonNotation<?, ?, R> jsonNotation,
            GameStateEncoding encoding,
            FileChannel channel
    ) throws IOException {

        ByteBuffer workingBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        workingBuffer.order(ByteOrder.BIG_ENDIAN);
        DataSource source = new DataSource.FileDataSource(channel, workingBuffer);
        return read(jsonNotation, encoding, source);
    }

    public static <R extends Roll> Lut<R> read(
            JsonNotation<?, ?, R> jsonNotation,
            GameStateEncoding encoding,
            DataSource source
    ) throws IOException {

        byte[] magic = source.readBytes(Lut.MAGIC.length);
        for (int index = 0; index < magic.length; ++index) {
            if (magic[index] != Lut.MAGIC[index])
                throw new IOException("Magic does not match");
        }
        byte version = source.readByte();
        if (version != Lut.VERSION_0)
            throw new IOException("Unsupported file version: " + Byte.toUnsignedInt(version));

        int metadataByteCount = source.readInt();
        byte[] metadataBytes = source.readBytes(metadataByteCount);
        String metadataJson = new String(metadataBytes, StandardCharsets.UTF_8);
        LutMetadata<R> metadata = LutMetadata.decode(jsonNotation, metadataJson);

        int mapCount = source.readInt();
        int[] mapEntryCounts = new int[mapCount];
        UInt32ValueBuffer[] mapKeyBuffers = new UInt32ValueBuffer[mapCount];
        FloatValueBuffer[] mapValueBuffers = new FloatValueBuffer[mapCount];

        for (int index = 0; index < mapCount; ++index) {
            int entryCount = source.readInt();
            mapEntryCounts[index] = entryCount;
            mapKeyBuffers[index] = new UInt32ValueBuffer(entryCount);
            mapValueBuffers[index] = new Percent16ValueBuffer(entryCount);
        }
        for (int index = 0; index < mapCount; ++index) {
            mapKeyBuffers[index].readContents(source);
        }
        for (int index = 0; index < mapCount; ++index) {
            mapValueBuffers[index].readContents(source);
        }

        LutMap[] maps = new LutMap[mapCount];
        for (int index = 0; index < mapCount; ++index) {
            maps[index] = new LutMap(
                    mapEntryCounts[index],
                    mapKeyBuffers[index],
                    mapValueBuffers[index]
            );
        }
        return new Lut<>(encoding, metadata, maps);
    }
}
