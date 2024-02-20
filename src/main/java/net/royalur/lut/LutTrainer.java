package net.royalur.lut;

import net.royalur.lut.buffer.Float32ValueBuffer;
import net.royalur.lut.buffer.UInt32ValueBuffer;
import net.royalur.lut.store.LutMap;
import net.royalur.lut.store.OrderedUInt32BufferSet;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;

import java.io.*;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A lookup table based upon game states.
 */
public class LutTrainer<R extends Roll> {

    private static final DecimalFormat MS_DURATION = new DecimalFormat("#,###");

    private static final int OCCUPANTS_MASK = 0b11;
    private static final int LIGHT_ONLY_FLAG = 0b100;
    private static final int LIGHT_PATH_INDEX_SHIFT = 3;
    private static final int LIGHT_PATH_INDEX_MASK = 0b11111;
    private static final int DARK_PATH_INDEX_SHIFT = 8;
    private static final int DARK_PATH_INDEX_MASK = 0b11111;

    private static final int DEFAULT_UPPER_KEY_LIMIT = 64;

    private final GameSettings<R> settings;
    private final GameStateEncoding encoding;
    private final JsonNotation<?, ?, R> jsonNotation;
    private final int boardIndexCount;
    private final int[] tileFlags;
    private final int[] nextBoardIndices;

    public LutTrainer(
            GameSettings<R> settings,
            GameStateEncoding encoding,
            JsonNotation<?, ?, R> jsonNotation
    ) {
        this.settings = settings;
        this.encoding = encoding;
        this.jsonNotation = jsonNotation;

        BoardShape shape = settings.getBoardShape();
        PathPair paths = settings.getPaths();
        int width = shape.getWidth();
        int height = shape.getHeight();
        this.boardIndexCount = width * height;

        this.tileFlags = new int[boardIndexCount];

        List<Tile> lightPath = paths.getLight();
        List<Tile> darkPath = paths.getDark();
        for (int boardX = 0; boardX < width; ++boardX) {
            for (int boardY = 0; boardY < height; ++boardY) {
                Tile tile = Tile.fromIndices(boardX, boardY);
                int index = boardX + width * boardY;

                int occupants = 1;
                boolean tileLightOnly = false;
                int lightIndex = 0;
                int darkIndex = 0;
                if (lightPath.contains(tile)) {
                    occupants += 1;
                    tileLightOnly = true;
                    lightIndex = lightPath.indexOf(tile);
                }
                if (darkPath.contains(tile)) {
                    occupants += 1;
                    tileLightOnly = false;
                    darkIndex = darkPath.indexOf(tile);
                }

                int lightOnlyFlag = (tileLightOnly ? LIGHT_ONLY_FLAG : 0);
                int occupantsFlag = (occupants & OCCUPANTS_MASK);
                int lightPathFlag = (lightIndex & LIGHT_PATH_INDEX_MASK) << LIGHT_PATH_INDEX_SHIFT;
                int darkPathFlag = (darkIndex & DARK_PATH_INDEX_MASK) << DARK_PATH_INDEX_SHIFT;
                tileFlags[index] = lightOnlyFlag | occupantsFlag | lightPathFlag | darkPathFlag;
            }
        }

        this.nextBoardIndices = new int[boardIndexCount];
        for (int index = 0; index < boardIndexCount; ++index) {
            int nextX = index % width;
            int nextY = index / width;
            do {
                nextY += 1;
                if (nextY >= height) {
                    nextX += 1;
                    nextY = 0;
                }
            } while (nextX < width && (tileFlags[nextX + width * nextY] & OCCUPANTS_MASK) == 1);

            int nextIndex = nextX + width * nextY;
            if (nextX >= width) {
                nextIndex = boardIndexCount;
            }
            nextBoardIndices[index] = nextIndex;
        }
    }

    public int countStates() {
        return countStates(game -> true);
    }

    public int countStates(Function<FastSimpleGame, Boolean> gameFilter) {
        if (settings.getStartingPieceCount() > 7)
            throw new IllegalStateException("Starting piece count > 7 is not supported");
        if (settings.getBoardShape().getArea() >= 27)
            throw new IllegalArgumentException("Board area too big");

        AtomicLong stateCount = new AtomicLong();
        loopGameStates((game) -> {
            if (gameFilter.apply(game)) {
                stateCount.incrementAndGet();
            }
        });
        return Math.toIntExact(stateCount.get());
    }

