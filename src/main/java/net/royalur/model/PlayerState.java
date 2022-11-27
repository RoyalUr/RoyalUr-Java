package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A player state represents the state of a single player at a point in the game.
 * This class is immutable.
 */
public class PlayerState {

    /**
     * The player that this state represents.
     */
    public final @Nonnull Player player;

    /**
     * The name of the player that this state represents.
     */
    public final @Nonnull String name;

    /**
     * The number of pieces that the player has yet to play.
     */
    public final int pieceCount;

    /**
     * The number of pieces that the player has taken off the board.
     */
    public final int score;

    /**
     * @param player The player that this state represents.
     * @param name The name of the player that this state represents.
     * @param pieceCount The number of pieces that the player has yet to play.
     * @param score The number of pieces that the player has taken off the board.
     */
    public PlayerState(@Nonnull Player player, @Nonnull String name, int pieceCount, int score) {
        if (pieceCount < 0)
            throw new IllegalArgumentException("pieceCount must be at least 0: Not: " + pieceCount);
        if (score < 0)
            throw new IllegalArgumentException("score must be at least 0: Not: " + score);

        this.player = player;
        this.name = name;
        this.pieceCount = pieceCount;
        this.score = score;
    }

    /**
     * This constructor uses the default name from {@param player}.
     * @param player The player that this state represents.
     * @param pieceCount The number of pieces that the player has yet to play.
     * @param score The number of pieces that the player has taken off the board.
     */
    public PlayerState(@Nonnull Player player, int pieceCount, int score) {
        this(player, player.name, pieceCount, score);
    }

    @Override
    public int hashCode() {
        return player.hashCode() ^ (37 * name.hashCode())
                ^ (97 * Integer.hashCode(pieceCount)) ^ (137 * Integer.hashCode(score));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        PlayerState other = (PlayerState) obj;
        return player == other.player && name.equals(other.name) &&
                pieceCount == other.pieceCount && score == other.score;
    }

    @Override
    public @Nonnull String toString() {
        StringBuilder builder = new StringBuilder();

        // Player name, with colour in brackets if necessary.
        builder.append("Player: ").append(name);
        if (!name.equals(player.name)) {
            builder.append(" (").append(player.name).append(")");
        }
        builder.append("\n");

        builder.append("Pieces: ").append(pieceCount).append("\n");
        builder.append("Score: ").append(score);
        return builder.toString();
    }
}
