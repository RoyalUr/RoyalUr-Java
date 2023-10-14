package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;

/**
 * A game state represents a single point within a game.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public abstract class GameState<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The state of the pieces on the board.
     */
    private final @Nonnull Board<P> board;

    /**
     * The state of the light player.
     */
    private final @Nonnull S lightPlayer;

    /**
     * The state of the dark player.
     */
    private final @Nonnull S darkPlayer;

    /**
     * Instantiates a game state.
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     */
    public GameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer
    ) {
        if (lightPlayer.getPlayer() != PlayerType.LIGHT)
            throw new IllegalArgumentException("lightPlayer should be a Player.LIGHT, not " + lightPlayer.getPlayer());
        if (darkPlayer.getPlayer() != PlayerType.DARK)
            throw new IllegalArgumentException("darkPlayer should be a Player.DARK, not " + darkPlayer.getPlayer());

        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    /**
     * Get the state of the pieces on the board.
     * @return The state of the pieces on the board.
     */
    public @Nonnull Board<P> getBoard() {
        return board;
    }

    /**
     * Get the state of the light player.
     * @return The state of the light player.
     */
    public @Nonnull S getLightPlayer() {
        return lightPlayer;
    }

    /**
     * Get the state of the dark player.
     * @return The state of the dark player.
     */
    public @Nonnull S getDarkPlayer() {
        return darkPlayer;
    }

    /**
     * Gets the state of the player {@code player}.
     * @param player The player to retrieve the state of.
     * @return The state of the player {@code player}.
     */
    public @Nonnull S getPlayer(@Nonnull PlayerType player) {
        return switch (player) {
            case LIGHT -> getLightPlayer();
            case DARK -> getDarkPlayer();
        };
    }

    /**
     * Returns whether this state is a valid state to be played from.
     * @return Whether this state is a valid state to be played from.
     */
    public abstract boolean isPlayable();

    /**
     * Returns whether this state represents a finished game.
     * @return Whether this state represents a finished game.
     */
    public abstract boolean isFinished();

    /**
     * Generates an English text description of the state of the game.
     * @return An English text description of the state of the game.
     */
    public abstract @Nonnull String describe();
}
