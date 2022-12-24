package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A player state represents the state of a single player at a point in the game.
 * This includes the player's score and number of pieces left to play.
 */
public class PlayerState {

    /**
     * The default name to use for players that do not provide a name.
     */
    public static final @Nonnull String ANONYMOUS_NAME = "Anonymous";

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
    private int pieceCount;

    /**
     * The number of pieces that the player has taken off the board.
     */
    private int score;

    /**
     * Instantiates a state for a player in a game.
     * @param player The player that this state represents.
     * @param name The name of the player that this state represents.
     * @param pieceCount The number of pieces that the player has yet to play.
     * @param score The number of pieces that the player has taken off the board.
     */
    public PlayerState(@Nonnull Player player, @Nonnull String name, int pieceCount, int score) {
        if (pieceCount < 0)
            throw new IllegalArgumentException("pieceCount must be at least 0. Not: " + pieceCount);
        if (score < 0)
            throw new IllegalArgumentException("score must be at least 0. Not: " + score);

        this.player = player;
        this.name = name;
        this.pieceCount = pieceCount;
        this.score = score;
    }

    /**
     * Instantiates a state for an anonymous player in a game.
     * @param player The player that this state represents.
     * @param pieceCount The number of pieces that the player has yet to play.
     * @param score The number of pieces that the player has taken off the board.
     */
    public PlayerState(@Nonnull Player player, int pieceCount, int score) {
        this(player, ANONYMOUS_NAME, pieceCount, score);
    }

    /**
     * Instantiates a state that is a copy of {@param template}.
     * @param template Another player state to use as a template to copy from.
     */
    protected PlayerState(@Nonnull PlayerState template) {
        this(template.player, template.name, template.pieceCount, template.score);
    }

    /**
     * Creates an exact copy of this player state.
     * @return An exact copy of this player state.
     */
    public @Nonnull PlayerState copy() {
        return new PlayerState(this);
    }

    /**
     * Produces a copy of the player state {@param state}, and guarantees
     * that the copy is of the same type as {@param state}. This is used
     * to allow custom PlayerState subclasses to be created that can be
     * copied to be modified by rule sets.
     * @param state The player state to copy.
     * @return A copy of the player state {@param state}.
     * @param <S> The type of the player state that is being copied.
     */
    @SuppressWarnings("unchecked")
    public static <S extends PlayerState> @Nonnull S safeCopy(@Nonnull S state) {
        PlayerState copy = state.copy();
        if (!copy.getClass().equals(state.getClass())) {
            throw new RuntimeException(
                    "The copy() method of " + state.getClass() + " produced a different type of player state, " +
                    copy.getClass() + ". The class is likely missing on override for the copy method."
            );
        }

        // Same class, so safe to cast.
        return (S) copy;
    }

    /**
     * Retrieves the number of pieces that this player has yet to play.
     * @return The number of pieces that this player has yet to play.
     */
    public int getPieceCount() {
        return pieceCount;
    }

    /**
     * Retrieves the score of this player.
     * @return The score of this player.
     */
    public int getScore() {
        return score;
    }

    /**
     * Subtract a single piece from this player.
     */
    public void subtractPiece() {
        if (pieceCount <= 0)
            throw new IllegalStateException("This player has no pieces");

        pieceCount -= 1;
    }

    /**
     * Add a single piece to this player.
     */
    public void addPiece() {
        pieceCount += 1;
    }

    /**
     * Add a single piece scored to this player.
     */
    public void scorePiece() {
        score += 1;
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
