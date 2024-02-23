package net.royalur.lut;

import net.royalur.lut.buffer.Float32ValueBuffer;
import net.royalur.lut.buffer.UInt32ValueBuffer;
import net.royalur.lut.store.LutMap;
import net.royalur.lut.store.OrderedUInt32BufferSet;
import net.royalur.model.*;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * A lookup table based upon game states.
 */
public class LutTrainer {

    private static final int DEFAULT_UPPER_KEY_LIMIT = 64;

    private final GameSettings settings;
    private final GameStateEncoding encoding;
    private final JsonNotation jsonNotation;
    private final FastSimpleFlags flags;

    public LutTrainer(
            GameSettings settings,
            GameStateEncoding encoding,
            JsonNotation jsonNotation
    ) {
        this.settings = settings;
        this.encoding = encoding;
        this.jsonNotation = jsonNotation;
        this.flags = new FastSimpleFlags(settings);
    }

    public UInt32ValueBuffer populateKeys(int upperKeyFilter) {
        OrderedUInt32BufferSet keys = new OrderedUInt32BufferSet();

        AtomicInteger entryCount = new AtomicInteger(0);
        flags.loopLightGameStates(game -> {
            if (!game.isLightTurn)
                return;

            long key = encoding.encodeGameState(game);
            int upperKey = GameStateEncoding.calcUpperKey(key);
            int lowerKey = GameStateEncoding.calcLowerKey(key);
            if (upperKey != upperKeyFilter)
                return;

            keys.add(lowerKey);
            entryCount.incrementAndGet();
        });
        return keys.toSingleCompressedBuffer().getBuffer();
    }

    public LutMap populateNewMap(int upperKeyFilter) {
        UInt32ValueBuffer keys = populateKeys(upperKeyFilter);
        int entryCount = keys.getCapacity();

        Float32ValueBuffer values = new Float32ValueBuffer(entryCount);
        LutMap map = new LutMap(entryCount, keys, values);

        flags.loopLightGameStates(game -> {
            if (!game.isLightTurn)
                return;

            long key = encoding.encodeGameState(game);
            int upperKey = GameStateEncoding.calcUpperKey(key);
            int lowerKey = GameStateEncoding.calcLowerKey(key);
            if (upperKey != upperKeyFilter)
                return;

            float value = (game.isFinished ? 100.0f : 50.0f);
            map.set(lowerKey, value);
        });
        return map;
    }

    private Set<Integer> findAllUpperKeys() {
        Set<Integer> upperKeys = new HashSet<>();
        flags.loopLightGameStates(game -> {
            if (!game.isLightTurn)
                return;

            long key = encoding.encodeGameState(game);
            upperKeys.add(GameStateEncoding.calcUpperKey(key));
        });
        return upperKeys;
    }

    private static int calculateMaxUnsigned(Iterable<Integer> upperKeys) {
        int maxUpperKey = 0;
        for (int upperKey : upperKeys) {
            if (Long.compareUnsigned(upperKey, maxUpperKey) > 0) {
                maxUpperKey = upperKey;
            }
        }
        return maxUpperKey;
    }

    public LutMap[] populateNewMaps(int upperKeyLimit) {
        Set<Integer> upperKeys = findAllUpperKeys();
        int maxUpperKey = calculateMaxUnsigned(upperKeys);
        if (Long.compareUnsigned(maxUpperKey, upperKeyLimit) >= 0) {
            throw new IllegalArgumentException(
                    "upperKeyLimit exceeds error limit: " + maxUpperKey + " >= " + upperKeyLimit
            );
        }
        if (maxUpperKey < 0) {
            throw new UnsupportedOperationException(
                    "maxUpperKey is too large. It cannot be negative when treated as signed"
            );
        }

        LutMap[] maps = new LutMap[maxUpperKey + 1];
        for (int upperKey = 0; upperKey <= maxUpperKey; ++upperKey) {
            maps[upperKey] = populateNewMap(upperKey);
        }
        return maps;
    }

    public Lut populateNewLut(int upperKeyLimit) {
        LutMetadata metadata = new LutMetadata(settings);
        LutMap[] maps = populateNewMaps(upperKeyLimit);
        return new Lut(encoding, metadata, maps);
    }

    public Lut populateNewLut() {
        return populateNewLut(DEFAULT_UPPER_KEY_LIMIT);
    }

    private double iterateState(
            Lut lut,
            FastSimpleGame game,
            float[] probabilities,
            FastSimpleGame rollGame,
            FastSimpleGame moveGame,
            FastSimpleGame tempGame,
            FastSimpleMoveList moveList
    ) {
        double newValue = 0.0f;
        for (int roll = 0; roll < probabilities.length; ++roll) {
            float prob = probabilities[roll];
            if (prob <= 0.0f)
                continue;

            rollGame.copyFrom(game);
            rollGame.applyRoll(roll, moveList);

            double bestValue;
            if (rollGame.isWaitingForMove()) {
                bestValue = 0.0;

                for (int moveIndex = 0; moveIndex < moveList.moveCount; ++moveIndex) {
                    moveGame.copyFrom(rollGame);
                    moveGame.applyMove(moveList.moves[moveIndex]);

                    double moveValue = lut.getLightWinPercent(moveGame, tempGame);
                    bestValue = Math.max(bestValue, moveValue);
                }
            } else {
                bestValue = lut.getLightWinPercent(rollGame, tempGame);
            }
            newValue += prob * bestValue;
        }

        double lastValue = lut.updateLightWinPercent(game, newValue);
        return Math.abs(lastValue - newValue);
    }

