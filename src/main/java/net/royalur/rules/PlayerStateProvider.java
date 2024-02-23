package net.royalur.rules;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.model.PlayerState;

import javax.annotation.Nonnull;

/**
 * An interface that provides the manipulation of PlayerStates as a game progresses.
 */
public interface PlayerStateProvider<
        P extends Piece,
        S extends PlayerState
> {

    /**
     * Gets the number of pieces that players start with.
     * @return The number of pieces that players start with.
     */
    int getStartingPieceCount();

    /**
     * Generates a state for the {@code player} player.
     * @param player The player to create the starting state for.
     * @param pieces The number of pieces the player has yet to play.
     * @param score The number of pieces the player has scored.
     * @return A player state for the player {@code player}.
     */
    S create(PlayerType player, int pieces, int score);

    /**
     * Generates the starting state for the {@code player} player.
     * @param player The player to create the starting state for.
     * @return A player state for the player {@code player}.
     */
    S createStartingState(PlayerType player);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * with the given piece introduced to the board.
     * @param playerState The player state to modify the pieces of.
     * @param piece The piece that was introduced to the board.
     * @return A new player state after the given piece was introduced to the board.
     */
    S applyPieceIntroduced(S playerState, P piece);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * with the given piece captured.
     * @param playerState The player state to modify the pieces of.
     * @param piece The piece that was captured.
     * @return A new player state after the given piece was captured.
     */
    S applyPieceCaptured(S playerState, P piece);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * with the given piece scored.
     * @param playerState The player state to modify the score of.
     * @param piece The piece that was scored.
     * @return A new player state with the given new piece scored.
     */
    S applyPieceScored(S playerState, P piece);
}
