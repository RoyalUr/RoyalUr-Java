package net.royalur.model;

import javax.annotation.Nonnull;

/**
 * A game state represents a single point within a game.
 * This class is immutable.
 */
public abstract class GameState {

    /**
     * The type of this game state, representing its purpose.
     */
    public final @Nonnull GameStateType type;

    /**
     * The state of the pieces on the board.
     */
    public final @Nonnull Board board;

    /**
     * The state of the light player.
     */
    public final @Nonnull PlayerState lightPlayer;

    /**
     * The state of the dark player.
     */
    public final @Nonnull PlayerState darkPlayer;

    /**
     * @param type The type of this game state, representing its purpose.
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     */
    public GameState(
            @Nonnull GameStateType type,
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer) {

        if (lightPlayer.player != Player.LIGHT)
            throw new IllegalArgumentException("The lightPlayer should be of Player.LIGHT, not " + lightPlayer.player);
        if (darkPlayer.player != Player.DARK)
            throw new IllegalArgumentException("The darkPlayer should be of Player.DARK, not " + lightPlayer.player);

        this.type = type;
        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    /**
     * Returns whether this state is a valid state to be played from.
     * @return Whether this state is a valid state to be played from.
     */
    public abstract boolean isPlayable();

    /**
     * Retrieves the state of the player {@param player}.
     * @param player The player to retrieve the state of.
     * @return The state of the player {@param player}.
     */
    public @Nonnull PlayerState getPlayer(@Nonnull Player player) {
        switch (player) {
            case LIGHT: return lightPlayer;
            case DARK: return darkPlayer;
            default:
                throw new IllegalArgumentException("Unknown Player " + player);
        }
    }

    /**
     * Generates an English text description of the state of the game.
     * @return An English text description of the state of the game.
     */
    public abstract @Nonnull String describe();
}
