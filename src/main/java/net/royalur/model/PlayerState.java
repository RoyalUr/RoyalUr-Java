package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * A player state represents the state of a single player at a point in the game.
 * This includes the player's score and number of pieces left to play.
 */
public class PlayerState {

    /**
     * The player that this state represents.
     */
    public final @Nonnull Player player;

    /**
     * The number of pieces that the player has yet to play.
     */
    public final int pieceCount;

    /**
     * The number of pieces that the player has taken off the board.
     */
    public final int score;

    /**
     * Instantiates a state for a player in a game.
     * @param player The player that this state represents.
     * @param pieceCount The number of pieces that the player has yet to play.
     * @param score The number of pieces that the player has taken off the board.
     */
    public PlayerState(@Nonnull Player player, int pieceCount, int score) {
        if (pieceCount < 0)
            throw new IllegalArgumentException("pieceCount must be at least 0. Not: " + pieceCount);
        if (score < 0)
            throw new IllegalArgumentException("score must be at least 0. Not: " + score);

        this.player = player;
        this.pieceCount = pieceCount;
        this.score = score;
    }

    @Override
    public int hashCode() {
        return player.hashCode() ^ (97 * Integer.hashCode(pieceCount)) ^ (137 * Integer.hashCode(score));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        PlayerState other = (PlayerState) obj;
        return player == other.player && pieceCount == other.pieceCount && score == other.score;
    }

    @Override
    public @Nonnull String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(player.name).append(": ");

        builder.append(pieceCount).append(" Piece");
        if (pieceCount != 1) {
            builder.append("s");
        }

        builder.append(", ").append(score).append(" Score");
        return builder.toString();
    }
}
