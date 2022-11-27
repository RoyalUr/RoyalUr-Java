package net.royalur.rules;

import net.royalur.model.*;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A set of rules that govern the play of
 * a variant of the Royal Game of Ur.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public abstract class RuleSet<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The name of this rule set.
     */
    public final @Nonnull String name;

    /**
     * The shape of the game board.
     */
    public final @Nonnull BoardShape boardShape;

    /**
     * The paths that the players must take around the board.
     */
    public final @Nonnull PathPair paths;

    /**
     * The dice that are used to generate dice rolls.
     */
    public final @Nonnull Dice<R> dice;

    /**
     * @param name       The name of this rule set.
     * @param boardShape The shape of the game board.
     * @param paths      The paths that the players must take around the board.
     * @param dice       The dice that are used to generate dice rolls.
     */
    protected RuleSet(
            @Nonnull String name,
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull Dice<R> dice
    ) {
        if (!boardShape.isCompatible(paths.lightPath)) {
            throw new IllegalArgumentException(
                    "The " + paths.name + " paths are not compatible with the " + boardShape.name + " board shape"
            );
        }
        this.name = name;
        this.boardShape = boardShape;
        this.paths = paths;
        this.dice = dice;
    }

    /**
     * Generates an empty board of the right shape to use to play a game using this rule set.
     * @return An empty board to use to play a game using this rule set.
     */
    public @Nonnull Board<P> generateEmptyBoard() {
        return new Board<>(boardShape);
    }

    /**
     * Generates the starting state for the {@param player} player.
     * @param player The player to create the starting state for.
     * @return A player state for the player {@param player}.
     */
    public @Nonnull S generateNewPlayerState(@Nonnull Player player) {
        return generateNewPlayerState(player, player.name);
    }

    /**
     * Generates the starting state for the {@param player} player.
     * @param player The player to create the starting state for.
     * @param name   The name of the player to create the state for.
     * @return A player state for the player {@param player} with name {@param name}.
     */
    public abstract @Nonnull S generateNewPlayerState(@Nonnull Player player, @Nonnull String name);

    /**
     * Generates a die roll using the dice of this rule set.
     * @return A dice roll.
     */
    public @Nonnull R rollDice() {
        return dice.roll();
    }

    /**
     * Finds all available moves from the current state of the board and the player,
     * and the roll that they made of the dice.
     * @param board The current state of the board.
     * @param player The current state of the player.
     * @param roll The roll that was made. Must be non-zero.
     * @return A list of all the available moves from the position.
     */
    public abstract @Nonnull List<Move<P>> findAvailableMoves(
            @Nonnull Board<P> board, @Nonnull S player, @Nonnull R roll);
}
