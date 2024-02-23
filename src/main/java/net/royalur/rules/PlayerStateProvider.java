package net.royalur.rules;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.model.PlayerState;

/**
 * An interface that provides the manipulation of PlayerStates as a game progresses.
 */
public interface PlayerStateProvider {

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
    PlayerState create(PlayerType player, int pieces, int score);

    /**
     * Generates the starting state for the {@code player} player.
     * @param player The player to create the starting state for.
     * @return A player state for the player {@code player}.
     */
    PlayerState createStartingState(PlayerType player);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * with the given piece introduced to the board.
     * @param playerState The player state to modify the pieces of.
     * @param piece The piece that was introduced to the board.
     * @return A new player state after the given piece was introduced to the board.
     */
    PlayerState applyPieceIntroduced(PlayerState playerState, Piece piece);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * with the given piece captured.
     * @param playerState The player state to modify the pieces of.
     * @param piece The piece that was captured.
     * @return A new player state after the given piece was captured.
     */
    PlayerState applyPieceCaptured(PlayerState playerState, Piece piece);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * with the given piece scored.
     * @param playerState The player state to modify the score of.
     * @param piece The piece that was scored.
     * @return A new player state with the given new piece scored.
     */
    PlayerState applyPieceScored(PlayerState playerState, Piece piece);
}
