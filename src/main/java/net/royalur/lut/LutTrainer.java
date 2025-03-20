package net.royalur.lut;

import net.royalur.lut.buffer.*;
import net.royalur.lut.store.LutMap;
import net.royalur.lut.store.OrderedUInt32BufferSet;
import net.royalur.model.*;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;

import java.io.*;
import java.text.DecimalFormat;
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
    private final ValueType trainingValueType;
    private final JsonNotation jsonNotation;
    private final FastSimpleFlags flags;

    public LutTrainer(
            GameSettings settings,
            GameStateEncoding encoding,
            ValueType trainingValueType,
            JsonNotation jsonNotation
    ) {
        this.settings = settings;
        this.encoding = encoding;
        this.trainingValueType = trainingValueType;
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

        FloatValueBuffer values = trainingValueType.createFloatBuffer(entryCount);
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
        LutMetadata metadata = new LutMetadata(settings, trainingValueType);
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
            File checkpointFile,
            ValueType outputValueType
    ) throws IOException {

        train(lut, checkpointFile, outputValueType, 0.001d);
    }

    public Lut train(
            Lut lut,
            File checkpointFile,
            ValueType outputValueType,
            double tolerance
    ) throws IOException {

        long trainStart = System.nanoTime();
        lut = lut.convertValueTypes(trainingValueType);

        System.out.printf(
                "Training in %s to a stopping precision of %.12f\n",
                trainingValueType.getTextID(), tolerance
        );
        System.out.printf(
                "Checkpoints will be saved to %s\n",
                checkpointFile.getAbsolutePath()
        );
        System.out.printf(
                "The final output will be saved in %s\n",
                outputValueType.getTextID()
        );
        System.out.println();

        int iteration = 0;
        int pieceCount = settings.getStartingPieceCount();
        double overallMaxChange = 0.0;

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

                long overallStart = System.nanoTime();

                double maxChange;
                double[] lastMaxChanges = new double[10];
                Arrays.fill(lastMaxChanges, Double.POSITIVE_INFINITY);
                do {
                    long start = System.nanoTime();
                    maxChange = performTrainingIteration(lut, stateCount, stateFilter);
                    double durationMs = (System.nanoTime() - start) / 1e6;
                    System.out.printf(
                            "%d. scores = [%d, %d], max diff = %s (%s ms)\n",
                            iteration + 1,
                            minScore, maxScore,
                            formatMaxDiff(maxChange),
                            LutCLI.MS_DURATION.format(durationMs)
                    );
                    iteration += 1;

                    if (Double.isNaN(maxChange) || Double.isInfinite(maxChange)) {
                        throw new IllegalStateException(
                                "max diff is NaN or Infinite, which is a big problem: " + maxChange
                        );
                    }

                    // Verify that the maxChange has improved recently.
                    boolean improved = false;
                    for (int index = 0; index < lastMaxChanges.length; ++index) {
                        double previousMaxChange = lastMaxChanges[index];
                        if (index > 0) {
                            lastMaxChanges[index - 1] = previousMaxChange;
                        }
                        improved |= maxChange < previousMaxChange;
                    }
                    lastMaxChanges[lastMaxChanges.length - 1] = maxChange;
                    if (!improved) {
                        System.err.println(
                                "Max diff has not improved in " + lastMaxChanges.length + " iterations, "
                                        + "skipping to next set of scores"
                        );
                        break;
                    }

                    // Save checkpoints periodically.
                    if (iteration % 10 == 0) {
                        lut.write(trainingValueType, jsonNotation, checkpointFile);
                    }
                } while ((tolerance > 0 && maxChange > tolerance) || (tolerance <= 0 && maxChange > 0));

                double overallDurationMs = (System.nanoTime() - overallStart) / 1e6;
                System.out.printf(
                        "Finished scores = [%d, %d], max diff = %s (%s ms)\n\n",
                        minScore, maxScore,
                        formatMaxDiff(maxChange),
                        LutCLI.MS_DURATION.format(overallDurationMs)
                );
                if (maxChange > overallMaxChange) {
                    overallMaxChange = maxChange;
                }
            }
        }
        lut.getMetadata().addMetadata("target-precision", tolerance);
        if (!Double.isInfinite(overallMaxChange)) {
            lut.getMetadata().addMetadata("training-precision", overallMaxChange);
        }

        double totalDurationMs = (System.nanoTime() - trainStart) / 1e6d;
        System.out.println();
        System.out.printf(
                "Finished value iteration in %s ms!\n",
                LutCLI.MS_DURATION.format(totalDurationMs)
        );

        long writeStart = System.nanoTime();
        lut.write(outputValueType, jsonNotation, checkpointFile);
        double writeDurationMs = (System.nanoTime() - writeStart) / 1e6;
        System.out.println();
        System.out.printf(
                "Saving model took %s ms!\n",
                LutCLI.MS_DURATION.format(writeDurationMs)
        );
        return lut;
    }

    private static final DecimalFormat MAX_DIFF_FORMAT = new DecimalFormat("0.########E0");
    private static final String MAX_DIFF_FORMATTED_ZERO = MAX_DIFF_FORMAT.format(0d);
    private static final DecimalFormat VERY_SMALL_MAX_DIFF_FORMAT = new DecimalFormat("0.###E0");

    private static String formatMaxDiff(double maxDiff) {
        String value = MAX_DIFF_FORMAT.format(maxDiff);
        if (!MAX_DIFF_FORMATTED_ZERO.equals(value))
            return value;

        return VERY_SMALL_MAX_DIFF_FORMAT.format(maxDiff);
    }
}
