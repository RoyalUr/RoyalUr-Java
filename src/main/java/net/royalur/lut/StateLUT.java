package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.model.Tile;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;

import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * A lookup table based upon game states.
 */
public class StateLUT {

    private static final int OCCUPANTS_MASK = 0b11;
    private static final int LIGHT_ONLY_FLAG = 0b100;
    private static final int LIGHT_PATH_INDEX_SHIFT = 3;
    private static final int LIGHT_PATH_INDEX_MASK = 0b11111;
    private static final int DARK_PATH_INDEX_SHIFT = 8;
    private static final int DARK_PATH_INDEX_MASK = 0b11111;

    private final GameSettings<?> settings;
    private final BoardShape shape;
    private final PathPair paths;
    private final int width;
    private final int height;
    private final int area;
    private final int[] tileFlags;
    private final int[] nextBoardIndices;

    public StateLUT(GameSettings<?> settings) {
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

    public void compress() {

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

    public static void main(String[] args) {
        DecimalFormat msFormatter = new DecimalFormat("#,###");
        GameSettings<?> settings = GameSettings.FINKEL;
        StateLUT finkel = new StateLUT(settings);
        FinkelGameEncoding encoding = new FinkelGameEncoding();
        BigMap states = new BigMap(BigMap.INT, BigMap.INT);

        // Starting state.
        long start1 = System.nanoTime();
        finkel.loopGameStates((game) -> {
            int key = encoding.encode(game);
            float score = (game.isFinished ? 100 * (game.isLightTurn ? 1 : -1) : 0);
            states.put(key, Float.floatToRawIntBits(score));
        });
        double duration1Ms = (System.nanoTime() - start1) / 1e6;
        System.out.println("Population took " + msFormatter.format(duration1Ms) + " ms");

        long start2 = System.nanoTime();
        double overlapsPerChunkBeforeSort = states.getOverlapsPerChunk();
        states.sort();
        double overlapsPerChunkAfterSort = states.getOverlapsPerChunk();
        double duration2Ms = (System.nanoTime() - start2) / 1e6;
        System.out.println("Sort took " + msFormatter.format(duration2Ms) + " ms");

        // Iterate!
        AtomicReference<Float> maxChange = new AtomicReference<>(0f);
        FastSimpleGame rollGame = new FastSimpleGame(settings);
        FastSimpleGame moveGame = new FastSimpleGame(settings);
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        float[] probabilities = settings.getDice().createDice().getRollProbabilities();

        int iteration = 0;
        for (int minScore = 6; minScore >= 0; --minScore) {
            for (int maxScore = 6; maxScore >= minScore; --maxScore) {
                do {
                    long start3 = System.nanoTime();
                    maxChange.set(0.0f);

                    int minScoreThreshold = minScore;
                    int maxScoreThreshold = maxScore;
                    finkel.loopGameStates((game) -> {
                        if (game.isFinished)
                            return;
                        if (Math.min(game.light.score, game.dark.score) != minScoreThreshold)
                            return;
                        if (Math.max(game.light.score, game.dark.score) != maxScoreThreshold)
                            return;

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
                                    float moveValue = Float.intBitsToFloat(states.getInt(moveKey));

                                    if (rollGame.isLightTurn) {
                                        bestValue = Math.max(bestValue, moveValue);
                                    } else {
                                        bestValue = Math.min(bestValue, moveValue);
                                    }
                                }
                            } else {
                                int rollKey = encoding.encode(rollGame);
                                bestValue = Float.intBitsToFloat(states.getInt(rollKey));
                            }
                            newValue += prob * bestValue;
                        }

                        int lastValueBits = states.set(key, Float.floatToRawIntBits(newValue));
                        float lastValue = Float.intBitsToFloat(lastValueBits);

                        float difference = Math.abs(lastValue - newValue);
                        float currentMaxDifference = maxChange.get();
                        if (difference > currentMaxDifference) {
                            maxChange.set(difference);
                        }
                    });
                    double duration3Ms = (System.nanoTime() - start3) / 1e6;
                    System.out.printf(
                            "%d. scores = [%d, %d], max diff = %.3f (%s ms)\n",
                            iteration + 1,
                            minScoreThreshold, maxScoreThreshold,
                            maxChange.get(),
                            msFormatter.format(duration3Ms)
                    );
                    iteration += 1;
                } while (maxChange.get() > 0.01f);
            }
        }
    }
}
