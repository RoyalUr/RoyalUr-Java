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

    /**
     * Creates a copy of this player state with a new piece count and score.
     * @param newPieceCount The new piece count for the copied player state.
     * @param newScore The new score for the copied player state.
     * @return A copy of this player state with updated piece count and score.
     */
    public @Nonnull PlayerState copy(int newPieceCount, int newScore) {
        return new PlayerState(player, newPieceCount, newScore);
    }

    /**
     * Copies the player state {@code original} using the provided copy function,
     * and verifies that the copied player state is of the same type as the original.
     * This is used to increase type safety when custom PlayerState
     * subclasses are created.
     * @param original The player state that has been copied.
     * @param copyFn A function to copy the original player state.
     * @return A copy of the player state {@code state}.
     * @param <S> The type of the player state that has been copied.
     */
    @SuppressWarnings("unchecked")
    public static <S extends PlayerState> @Nonnull S safeCopy(
            @Nonnull S original,
            @Nonnull Function<S, PlayerState> copyFn
    ) {
        PlayerState copy = copyFn.apply(original);
        if (!copy.getClass().equals(original.getClass())) {
            throw new RuntimeException(
                    "The copy() method of " + original.getClass() + " produced a different type of player state, " +
                    copy.getClass() + ". The class is likely missing on override for the copy method."
            );
        }

        // Same class, so probably safe to cast (generics of subclasses could be incorrect).
        return (S) copy;
    }

    /**
     * Generates a copy of this player state with a single piece subtracted from this player.
     * @return A copy of this player state with a single piece subtracted from this player.
     */
    public @Nonnull PlayerState subtractPiece() {
        if (pieceCount <= 0)
            throw new IllegalStateException("This player has no pieces");

        return copy(pieceCount - 1, score);
    }

    /**
     * Generates a copy of this player state with a single piece added to this player.
     * @return A copy of this player state with a single piece added to this player.
     */
    public @Nonnull PlayerState addPiece() {
        return copy(pieceCount + 1, score);
    }

    /**
     * Generates a copy of this player state with a single piece added to the score of this player.
     * @return A copy of this player state with a single piece added to the score of this player.
     */
    public @Nonnull PlayerState scorePiece() {
        return copy(pieceCount, score + 1);
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
