package net.royalur.rules;

import net.royalur.model.Player;
import net.royalur.model.PlayerState;

import javax.annotation.Nonnull;

/**
 * Provides the manipulation of PlayerStates as a game progresses.
 */
public interface PlayerStateProvider<S extends PlayerState> {

    /**
     * Generates the starting state for the {@code player} player.
     * @param player The player to create the starting state for.
     * @return A player state for the player {@code player} with name {@code name}.
     */
    @Nonnull S create(@Nonnull Player player);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * but with {@code pieces} added or removed pieces.
     * @param playerState The player state to modify the pieces of.
     * @param pieces The number of pieces to add or subtract.
     * @return A new player state with {@code pieces} added or removed pieces.
     */
    @Nonnull S applyPiecesChange(S playerState, int pieces);

    /**
     * Generates a new player state that is a copy of {@code playerState},
     * but with {@code pieces} added or removed score.
     * @param playerState The player state to modify the score of.
     * @param pieces The number of score to add or subtract.
     * @return A new player state with {@code pieces} added or removed
     *         from its score.
     */
    @Nonnull S applyScoreChange(S playerState, int pieces);
}
