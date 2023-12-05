package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.model.Tile;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.rules.simple.fast.FastSimpleGame;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
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
            int nextBoardIndex = index;
            do {
                nextBoardIndex += 1;
            } while (nextBoardIndex < area && (tileFlags[nextBoardIndex] & OCCUPANTS_MASK) == 1);
            nextBoardIndices[index] = nextBoardIndex;
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

        for (int isLightTurn = 0; isLightTurn <= 1; ++isLightTurn) {
            for (int lightPieces = 0; lightPieces <= pieceCount; ++lightPieces) {
                for (int darkPieces = 0; darkPieces <= pieceCount; ++darkPieces) {
                    if (lightPieces == 0 && darkPieces == 0)
                        continue;

                    // Reset the game.
                    game.board.clear();
                    game.isLightTurn = (isLightTurn == 1);
                    game.light.pieces = lightPieces;
                    game.light.score = pieceCount - lightPieces;
                    game.dark.pieces = darkPieces;
                    game.dark.score = pieceCount - darkPieces;

                    loopBoardStates(gameConsumer, game, 0);
                }
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
                gameConsumer.accept(game);
            } else {
                loopBoardStates(gameConsumer, game, nextBoardIndex);
            }
        }
    }

    public static void main(String[] args) {
        for (int startingPieces = 1; startingPieces <= 7; ++startingPieces) {
            System.out.println("Finkel with " + startingPieces + " starting pieces");
            StateLUT finkel = new StateLUT(GameSettings.FINKEL.withStartingPieceCount(startingPieces));
            System.out.println("  " + finkel.countStates() + " states");
        }

        System.out.println("Aseb");
        StateLUT aseb = new StateLUT(GameSettings.ASEB);
        System.out.println(
                "  " + aseb.countStates() + " states"
        );

        System.out.println("Masters");
        StateLUT masters = new StateLUT(GameSettings.MASTERS);
        System.out.println(
                "  " + masters.countStates() + " states"
        );

        System.out.println("Blitz");
        StateLUT blitz = new StateLUT(GameSettings.BLITZ);
        System.out.println(
                "  " + blitz.countStates() + " states"
        );
    }
}
