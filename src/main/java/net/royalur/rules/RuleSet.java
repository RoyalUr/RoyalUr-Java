package net.royalur.rules;

import net.royalur.model.*;
import net.royalur.model.shape.StandardBoardShape;
import net.royalur.model.state.WaitingForMoveGameState;
import net.royalur.model.state.WaitingForRollGameState;
import net.royalur.rules.simple.SimpleRuleSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A set of rules that govern the play of a game of the Royal Game of Ur.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public abstract class RuleSet<P extends Piece, S extends PlayerState, R extends Roll> {

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
     * Instantiates a set of rules to be used by a game of the Royal Game of Ur.
     * @param boardShape The shape of the game board.
     * @param paths      The paths that the players must take around the board.
     * @param dice       The dice that are used to generate dice rolls.
     */
    protected RuleSet(
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull Dice<R> dice
    ) {
        if (!boardShape.isCompatible(paths.lightPath)) {
            throw new IllegalArgumentException(
                    "The " + paths.getIdentifier() + " paths are not compatible with the " +
                            boardShape.getIdentifier() + " board shape"
            );
        }
        this.boardShape = boardShape;
        this.paths = paths;
        this.dice = dice;
    }

    /**
     * Generates an identifier to identify this set of rules.
     * This does not include identifying the board shape, path, or dice used.
     * @return An identifier to identify this set of rules.
     */
    public @Nonnull String getIdentifier() {
        throw new UnsupportedOperationException("This rule set does not have an identifier (" + getClass() + ")");
    }

    /**
     * Gets a descriptor that can be used to uniquely identify these rules,
     * the board shape, the path, and the dice used.
     * @return A descriptor to describe these rules, the board shape, the path,
     *         and the dice used.
     */
    public @Nonnull String getDescriptor() {
        // Get the identifiers of the rules, board shape, paths, and dice.
        String rulesId = getIdentifier();
        String shapeId = boardShape.getIdentifier();
        String pathsId = paths.getIdentifier();
        String diceId = dice.getIdentifier();

        // Conditionally exclude IDs if they are the most common 'default'.
        List<String> components = new ArrayList<>();
        components.add(pathsId);
        if (!SimpleRuleSet.ID.equals(rulesId)) {
            components.add(rulesId + " Rules");
        }
        if (!shapeId.equals(pathsId) && !StandardBoardShape.ID.equals(shapeId)) {
            components.add(shapeId + " Board Shape");
        }
        if (!StandardDice.ID.equals(diceId)) {
            components.add(diceId + " Dice");
        }

        // Add it all together.
        return String.join(", ", components);
    }

    /**
     * Generates an empty board of the right shape to use to play a game using this rule set.
     * @return An empty board to use to play a game using this rule set.
     */
    public @Nonnull Board<P> generateEmptyBoard() {
        return new Board<>(boardShape);
    }

    /**
     * Generates the starting state for the {@code player} player.
     * @param player The player to create the starting state for.
     * @return A player state for the player {@code player} with name {@code name}.
     */
    public abstract @Nonnull S generateNewPlayerState(@Nonnull Player player);

    /**
     * Generates a random dice roll using the dice of this rule set.
     * @return A random dice roll.
     */
    public @Nonnull R rollDice() {
        return dice.roll();
    }

    /**
     * Generates a roll of the dice of value {@code value} using the dice of this rule set.
     * @param value The value of the dice to roll.
     * @return A roll of the dice of value {@code value}.
     */
    public @Nonnull R rollDice(int value) {
        return dice.roll(value);
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

    /**
     * Applies the roll {@code roll} to the state {@code state} to generate
     * the new state of the game. Multiple game states may be returned to
     * include information game states for maintaining history. However, the
     * latest or highest-index game state will represent the state of the game
     * after the move was made.
     * @param state The current state of the game.
     * @param roll The roll that the player made.
     * @return A list of new game states after the given move was made. The
     *         list may include historical information game states, and will
     *         always include the new state of the game as its last element.
     */
    public abstract @Nonnull List<GameState<P, S, R>> applyRoll(
            @Nonnull WaitingForRollGameState<P, S, R> state, @Nonnull R roll);

    /**
     * Applies the move {@code move} to the state {@code state} to generate
     * the new state of the game. Multiple game states may be returned to
     * include information game states for maintaining history. However, the
     * latest or highest-index game state will represent the state of the game
     * after the move was made.
     * <p>
     * This method does not check that the given move is a valid move
     * from the current game state.
     *
     * @param state The current state of the game.
     * @param move The move that the player chose to make from this position.
     * @return A list of new game states after the given move was made. The
     *         list may include historical information game states, and will
     *         always include the new state of the game as its last element.
     */
    public abstract @Nonnull List<GameState<P, S, R>> applyMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state, @Nonnull Move<P> move);

    @Override
    public @Nonnull String toString() {
        try {
            return getIdentifier() + " Rules";
        } catch (UnsupportedOperationException e) {
            return "Unknown Rules";
        }
    }
}
