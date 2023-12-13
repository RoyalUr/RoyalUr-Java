package net.royalur.lut;

import net.royalur.Game;
import net.royalur.lut.buffer.ValueType;
import net.royalur.lut.store.BigEntryStore;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.rules.simple.SimpleRuleSetProvider;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A lookup table based upon game states.
 */
public class StateLUT {

    private static final @Nonnull DecimalFormat MS_DURATION = new DecimalFormat("#,###");

    private static final int OCCUPANTS_MASK = 0b11;
    private static final int LIGHT_ONLY_FLAG = 0b100;
    private static final int LIGHT_PATH_INDEX_SHIFT = 3;
    private static final int LIGHT_PATH_INDEX_MASK = 0b11111;
    private static final int DARK_PATH_INDEX_SHIFT = 8;
    private static final int DARK_PATH_INDEX_MASK = 0b11111;

    private final @Nonnull GameSettings<?> settings;
    private final @Nonnull BoardShape shape;
    private final @Nonnull PathPair paths;
    private final int width;
    private final int height;
    private final int area;
    private final int[] tileFlags;
    private final int[] nextBoardIndices;

    public StateLUT(@Nonnull GameSettings<?> settings) {
        this.settings = settings;
        this.shape = settings.getBoardShape();
        this.paths = settings.getPaths();
        this.width = shape.getWidth();
        this.height = shape.getHeight();
        this.area = width * height;

        this.tileFlags = new int[area];

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

        this.nextBoardIndices = new int[area];
        for (int index = 0; index < area; ++index) {
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
                nextIndex = area;
            }
            nextBoardIndices[index] = nextIndex;
        }
    }

    public int countStates() {
        if (settings.getStartingPieceCount() > 7)
            throw new IllegalStateException("Starting piece count > 7 is not supported");
        if (shape.getTiles().size() >= 27)
            throw new IllegalArgumentException("Board area too big");

        AtomicLong stateCount = new AtomicLong();
        loopGameStates((game) -> stateCount.incrementAndGet());
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

            if (nextBoardIndex >= area) {
                game.isFinished = (newLightScore == pieceCount || newDarkScore == pieceCount);

                for (int isLightTurn = 0; isLightTurn <= 1; ++isLightTurn) {
                    // When the game is finished, the winner must have all pieces scored!
                    if (isLightTurn == 0 && newLightScore == pieceCount)
                        continue;
                    if (isLightTurn == 1 && newDarkScore == pieceCount)
                        continue;

                    game.isLightTurn = (isLightTurn == 1);
                    gameConsumer.accept(game);
                }
            } else {
                loopBoardStates(gameConsumer, game, nextBoardIndex);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        GameSettings<?> settings = GameSettings.FINKEL;
        StateLUT lut = new StateLUT(settings);
        FinkelGameEncoding encoding = new FinkelGameEncoding();

        File outputFile = new File("./finkel.rgu");
        BigEntryStore states = lut.readOrPopulateStateStore(encoding, outputFile);

        FastSimpleGame game = new FastSimpleGame(settings);
        game.copyFrom(new Game<>(new SimpleRuleSetProvider().create(settings, new GameMetadata())));
        System.out.println(Float.intBitsToFloat(states.getInt(encoding.encode(game))));
//        lut.iterate(settings, encoding, states, outputFile);
    }

    private void writeStateStore(
            @Nonnull BigEntryStore states,
            @Nonnull File outputFile
    ) throws IOException {

        long start = System.nanoTime();
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            states.write(fos.getChannel());
        }
        double durationMs = (System.nanoTime() - start) / 1e6;
        System.out.println("Write took " + MS_DURATION.format(durationMs) + " ms");
    }

    public @Nonnull BigEntryStore readStateStore(@Nonnull File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            BigEntryStore states = BigEntryStore.read(fis.getChannel());

            if (states.getKeyType() != ValueType.INT)
                throw new IOException("Expected int keys");
            if (states.getValueType() != ValueType.INT)
                throw new IOException("Expected int values");

            return states;
        }
    }

    public @Nonnull BigEntryStore readOrPopulateStateStore(
            @Nonnull FinkelGameEncoding encoding,
            @Nonnull File file
    ) throws IOException {

        if (file.exists())
            return readStateStore(file);

        System.out.println("Populating map...");

        // Populate the map.
        BigEntryStore states = new BigEntryStore(ValueType.INT, ValueType.INT);

        long start1 = System.nanoTime();
        loopGameStates((game) -> {
            int key = encoding.encode(game);
            float score = (game.isFinished ? 100 * (game.isLightTurn ? 1 : -1) : 0);
            states.addEntry(key, Float.floatToRawIntBits(score));
        });
        double duration1Ms = (System.nanoTime() - start1) / 1e6;
        System.out.println("Population took " + MS_DURATION.format(duration1Ms) + " ms");

        long start2 = System.nanoTime();
        double overlapsPerChunkBeforeSort = states.getOverlapsPerChunk();
        states.sort();
        double overlapsPerChunkAfterSort = states.getOverlapsPerChunk();
        double duration2Ms = (System.nanoTime() - start2) / 1e6;
        System.out.println("Sort took " + MS_DURATION.format(duration2Ms) + " ms");

        writeStateStore(states, file);
        return states;
    }

    private float iterateState(
            @Nonnull FastSimpleGame game,
            @Nonnull FinkelGameEncoding encoding,
            @Nonnull BigEntryStore states,
            float[] probabilities,
            @Nonnull FastSimpleGame rollGame,
            @Nonnull FastSimpleGame moveGame,
            @Nonnull FastSimpleMoveList moveList
    ) {
        int key = encoding.encode(game);

        float newValue = 0.0f;
        for (int roll = 0; roll <= 4; ++roll) {
            float prob = probabilities[roll];

            rollGame.copyFrom(game);
            rollGame.applyRoll(roll, moveList);

            float bestValue;
            if (rollGame.isWaitingForMove()) {
                bestValue = (rollGame.isLightTurn ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY);

                for (int moveIndex = 0; moveIndex < moveList.moveCount; ++moveIndex) {
                    moveGame.copyFrom(rollGame);
                    moveGame.applyMove(moveList.moves[moveIndex]);
                    int moveKey = encoding.encode(moveGame);
                    Integer moveValueBits = states.getInt(moveKey);
                    if (moveValueBits == null)
                        throw new NullPointerException(Integer.toBinaryString(moveKey));

                    float moveValue = Float.intBitsToFloat(moveValueBits);

                    if (rollGame.isLightTurn) {
                        bestValue = Math.max(bestValue, moveValue);
                    } else {
                        bestValue = Math.min(bestValue, moveValue);
                    }
                }
            } else {
                int rollKey = encoding.encode(rollGame);
                Integer rollValueBits = states.getInt(rollKey);
                if (rollValueBits == null)
                    throw new NullPointerException(Integer.toBinaryString(rollKey));

                bestValue = Float.intBitsToFloat(rollValueBits);
            }
            newValue += prob * bestValue;
        }

        int lastValueBits = states.updateEntry(key, Float.floatToRawIntBits(newValue));
        float lastValue = Float.intBitsToFloat(lastValueBits);
        return Math.abs(lastValue - newValue);
    }

    public void iterate(
            @Nonnull GameSettings<?> settings,
            @Nonnull FinkelGameEncoding encoding,
            @Nonnull BigEntryStore states,
            @Nonnull File outputFile
    ) throws IOException {

        AtomicReference<Float> maxChange = new AtomicReference<>(0f);
        FastSimpleGame rollGame = new FastSimpleGame(settings);
        FastSimpleGame moveGame = new FastSimpleGame(settings);
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        float[] probabilities = settings.getDice().createDice().getRollProbabilities();

        int iteration = 0;
        for (int minScore = 6; minScore >= 0; --minScore) {
            for (int maxScore = 6; maxScore >= minScore; --maxScore) {
                do {
                    long start = System.nanoTime();
                    maxChange.set(0.0f);

                    int minScoreThreshold = minScore;
                    int maxScoreThreshold = maxScore;
                    loopGameStates((game) -> {
                        if (game.isFinished)
                            return;
                        if (Math.min(game.light.score, game.dark.score) != minScoreThreshold)
                            return;
                        if (Math.max(game.light.score, game.dark.score) != maxScoreThreshold)
                            return;

                        float difference = iterateState(
                                game, encoding, states, probabilities,
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
                        writeStateStore(states, outputFile);
                    }
                } while (maxChange.get() > 0.01f);
            }
        }

        writeStateStore(states, outputFile);

        System.out.println();
        System.out.println("Finished progressive value iteration!");
        System.out.println("Starting full value iteration for 10 steps...");
        for (int index = 0; index < 10; ++index) {
            long start = System.nanoTime();
            maxChange.set(0.0f);
            loopGameStates((game) -> {
                if (game.isFinished)
                    return;

                float difference = iterateState(
                        game, encoding, states, probabilities,
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
        writeStateStore(states, outputFile);
    }
}