    public void loopGameStates(Consumer<FastSimpleGame> gameConsumer) {
        FastSimpleGame game = new FastSimpleGame(settings);
        int pieceCount = settings.getStartingPieceCount();

        for (int lightPieces = 0; lightPieces <= pieceCount; ++lightPieces) {
            for (int darkPieces = 0; darkPieces <= pieceCount; ++darkPieces) {
                // Reset the game.
                game.board.clear();
                game.light.pieces = lightPieces;
                game.light.score = pieceCount - lightPieces;
                game.dark.pieces = darkPieces;
                game.dark.score = pieceCount - darkPieces;

                loopBoardStates(gameConsumer, game, 0);
            }
        }
    }

    private void loopBoardStates(
            Consumer<FastSimpleGame> gameConsumer,
            FastSimpleGame game,
            int boardIndex
    ) {
        int pieceCount = settings.getStartingPieceCount();
        int tileFlag = tileFlags[boardIndex];
        boolean lightOnly = (tileFlag & LIGHT_ONLY_FLAG) != 0;
        int occupants = tileFlag & OCCUPANTS_MASK;
        int lightIndex = (tileFlag >> LIGHT_PATH_INDEX_SHIFT) & LIGHT_PATH_INDEX_MASK;
        int darkIndex = (tileFlag >> DARK_PATH_INDEX_SHIFT) & DARK_PATH_INDEX_MASK;
        int nextBoardIndex = nextBoardIndices[boardIndex];

        int originalLightScore = game.light.score;
        int originalDarkScore = game.dark.score;

        for (int occupant = 0; occupant < occupants; ++occupant) {
            int newLightScore = originalLightScore;
            int newDarkScore = originalDarkScore;
            int newPiece = 0;
            if (occupant == 1) {
                if (lightOnly) {
                    newPiece = lightIndex + 1;
                    newLightScore -= 1;
                } else {
                    newPiece = -(darkIndex + 1);
                    newDarkScore -= 1;
                }
            } else if (occupant == 2) {
                newPiece = lightIndex + 1;
                newLightScore -= 1;
            }
            if (newLightScore < 0 || newDarkScore < 0)
                continue;

            game.board.set(boardIndex, newPiece);
            game.light.score = newLightScore;
            game.dark.score = newDarkScore;

            if (nextBoardIndex >= boardIndexCount) {
                boolean lightWon = (newLightScore >= pieceCount);
                boolean darkWon = (newDarkScore >= pieceCount);
                game.isFinished = (lightWon || darkWon);

                for (int turn = 0; turn <= 1; ++turn) {
                    // When the game is finished, the winner must have all pieces scored!
                    boolean isLightTurn = (turn == 1);
                    if (!isLightTurn && lightWon)
                        continue;
                    if (isLightTurn && darkWon)
                        continue;

                    game.isLightTurn = isLightTurn;
                    gameConsumer.accept(game);
                }
            } else {
                loopBoardStates(gameConsumer, game, nextBoardIndex);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GameSettings<Roll> settings = GameSettings.FINKEL;
        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();
        JsonNotation<?, ?, Roll> jsonNotation = JsonNotation.createSimple();
        LutTrainer<Roll> trainer = new LutTrainer<>(settings, encoding, jsonNotation);

        long populateStart = System.nanoTime();
        Lut<Roll> lut = trainer.populateNewLut();
        double populateDurationMs = (System.nanoTime() - populateStart) / 1e6;
        System.out.println("Populate took " + MS_DURATION.format(populateDurationMs) + " ms");

        long start = System.nanoTime();
        File outputFile = new File("./finkel_empty.rgu");
        lut.write(jsonNotation, outputFile);
        double durationMs = (System.nanoTime() - start) / 1e6;
        System.out.println("Write took " + MS_DURATION.format(durationMs) + " ms");
    }

    public UInt32ValueBuffer populateKeys(int upperKeyFilter) {
        OrderedUInt32BufferSet keys = new OrderedUInt32BufferSet();

        AtomicInteger entryCount = new AtomicInteger(0);
        loopGameStates(game -> {
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

        loopGameStates(game -> {
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
        loopGameStates(game -> {
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

    public Lut<R> populateNewLut(int upperKeyLimit) {
        LutMetadata<R> metadata = new LutMetadata<>(settings);
        LutMap[] maps = populateNewMaps(upperKeyLimit);
        return new Lut<>(encoding, metadata, maps);
    }

    public Lut<R> populateNewLut() {
        return populateNewLut(DEFAULT_UPPER_KEY_LIMIT);
    }

    private double iterateState(
            Lut<R> lut,
            FastSimpleGame game,
            float[] probabilities,
            FastSimpleGame rollGame,
            FastSimpleGame moveGame,
            FastSimpleMoveList moveList
    ) {
        double newValue = 0.0f;
        for (int roll = 0; roll <= 4; ++roll) {
            float prob = probabilities[roll];

            rollGame.copyFrom(game);
            rollGame.applyRoll(roll, moveList);

            double bestValue;
            if (rollGame.isWaitingForMove()) {
                bestValue = 0.0;

                for (int moveIndex = 0; moveIndex < moveList.moveCount; ++moveIndex) {
                    moveGame.copyFrom(rollGame);
                    moveGame.applyMove(moveList.moves[moveIndex]);

                    double moveValue = lut.getLightWinPercent(moveGame);
                    bestValue = Math.max(bestValue, moveValue);
                }
            } else {
                bestValue = lut.getLightWinPercent(rollGame);
            }
            newValue += prob * bestValue;
        }

        double lastValue = lut.updateLightWinPercent(game, newValue);
        return Math.abs(lastValue - newValue);
    }

    public void train(
            Lut<R> lut,
            File outputFile
    ) throws IOException {

        AtomicReference<Double> maxChange = new AtomicReference<>(0.0d);
        FastSimpleGame rollGame = new FastSimpleGame(settings);
        FastSimpleGame moveGame = new FastSimpleGame(settings);
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        float[] probabilities = settings.getDice().createDice().getRollProbabilities();

        int iteration = 0;
        for (int minScore = 6; minScore >= 0; --minScore) {
            for (int maxScore = 6; maxScore >= minScore; --maxScore) {
                do {
                    long start = System.nanoTime();
                    maxChange.set(0.0d);

                    int minScoreThreshold = minScore;
                    int maxScoreThreshold = maxScore;
                    loopGameStates((game) -> {
                        if (game.isFinished || !game.isLightTurn)
                            return;
                        if (Math.min(game.light.score, game.dark.score) != minScoreThreshold)
                            return;
                        if (Math.max(game.light.score, game.dark.score) != maxScoreThreshold)
                            return;

                        double difference = iterateState(
                                lut, game, probabilities,
                                rollGame, moveGame, moveList
                        );
                        if (difference > maxChange.get()) {
                            maxChange.set(difference);
                        }
                    });
                    double durationMs = (System.nanoTime() - start) / 1e6;
                    System.out.printf(
                            "%d. scores = [%d, %d], max diff = %.3f (%s ms)\n",
                            iteration + 1,
                            minScoreThreshold, maxScoreThreshold,
                            maxChange.get(),
                            MS_DURATION.format(durationMs)
                    );
                    iteration += 1;

                    if (iteration % 10 == 0) {
                        lut.write(jsonNotation, outputFile);
                    }
                } while (maxChange.get() > 0.01f);
            }
        }

        lut.write(jsonNotation, outputFile);

        System.out.println();
        System.out.println("Finished progressive value iteration!");
        System.out.println("Starting full value iteration for 10 steps...");
        for (int index = 0; index < 10; ++index) {
            long start = System.nanoTime();
            maxChange.set(0.0d);
            loopGameStates((game) -> {
                if (game.isFinished || !game.isLightTurn)
                    return;

                double difference = iterateState(
                        lut, game, probabilities,
                        rollGame, moveGame, moveList
                );
                if (difference > maxChange.get()) {
                    maxChange.set(difference);
                }
            });
            double durationMs = (System.nanoTime() - start) / 1e6;
            System.out.printf(
                    "%d. max diff = %.3f (%s ms)\n",
                    index + 1,
                    maxChange.get(),
                    MS_DURATION.format(durationMs)
            );
        }
        lut.write(jsonNotation, outputFile);
    }
}
