package net.royalur.rules.simple.fast;

import net.royalur.model.GameSettings;
import net.royalur.model.Tile;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Flags about the tiles on the board that can be accessed efficiently.
 */
public class FastSimpleFlags {

    public static final int OCCUPANTS_MASK = 0b11;
    public static final int LIGHT_ONLY_FLAG = 0b100;
    public static final int LIGHT_PATH_INDEX_SHIFT = 3;
    public static final int LIGHT_PATH_INDEX_MASK = 0b11111;
    public static final int DARK_PATH_INDEX_SHIFT = 8;
    public static final int DARK_PATH_INDEX_MASK = 0b11111;

    public final GameSettings<?> settings;
    private final int startingPieceCount;

    public final int boardIndexCount;
    public final int[] tileFlags;
    public final int[] nextBoardIndices;
    public final int warTileCount;
    public final int safeTileCountPerPlayer;

    public FastSimpleFlags(GameSettings<?> settings) {
        if (!isSymmetrical(settings))
            throw new IllegalArgumentException("Light & dark paths are not symmetrical");

        this.settings = settings;
        this.startingPieceCount = settings.getStartingPieceCount();

        this.boardIndexCount = calculateBoardIndexCount(settings);
        this.tileFlags = calculateTileFlags(settings, boardIndexCount);
        this.nextBoardIndices = calculateNextBoardIndices(settings, tileFlags);
        this.warTileCount = calculateWarTileCount(tileFlags);
        this.safeTileCountPerPlayer = calculateSafeTileCountPerPlayer(tileFlags);
    }

    public int countStates() {
        return countStates(game -> true);
    }

    public int countStates(Function<FastSimpleGame, Boolean> gameFilter) {
        AtomicLong stateCount = new AtomicLong();
        loopLightGameStates((game) -> {
            if (gameFilter.apply(game)) {
                stateCount.incrementAndGet();
            }
        });
        return Math.toIntExact(stateCount.get());
    }

    /**
     * Only loops through game states where it is light's turn.
     */
    public void loopLightGameStates(Consumer<FastSimpleGame> gameConsumer) {
        FastSimpleGame game = new FastSimpleGame(settings);
        game.isLightTurn = true;  // Always true.

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
        int startingPieceCount = this.startingPieceCount;
        int boardIndexCount = this.boardIndexCount;

        int tileFlag = tileFlags[boardIndex];
        boolean lightOnly = (tileFlag & LIGHT_ONLY_FLAG) != 0;
        int occupants = tileFlag & OCCUPANTS_MASK;
        int nextBoardIndex = nextBoardIndices[boardIndex];

        int originalLightScore = game.light.score;
        int originalDarkScore = game.dark.score;

        for (int occupant = 0; occupant < occupants; ++occupant) {
            int newLightScore = originalLightScore;
            int newDarkScore = originalDarkScore;
            int newPiece = 0;
            if (occupant == 1) {
                if (lightOnly) {
                    int lightIndex = (tileFlag >> LIGHT_PATH_INDEX_SHIFT) & LIGHT_PATH_INDEX_MASK;
                    newPiece = lightIndex + 1;
                    newLightScore -= 1;
                } else {
                    int darkIndex = (tileFlag >> DARK_PATH_INDEX_SHIFT) & DARK_PATH_INDEX_MASK;
                    newPiece = -(darkIndex + 1);
                    newDarkScore -= 1;
                }
            } else if (occupant == 2) {
                int lightIndex = (tileFlag >> LIGHT_PATH_INDEX_SHIFT) & LIGHT_PATH_INDEX_MASK;
                newPiece = lightIndex + 1;
                newLightScore -= 1;
            }
            if (newLightScore < 0 || newDarkScore < 0)
                continue;

            game.board.set(boardIndex, newPiece);
            game.light.score = newLightScore;
            game.dark.score = newDarkScore;

            if (nextBoardIndex >= boardIndexCount) {
                boolean darkWon = (newDarkScore >= startingPieceCount);
                if (darkWon)
                    continue;

                game.isFinished = (newLightScore >= startingPieceCount);
                gameConsumer.accept(game);
            } else {
                loopBoardStates(gameConsumer, game, nextBoardIndex);
            }
        }
    }

    private static boolean isSymmetrical(GameSettings<?> settings) {
        BoardShape shape = settings.getBoardShape();
        int width = shape.getWidth();

        PathPair paths = settings.getPaths();
        List<Tile> lightPath = paths.getLight();
        List<Tile> darkPath = paths.getDark();
        if (lightPath.size() != darkPath.size())
            return false;

        for (int index = 0; index < lightPath.size(); ++index) {
            Tile lightTile = lightPath.get(index);
            Tile darkTile = darkPath.get(index);
            if (lightTile.getY() != darkTile.getY())
                return false;
            if (lightTile.getX() != width - darkTile.getX() + 1)
                return false;
        }
        return true;
    }

    private static int calculateBoardIndexCount(GameSettings<?> settings) {
        BoardShape shape = settings.getBoardShape();
        int width = shape.getWidth();
        int height = shape.getHeight();
        return width * height;
    }

    private static int[] calculateTileFlags(GameSettings<?> settings, int boardIndexCount) {
        BoardShape shape = settings.getBoardShape();
        int width = shape.getWidth();
        int height = shape.getHeight();

        PathPair paths = settings.getPaths();
        List<Tile> lightPath = paths.getLight();
        List<Tile> darkPath = paths.getDark();

        int[] tileFlags = new int[boardIndexCount];
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
        return tileFlags;
    }

    private static int[] calculateNextBoardIndices(GameSettings<?> settings, int[] tileFlags) {
        BoardShape shape = settings.getBoardShape();
        int width = shape.getWidth();
        int height = shape.getHeight();
        int boardIndexCount = tileFlags.length;

        int[] nextBoardIndices = new int[boardIndexCount];
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
        return nextBoardIndices;
    }

    private static int calculateWarTileCount(int[] tileFlags) {
        int warTileCount = 0;
        for (int tileFlag : tileFlags) {
            int occupants = tileFlag & OCCUPANTS_MASK;
            if (occupants == 3) {
                warTileCount += 1;
            }
        }
        return warTileCount;
    }

    private static int calculateSafeTileCountPerPlayer(int[] tileFlags) {
        int lightSafeTileCount = 0;
        int darkSafeTileCount = 0;
        for (int tileFlag : tileFlags) {
            int occupants = tileFlag & OCCUPANTS_MASK;
            if (occupants != 2)
                continue;

            boolean lightOnly = (tileFlag & LIGHT_ONLY_FLAG) != 0;
            if (lightOnly) {
                lightSafeTileCount += 1;
            } else {
                darkSafeTileCount += 1;
            }
        }
        if (lightSafeTileCount != darkSafeTileCount) {
            throw new IllegalArgumentException(
                    "Asymmetrical board! Light & dark safe tile counts are not equal"
            );
        }

        return lightSafeTileCount;
    }
}
