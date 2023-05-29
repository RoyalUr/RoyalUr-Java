package net.royalur.rules;

import net.royalur.model.*;
import net.royalur.model.path.PathPair;
import net.royalur.model.state.WaitingForMoveGameState;
import net.royalur.model.state.WaitingForRollGameState;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A set of rules that govern the play of a game of the Royal Game of Ur.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public interface RuleSet<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * Gets the shape of the board used in this rule set.
     * @return The shape of the game board.
     */
    @Nonnull BoardShape getBoardShape();

    /**
     * Gets the paths that the players must take around the board.
     * @return The paths that players must take around the board.
     */
    @Nonnull
    PathPair getPaths();

    /**
     * Gets the dice that are used to generate dice rolls.
     * @return The dice that are used to generate dice rolls.
     */
    @Nonnull Dice<R> getDice();

    /**
     * Gets the provider of piece manipulations.
     * @return The provider of making piece changes.
     */
    @Nonnull PieceProvider<P> getPieceProvider();

    /**
     * Gets the provider of player state manipulations.
     * @return The provider of making player state changes.
     */
    @Nonnull PlayerStateProvider<S> getPlayerStateProvider();

    /**
     * Generates the initial state for a game.
     * @return The initial state for a game.
     */
    @Nonnull GameState<P, S, R> generateInitialGameState();

    /**
     * Finds all available moves from the current state of the board and the player,
     * and the roll that they made of the dice.
     * @param board The current state of the board.
     * @param player The current state of the player.
     * @param roll The roll that was made. Must be non-zero.
     * @return A list of all the available moves from the position.
     */
    @Nonnull List<Move<P>> findAvailableMoves(
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
    @Nonnull List<GameState<P, S, R>> applyRoll(
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
    @Nonnull List<GameState<P, S, R>> applyMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state, @Nonnull Move<P> move);
}
