package net.royalur.lut;

import net.royalur.lut.buffer.*;
import net.royalur.lut.store.DataSource;
import net.royalur.lut.store.LutMap;
import net.royalur.lut.store.DataSink;
import net.royalur.model.GameSettings;
import net.royalur.model.PlayerType;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.state.EndGameState;
import net.royalur.rules.state.GameState;
import net.royalur.rules.state.WaitingForRollGameState;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

public class Lut {

    public static final byte[] MAGIC = new byte[] {0x52, 0x47, 0x55};
    public static final byte VERSION_0 = (byte) 0;
    public static final byte LATEST_VERSION = VERSION_0;

    private final GameStateEncoding encoding;
    private final LutMetadata metadata;
    private final LutMap[] maps;
    private final FastSimpleGame stateConversionGame;
    private final FastSimpleGame tempGame;

    public Lut(
            GameStateEncoding encoding,
            LutMetadata metadata,
            LutMap[] maps
    ) {
        this.encoding = encoding;
        this.metadata = metadata;
        this.maps = maps;
        this.stateConversionGame = new FastSimpleGame(metadata.getGameSettings());
        this.tempGame = new FastSimpleGame(metadata.getGameSettings());
    }

    /**
     * A different instance of Lut must be used on each thread.
     */
    public Lut shallowCopy() {
        return new Lut(encoding, metadata, maps);
    }

    public int getEntryCount() {
        int size = 0;
        for (LutMap map : maps) {
            size += map.getEntryCount();
        }
        return size;
    }

    public GameStateEncoding getGameStateEncoding() {
        return encoding;
    }

    public LutMetadata getMetadata() {
        return metadata;
    }

    public GameSettings getGameSettings() {
        return metadata.getGameSettings();
    }

    public LutMap getMap(int upperKey) {
        return maps[upperKey];
    }

    public LutMap[] getMaps() {
        return maps;
    }

    /**
     * Assumes that the game is using symmetrical paths.
     * NOT thread-safe.
     */
    public double getLightWinPercent(FastSimpleGame game) {
        return getLightWinPercent(game, tempGame);
    }

    /**
     * Assumes that the game is using symmetrical paths.
     * NOT thread-safe.
     */
    public double getLightWinPercent(GameState state) {
        if (state instanceof EndGameState endState) {
            if (endState.hasWinner())
                return (endState.getWinner() == PlayerType.LIGHT ? 100.0 : 0.0);
            return 50.0;
        }
        if (!(state instanceof WaitingForRollGameState)) {
            throw new IllegalArgumentException(
                    "Can only get the win percentage for end and waiting for roll game states"
            );
        }
        stateConversionGame.copyFrom(state);
        return getLightWinPercent(stateConversionGame, tempGame);
    }

    /**
     * Assumes that the game is using symmetrical paths.
     * This is thread-safe.
     */
    public double getLightWinPercent(FastSimpleGame game, @Nullable FastSimpleGame tempGame) {
        long key = encoding.encodeSymmetricalGameState(game, tempGame);
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

    public Lut convertValueTypes(ValueType newValueType) {
        if (!newValueType.isFloat()) {
            throw new IllegalArgumentException(
                    "Only floating-point value types are supported for Lut, not: " + newValueType
            );
        }

        LutMetadata newMetadata = metadata.copyWithValueType(newValueType);
        LutMap[] newMaps = new LutMap[maps.length];
        for (int index = 0; index < maps.length; ++index) {
            LutMap oldMap = maps[index];
            newMaps[index] = new LutMap(
                    oldMap.getEntryCount(),
                    oldMap.getKeyBuffer(),
                    (FloatValueBuffer) oldMap.getValueBuffer().convertTo(newValueType)
            );
        }
        return new Lut(encoding, newMetadata, newMaps);
    }

    public void write(
            ValueType outputValueType,
            JsonNotation notation,
            File file
    ) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            write(outputValueType, notation, fos.getChannel());
        }
    }

    public void write(
            ValueType outputValueType,
            JsonNotation notation,
            FileChannel channel
    ) throws IOException {
        ByteBuffer outputBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        outputBuffer.order(ByteOrder.BIG_ENDIAN);
        DataSink output = new DataSink.FileDataSink(channel, outputBuffer);
        write(outputValueType, notation, output);
    }

    public void write(
            ValueType outputValueType,
            JsonNotation notation,
            DataSink output
    ) throws IOException {
        LutMetadata metadata = this.metadata.copyWithValueType(outputValueType);

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
            ValueBuffer buffer = map.getValueBuffer();
            buffer.convertTo(outputValueType).writeContents(output);
        }
    }

    public static Lut read(File file) throws IOException {
        return read(
                new JsonNotation(),
                GameStateEncoding::createSimple,
                file
        );
    }

    public static Lut read(
            JsonNotation jsonNotation,
            Function<GameSettings, GameStateEncoding> encodingGenerator,
            File file
    ) throws IOException {

        try (FileInputStream fis = new FileInputStream(file)) {
            return read(jsonNotation, encodingGenerator, fis.getChannel());
        }
    }

    public static Lut read(
            JsonNotation jsonNotation,
            Function<GameSettings, GameStateEncoding> encodingGenerator,
            FileChannel channel
    ) throws IOException {

        ByteBuffer workingBuffer = ByteBuffer.allocateDirect(1024 * 1024);
        workingBuffer.order(ByteOrder.BIG_ENDIAN);
        DataSource source = new DataSource.FileDataSource(channel, workingBuffer);
        return read(jsonNotation, encodingGenerator, source);
    }

    public static Lut read(
            JsonNotation jsonNotation,
            Function<GameSettings, GameStateEncoding> encodingGenerator,
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
        LutMetadata metadata = LutMetadata.decode(jsonNotation, metadataJson);
        GameStateEncoding encoding = encodingGenerator.apply(metadata.getGameSettings());

        int mapCount = source.readInt();
        int[] mapEntryCounts = new int[mapCount];
        UInt32ValueBuffer[] mapKeyBuffers = new UInt32ValueBuffer[mapCount];
        FloatValueBuffer[] mapValueBuffers = new FloatValueBuffer[mapCount];

        for (int index = 0; index < mapCount; ++index) {
            int entryCount = source.readInt();
            mapEntryCounts[index] = entryCount;
            mapKeyBuffers[index] = new UInt32ValueBuffer(entryCount);
            mapValueBuffers[index] = metadata.getValueType().createFloatBuffer(entryCount);
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
        return new Lut(encoding, metadata, maps);
    }
}
