package net.royalur;

import net.royalur.cli.CLI;
import net.royalur.lut.GameStateEncoding;
import net.royalur.lut.Lut;
import net.royalur.lut.buffer.UInt8ValueBuffer;
import net.royalur.lut.store.DataSink;
import net.royalur.lut.store.LutMap;
import net.royalur.model.GameSettings;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.rules.simple.fast.FastSimpleGame;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Work to visualise lut.
 */
public class LutVisualisation {

    private final Lut lut;
    private final GameSettings settings;
    private final FastSimpleFlags flags;
    private final UInt8ValueBuffer[] depthBuffers;

    public LutVisualisation(Lut lut) {
        this.lut = lut;
        this.settings = lut.getGameSettings();
        this.flags = new FastSimpleFlags(settings);

        LutMap[] maps = lut.getMaps();
        this.depthBuffers = new UInt8ValueBuffer[maps.length];
        for (int index = 0; index < maps.length; ++index) {
            LutMap map = maps[index];
            UInt8ValueBuffer buffer = new UInt8ValueBuffer(map.getEntryCount());
            depthBuffers[index] = buffer;

            for (int bufferIndex = 0; bufferIndex < buffer.getCapacity(); ++bufferIndex) {
                buffer.set(bufferIndex, 255);
            }
        }
    }

    private int getDepth(long key) {
        int upperKey = GameStateEncoding.calcUpperKey(key);
        int lowerKey = GameStateEncoding.calcLowerKey(key);
        int index = lut.getMap(upperKey).indexOfKey(lowerKey);
        return Byte.toUnsignedInt(depthBuffers[upperKey].getByte(index));
    }

    private int setDepth(long key, int depth) {
        int upperKey = GameStateEncoding.calcUpperKey(key);
        int lowerKey = GameStateEncoding.calcLowerKey(key);
        int index = lut.getMap(upperKey).indexOfKey(lowerKey);
        return depthBuffers[upperKey].set(index, depth);
    }

    private int iterate() {
        GameStateEncoding encoding = lut.getGameStateEncoding();
        FastSimpleGame tempGame = new FastSimpleGame(lut.getGameSettings());

        AtomicInteger changeCount = new AtomicInteger(0);
        flags.loopLightGameStatesAndNeighbours((game, neighbours) -> {
            long gameKey = encoding.encodeGameState(game);
            int gameDepth = getDepth(gameKey);
            int newDepth = gameDepth + 1;

            for (FastSimpleGame neighbour : neighbours) {
                long neighbourKey = encoding.encodeSymmetricalGameState(neighbour, tempGame);
                int neighbourDepth = getDepth(neighbourKey);
                if (newDepth < neighbourDepth) {
                    setDepth(neighbourKey, newDepth);
                    changeCount.incrementAndGet();
                }
            }
        });
        return changeCount.get();
    }

    public void calculateDepths(File outputFile) throws IOException {
        int index = 0;
        while (true) {
            long start = System.nanoTime();
            if (iterate() == 0)
                break;

            double durationMS = (System.nanoTime() - start) / 1e6d;
            System.out.println(".. " + (++index) + " (" + CLI.MS_DURATION.format(durationMS) + ")");
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            ByteBuffer workingBuffer = ByteBuffer.allocateDirect(1024 * 1024);
            DataSink sink = new DataSink.FileDataSink(fos.getChannel(), workingBuffer);
            for (UInt8ValueBuffer buffer : depthBuffers) {
                buffer.writeContents(sink);
            }
        }
    }
}
