package net.royalur.model;

import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.DiceType;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.*;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.BoardShape;
import net.royalur.model.shape.BoardShapeFactory;
import net.royalur.model.shape.StandardBoardShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Settings for running games of the Royal Game of Ur. This is built for
 * convenience, and cannot represent all possible combinations of rules
 * that can be used to play the Royal Game of Ur. If a more exotic set of
 * rules is desired, then you will need to construct your games manually.
 */
public class GameSettings {

    /**
     * The rules used in the YouTube video Tom Scott vs. Irving Finkel.
     */
    public static final GameSettings FINKEL = new GameSettings(
            new StandardBoardShape(),
            new BellPathPair(),
            DiceType.FOUR_BINARY,
            7,
            true,
            true,
            false
    );

    /**
     * The settings proposed by James Masters.
     */
    public static final GameSettings MASTERS = new GameSettings(
            new StandardBoardShape(),
            new MastersPathPair(),
            DiceType.FOUR_BINARY,
            7,
            false,
            true,
            false
    );

    /**
     * The settings used for Aseb.
     */
    public static final GameSettings ASEB = new GameSettings(
            new AsebBoardShape(),
            new AsebPathPair(),
            DiceType.FOUR_BINARY,
            5,
            true,
            true,
            false
    );

    /**
     * The settings used for Blitz.
     */
    public static final GameSettings BLITZ = new GameSettings(
            new StandardBoardShape(),
            new MastersPathPair(),
            DiceType.FOUR_BINARY,
            5,
            false,
            true,
            true
    );

    /**
     * The shape of the game board.
     */
    private final BoardShape boardShape;

    /**
     * The paths that each player must take around the board.
     */
    private final PathPair paths;

    /**
     * A generator for the dice that should be used to generate dice rolls in games.
     */
    private final DiceFactory diceFactory;

    /**
     * The number of pieces that each player starts with.
     */
    private final int startingPieceCount;

    /**
     * Whether pieces on rosette tiles are safe from capture.
     */
    private final boolean safeRosettes;

    /**
     * Whether landing on a rosette tile grants the player an
     * additional roll of the dice.
     */
    private final boolean rosettesGrantExtraRolls;

    /**
     * Whether capturing a piece grants the player an additional
     * roll of the dice.
     */
    private final boolean capturesGrantExtraRolls;

    /**
     * Instantiates a new set of game settings for the Royal Game of Ur.
     * @param boardShape The shape of the game board.
     * @param paths The paths that each player must take around the board.
     * @param diceFactory A generator for the dice that should be used to generate dice rolls.
     * @param startingPieceCount The number of pieces that each player starts with.
     * @param safeRosettes Whether pieces on rosette tiles are safe from capture.
     * @param rosettesGrantExtraRolls Whether landing on a rosette grants an extra roll.
     * @param capturesGrantExtraRolls Whether capturing a piece grants an extra roll.
     */
    public GameSettings(
            BoardShape boardShape,
            PathPair paths,
            DiceFactory diceFactory,
            int startingPieceCount,
            boolean safeRosettes,
            boolean rosettesGrantExtraRolls,
            boolean capturesGrantExtraRolls
    ) {
        if (startingPieceCount < 1) {
            throw new IllegalArgumentException(
                    "startingPieceCount must be at least 1, not: " + startingPieceCount
            );
        }

        this.boardShape = boardShape;
        this.paths = paths;
        this.diceFactory = diceFactory;
        this.startingPieceCount = startingPieceCount;
        this.safeRosettes = safeRosettes;
        this.rosettesGrantExtraRolls = rosettesGrantExtraRolls;
        this.capturesGrantExtraRolls = capturesGrantExtraRolls;
    }

    /**
     * Gets the shape of the game board.
     * @return The shape of the game board.
     */
    public BoardShape getBoardShape() {
        return boardShape;
    }

    /**
     * Gets the paths that each player must take around the board.
     * @return The paths that each player must take around the board.
     */
    public PathPair getPaths() {
        return paths;
    }

    /**
     * Gets the generator of the dice that should be used to generate dice rolls.
     * @return The generator of the dice that should be used to generate dice rolls.
     */
    public DiceFactory getDice() {
        return diceFactory;
    }

    /**
     * Gets the number of pieces that each player starts with.
     * @return The number of pieces that each player starts with.
     */
    public int getStartingPieceCount() {
        return startingPieceCount;
    }