    private double performTrainingIterationSection(
            Lut lut,
            Function<FastSimpleGame, Boolean> stateFilter,
            int fromIndex,
            int toIndex
    ) {
        AtomicInteger indexCounter = new AtomicInteger(0);
        AtomicReference<Double> maxChange = new AtomicReference<>(0.0d);

        FastSimpleGame rollGame = new FastSimpleGame(settings);
        FastSimpleGame moveGame = new FastSimpleGame(settings);
        FastSimpleGame tempGame = new FastSimpleGame(settings);
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        float[] probabilities = settings.getDice().createDice().getRollProbabilities();

        flags.loopLightGameStates(game -> {
            if (game.isFinished || !stateFilter.apply(game))
                return;

            int index = indexCounter.getAndIncrement();
            if (index < fromIndex || index >= toIndex)
                return;

            double change = iterateState(
                    lut, game, probabilities,
                    rollGame, moveGame, tempGame,
                    moveList
            );
            if (change > maxChange.get()) {
                maxChange.set(change);
            }
        });
        return maxChange.get();
    }

    private double performTrainingIteration(
            Lut lut,
            int stateCount,
            Function<FastSimpleGame, Boolean> stateFilter
    ) {
        // Split up the keys between threads for processing.
        int threadCount = Runtime.getRuntime().availableProcessors();
        int statesPerThread = (stateCount + threadCount - 1) / threadCount;
        AtomicReference<Double> maxChange = new AtomicReference<>(0.0d);

        List<Thread> threads = new ArrayList<>();
        AtomicReference<Exception> error = new AtomicReference<>();

        for (int threadNo = 0; threadNo < threadCount; ++threadNo) {

            int fromIndex = statesPerThread * threadNo;
            int toIndex = statesPerThread * (threadNo + 1);

            Thread thread = new Thread(() -> {
                try {
                    double change = performTrainingIterationSection(
                            lut, stateFilter,
                            fromIndex, toIndex
                    );
                    synchronized (maxChange) {
                        if (change > maxChange.get()) {
                            maxChange.set(change);
                        }
                    }
                } catch (Exception e) {
                    error.set(e);
                }
            }, "train-" + threadNo);
            threads.add(thread);
            thread.start();
        }

        // Wait for all processing to complete.
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (error.get() != null)
            throw new RuntimeException(error.get());

        return maxChange.get();
    }

    public void train(
            Lut lut,
            File checkpointFile
    ) throws IOException {

        train(lut, checkpointFile, 0.01d);
    }

    public Lut train(
            Lut lut,
            File checkpointFile,
            double tolerance
    ) throws IOException {

        lut = lut.copyValuesToFloat32();

        int iteration = 0;
        int pieceCount = settings.getStartingPieceCount();
        for (int minScore = pieceCount - 1; minScore >= 0; --minScore) {
            for (int maxScore = pieceCount - 1; maxScore >= minScore; --maxScore) {

                int minScoreFinal = minScore;
                int maxScoreFinal = maxScore;
                Function<FastSimpleGame, Boolean> stateFilter = game -> {
                    int min = Math.min(game.light.score, game.dark.score);
                    int max = Math.max(game.light.score, game.dark.score);
                    return min == minScoreFinal && max == maxScoreFinal;
                };
                int stateCount = flags.countStates(stateFilter);

                double maxChange;
                do {
                    long start = System.nanoTime();
                    maxChange = performTrainingIteration(lut, stateCount, stateFilter);
                    double durationMs = (System.nanoTime() - start) / 1e6;
                    System.out.printf(
                            "%d. scores = [%d, %d], max diff = %.3f (%s ms)\n",
                            iteration + 1,
                            minScore, maxScore,
                            maxChange,
                            LutCLI.MS_DURATION.format(durationMs)
                    );
                    iteration += 1;

                    if (iteration % 10 == 0) {
                        lut.write(jsonNotation, checkpointFile);
                    }
                } while (maxChange > tolerance);
            }
        }

        lut.write(jsonNotation, checkpointFile);

        System.out.println();
        System.out.println("Finished progressive value iteration!");
        System.out.println("Starting full value iteration for 10 steps...");
        int stateCount = flags.countStates();
        for (int index = 0; index < 10; ++index) {
            long start = System.nanoTime();

            double maxChange = performTrainingIteration(
                    lut, stateCount, game -> true
            );
            double durationMs = (System.nanoTime() - start) / 1e6;
            System.out.printf(
                    "%d. max diff = %.3f (%s ms)\n",
                    index + 1,
                    maxChange,
                    LutCLI.MS_DURATION.format(durationMs)
            );
        }
        lut.write(jsonNotation, checkpointFile);
        return lut;
    }
}
