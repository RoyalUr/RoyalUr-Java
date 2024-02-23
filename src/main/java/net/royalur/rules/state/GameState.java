package net.royalur.rules.state;

import net.royalur.model.*;

/**
 * A game state represents a single point within a game.
 */
public abstract class GameState {

    /**
     * The state of the pieces on the board.
     */
    private final Board board;

    /**
     * The state of the light player.
     */
    private final PlayerState lightPlayer;

    /**
     * The state of the dark player.
     */
    private final PlayerState darkPlayer;

    /**
     * Instantiates a game state.
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     */
    public GameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer
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
    public Board getBoard() {
        return board;
    }

    /**
     * Get the state of the light player.
     * @return The state of the light player.
     */
    public PlayerState getLightPlayer() {
        return lightPlayer;
    }

    /**
     * Get the state of the dark player.
     * @return The state of the dark player.
     */
    public PlayerState getDarkPlayer() {
        return darkPlayer;
    }

    /**
     * Gets the state of the player {@code player}.
     * @param player The player to retrieve the state of.
     * @return The state of the player {@code player}.
     */
    public PlayerState getPlayer(PlayerType player) {
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
    public abstract String describe();
}