    /**
     * Gets whether pieces on rosette tiles are safe from capture.
     * @return Whether pieces on rosette tiles are safe from capture.
     */
    public boolean areRosettesSafe() {
        return safeRosettes;
    }

    /**
     * Gets whether landing on a rosette tile grants the player an
     * additional roll of the dice.
     * @return Whether landing on a rosette grants an extra roll.
     */
    public boolean doRosettesGrantExtraRolls() {
        return rosettesGrantExtraRolls;
    }

    /**
     * Gets whether capturing a piece grants the player an additional
     * roll of the dice.
     * @return Whether capturing a piece grants an extra roll.
     */
    public boolean doCapturesGrantExtraRolls() {
        return capturesGrantExtraRolls;
    }

    /**
     * Generates new game settings with {@code boardShape}.
     * @param boardShape The board shape to use for the new game settings.
     * @return New game settings updated with a new board shape.
     */
    public GameSettings withBoardShape(BoardShape boardShape) {
        return new GameSettings(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }

    /**
     * Generates new game settings with a board shape from {@code boardShapeFactory}.
     * @param boardShapeFactory The factory to use to generate the board shape for
     *                          the new game settings.
     * @return New game settings updated with a new board shape.
     */
    public GameSettings withBoardShape(BoardShapeFactory boardShapeFactory) {
        return withBoardShape(boardShapeFactory.createBoardShape());
    }

    /**
     * Generates new game settings with {@code paths}.
     * @param paths The paths to use for the new game settings.
     * @return New game settings updated with new paths.
     */
    public GameSettings withPaths(PathPair paths) {
        return new GameSettings(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }

    /**
     * Generates new game settings with paths generated by {@code pathsFactory}.
     * @param pathsFactory The factory to use to generate the paths for
     *                     the new game settings.
     * @return New game settings updated with new paths.
     */
    public GameSettings withPaths(PathPairFactory pathsFactory) {
        return withPaths(pathsFactory.createPathPair());
    }

    /**
     * Generates new game settings with {@code diceFactory}.
     * @param diceFactory The factory to use to generate dice for games
     *                    made using the new game settings.
     * @return New game settings updated with a new generator of dice.
     */
    public GameSettings withDice(DiceFactory diceFactory) {
        return new GameSettings(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }

    /**
     * Generates new game settings with {@code startingPieceCount}.
     * @param startingPieceCount The number of pieces for each player to
     *                           start with in the new game settings.
     * @return New game settings updated with a new starting piece count.
     */
    public GameSettings withStartingPieceCount(int startingPieceCount) {
        return new GameSettings(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }

    /**
     * Generates new game settings with {@code safeRosettes}.
     * @param safeRosettes Whether rosettes should be safe from
     *                     capture in the new game settings.
     * @return New game settings updated with a new value for
     *         whether rosettes are safe.
     */
    public GameSettings withSafeRosettes(boolean safeRosettes) {
        return new GameSettings(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }

    /**
     * Generates new game settings with {@code rosettesGrantExtraRolls}.
     * @param rosettesGrantExtraRolls Whether landing on a rosette should grant
     *                                an extra roll in the new game settings.
     * @return New game settings updated with a new value for
     *         whether landing on rosettes grants an extra roll.
     */
    public GameSettings withRosettesGrantExtraRolls(boolean rosettesGrantExtraRolls) {
        return new GameSettings(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }

    /**
     * Generates new game settings with {@code capturesGrantExtraRolls}.
     * @param capturesGrantExtraRolls Whether capturing a piece should grant
     *                                an extra roll in the new game settings.
     * @return New game settings updated with a new value for
     *         whether capturing a piece grants an extra roll.
     */
    public GameSettings withCapturesGrantExtraRolls(boolean capturesGrantExtraRolls) {
        return new GameSettings(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        GameSettings other = (GameSettings) obj;
        return boardShape.equals(other.boardShape)
                && paths.equals(other.paths)
                && diceFactory.equals(other.diceFactory)
                && startingPieceCount == other.startingPieceCount
                && safeRosettes == other.safeRosettes
                && rosettesGrantExtraRolls == other.rosettesGrantExtraRolls
                && capturesGrantExtraRolls == other.capturesGrantExtraRolls;
    }
}
