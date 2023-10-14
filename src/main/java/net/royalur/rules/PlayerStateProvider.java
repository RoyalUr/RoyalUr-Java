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
     * Generates the starting state for the {@code player} player.
     * @param player The player to create the starting state for.
     * @return A player state for the player {@code player} with name {@code name}.
     */
    @Nonnull S create(@Nonnull PlayerType player);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * but after the given piece was introduced to the board.
     * @param playerState The player state to modify the pieces of.
     * @param piece The piece that was introduced to the board.
     * @return A new player state after the given piece was introduced to the board.
     */
    @Nonnull S applyPieceIntroduced(@Nonnull S playerState, @Nonnull P piece);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * but after the given piece was captured.
     * @param playerState The player state to modify the pieces of.
     * @param piece The piece that was captured.
     * @return A new player state after the given piece was captured.
     */
    @Nonnull S applyPieceCaptured(@Nonnull S playerState, @Nonnull P piece);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * but after the new piece was scored.
     * @param playerState The player state to modify the score of.
     * @param piece The piece that was scored.
     * @return A new player state with the given new piece scored.
     */
    @Nonnull S applyPieceScored(@Nonnull S playerState, @Nonnull P piece);
}
