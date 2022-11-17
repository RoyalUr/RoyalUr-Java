package net.royalur.model;

import javax.annotation.Nonnull;

/**
 * A game state represents a single point within a game.
 * This class is immutable.
 */
public class GameState {

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
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     */
    public GameState(@Nonnull Board board, @Nonnull PlayerState lightPlayer, @Nonnull PlayerState darkPlayer) {
        if (lightPlayer.player != Player.LIGHT)
            throw new IllegalArgumentException("The lightPlayer should be of Player.LIGHT, not " + lightPlayer.player);
        if (darkPlayer.player != Player.DARK)
            throw new IllegalArgumentException("The darkPlayer should be of Player.DARK, not " + lightPlayer.player);

        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }
}
