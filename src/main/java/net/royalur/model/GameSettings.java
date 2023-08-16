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

/**
 * Settings for running games of the Royal Game of Ur.
 * This is built for convenience, and cannot represent all
 * possible combinations of rules that can be used to play
 * the Royal Game of Ur. If a more exotic set of rules is
 * desired, then you will need to construct your games
 * manually.
 */
public class GameSettings<R extends Roll> {

    /**
     * The rules used in the YouTube video Tom Scott vs. Irving Finkel.
     */
    public static final @Nonnull GameSettings<Roll> FINKEL = new GameSettings<>(
            new StandardBoardShape(),
            new BellPathPair(),
            DiceType.FOUR_BINARY,
            7,
            true,
            true,
            false
    );

    /**
     * The default settings used on RoyalUr.net.
     */
    public static final @Nonnull GameSettings<Roll> ROYALUR_NET = FINKEL;

    /**
     * The settings proposed by James Masters.
     */
    public static final @Nonnull GameSettings<Roll> MASTERS = new GameSettings<>(
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
    public static final @Nonnull GameSettings<Roll> ASEB = new GameSettings<>(
            new AsebBoardShape(),
            new AsebPathPair(),
            DiceType.FOUR_BINARY,
            5,
            true,
            true,
            false
    );

    /**
     * The shape of the game board.
     */
    private final @Nonnull BoardShape boardShape;

    /**
     * The paths that each player must take around the board.
     */
    private final @Nonnull PathPair paths;

    /**
     * A generator for the dice that should be used to generate dice rolls in games.
     */
    private final @Nonnull DiceFactory<R> diceFactory;

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
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull DiceFactory<R> diceFactory,
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
    public @Nonnull BoardShape getBoardShape() {
        return boardShape;
    }

    /**
     * Gets the paths that each player must take around the board.
     * @return The paths that each player must take around the board.
     */
    public @Nonnull PathPair getPaths() {
        return paths;
    }

    /**
     * Gets the generator of the dice that should be used to generate dice rolls.
     * @return The generator of the dice that should be used to generate dice rolls.
     */
    public @Nonnull DiceFactory<R> getDice() {
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
    public @Nonnull GameSettings<R> withBoardShape(@Nonnull BoardShape boardShape) {
        return new GameSettings<>(
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
    public @Nonnull GameSettings<R> withBoardShape(@Nonnull BoardShapeFactory boardShapeFactory) {
        return withBoardShape(boardShapeFactory.create());
    }

    /**
     * Generates new game settings with {@code paths}.
     * @param paths The paths to use for the new game settings.
     * @return New game settings updated with new paths.
     */
    public @Nonnull GameSettings<R> withPaths(@Nonnull PathPair paths) {
        return new GameSettings<>(
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
    public @Nonnull GameSettings<R> withPaths(@Nonnull PathPairFactory pathsFactory) {
        return withPaths(pathsFactory.create());
    }

    /**
     * Generates new game settings with {@code diceFactory}.
     * @param diceFactory The factory to use to generate dice for games
     *                    made using the new game settings.
     * @return New game settings updated with a new generator of dice.
     */
    public <NEW_R extends Roll> @Nonnull GameSettings<NEW_R> withDice(
            @Nonnull DiceFactory<NEW_R> diceFactory
    ) {
        return new GameSettings<>(
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
    public @Nonnull GameSettings<R> withStartingPieceCount(int startingPieceCount) {
        return new GameSettings<>(
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
    public @Nonnull GameSettings<R> withSafeRosettes(boolean safeRosettes) {
        return new GameSettings<>(
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
    public @Nonnull GameSettings<R> withRosettesGrantExtraRolls(boolean rosettesGrantExtraRolls) {
        return new GameSettings<>(
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
    public @Nonnull GameSettings<R> withCapturesGrantExtraRolls(boolean capturesGrantExtraRolls) {
        return new GameSettings<>(
                boardShape, paths, diceFactory,
                startingPieceCount, safeRosettes,
                rosettesGrantExtraRolls, capturesGrantExtraRolls
        );
    }
}
