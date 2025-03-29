package net.royalur.rules.state;

import net.royalur.model.*;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

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
     * Additional miscellaneous metadata about this state.
     */
    private final Map<String, String> metadata = new HashMap<>();

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
            throw new IllegalArgumentException("timeSinceGameStartMs must be >= 0");

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
     * Add a piece of metadata to this state.
     * @param key The identifier for the metadata to add.
     * @param value The value associated with the key.
     */
    public void addMetadata(String key, String value) {
        metadata.put(key, value);
    }

    /**
     * Adds many pieces of metadata to this state.
     * @param entries The pieces of metadata to add.
     */
    public void addMetadata(Map<String, String> entries) {
        metadata.putAll(entries);
    }

    /**
     * Removes a piece of metadata from this state.
     * @param key The identifier for the metadata to remove.
     * @return The value that was associated with the key, or null if none existed.
     */
    public String removeMetadata(String key) {
        return metadata.remove(key);
    }

    /**
     * Removes all metadata from this state.
     */
    public void clearMetadata() {
        metadata.clear();
    }

    /**
     * Checks whether this state has metadata associated with the given key.
     * @param key The identifier to check for.
     * @return Whether the key exists in the metadata.
     */
    public boolean hasMetadata(String key) {
        return metadata.containsKey(key);
    }

    /**
     * Retrieves the value of a specific metadata entry.
     * @param key The identifier of the metadata to retrieve.
     * @return The value associated with the key, or null if none exists.
     */
    public String getMetadata(String key) {
        return metadata.get(key);
    }

    /**
     * Gets the metadata associated with this state.
     * @return The metadata associated with this state.
     */
    public Map<String, String> getMetadata() {
        return metadata;
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
