package net.royalur.rules.state;

import net.royalur.model.*;

import javax.annotation.Nullable;

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
     * Gets the time this state was created, measured by the number of
     * milliseconds elapsed since the start of the game.
     */
    private final long timeSinceGameStartMs;

    /**
     * Instantiates a game state.
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     * @param timeSinceGameStartMs The time this state was created.
     */
    public GameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            long timeSinceGameStartMs
    ) {
        if (lightPlayer.getPlayer() != PlayerType.LIGHT)
            throw new IllegalArgumentException("lightPlayer should be a Player.LIGHT, not " + lightPlayer.getPlayer());
        if (darkPlayer.getPlayer() != PlayerType.DARK)
            throw new IllegalArgumentException("darkPlayer should be a Player.DARK, not " + darkPlayer.getPlayer());
        if (timeSinceGameStartMs < 0)
            throw new IllegalArgumentException("secondsSinceGameStart must be >= 0");

        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
        this.timeSinceGameStartMs = timeSinceGameStartMs;
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
    public PlayerState getPlayerState(PlayerType player) {
        return switch (player) {
            case LIGHT -> getLightPlayer();
            case DARK -> getDarkPlayer();
        };
    }

    /**
     * Gets the time this state was created, measured by the number of
     * milliseconds elapsed since the start of the game.
     * @return The time this state was created.
     */
    public long getTimeSinceGameStartMs() {
        return timeSinceGameStartMs;
    }

    /**
     * Get the subject player of the game state. e.g., player
     * to roll/move, player that rolled/moved, player that won.
     */
    public abstract @Nullable PlayerType getSubject();

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof GameState other))
            return false;

        return board.equals(other.board)
                && lightPlayer.equals(other.lightPlayer)
                && darkPlayer.equals(other.darkPlayer)
                && timeSinceGameStartMs == other.timeSinceGameStartMs;
    }
}
