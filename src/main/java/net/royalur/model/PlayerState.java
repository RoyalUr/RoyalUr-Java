package net.royalur.model;

import javax.annotation.Nullable;

/**
 * A player state represents the state of a single player at a point in the game.
 * This includes the player's score and number of pieces left to play.
 */
public class PlayerState {

    /**
     * The player that this state represents.
     */
    private final PlayerType player;

    /**
     * The number of pieces that the player has available to introduce
     * to the board.
     */
    private final int pieceCount;

    /**
     * The number of pieces that the player has taken off the board.
     */
    private final int score;

    /**
     * Instantiates a state for a player in a game.
     * @param player The player that this state represents.
     * @param pieceCount The number of pieces that the player has available to
     *                   introduce to the board.
     * @param score The number of pieces that the player has taken off the board.
     */
    public PlayerState(PlayerType player, int pieceCount, int score) {
        if (pieceCount < 0)
            throw new IllegalArgumentException("pieceCount must be at least 0. Not: " + pieceCount);
        if (score < 0)
            throw new IllegalArgumentException("score must be at least 0. Not: " + score);

        this.player = player;
        this.pieceCount = pieceCount;
        this.score = score;
    }

    /**
     * Gets the player that this state represents.
     * @return The player that this state represents.
     */
    public PlayerType getPlayer() {
        return player;
    }

    /**
     * Gets the number of pieces that the player has available
     * to introduce to the board.
     * @return The number of pieces that the player has available
     *         to introduce to the board.
     */
    public int getPieceCount() {
        return pieceCount;
    }

    /**
     * Gets the number of pieces that the player has taken off the board.
     * @return The number of pieces that the player has taken off the board.
     */
    public int getScore() {
        return score;
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
        return player == other.player
                && pieceCount == other.pieceCount
                && score == other.score;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(player.getTextName()).append(": ");

        builder.append(pieceCount).append(" Piece");
        if (pieceCount != 1) {
            builder.append("s");
        }

        builder.append(", ").append(score).append(" Score");
        return builder.toString();
    }
}
