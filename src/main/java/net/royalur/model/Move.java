package net.royalur.model;

import net.royalur.model.shape.BoardShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A move that can be made on a board.
 * @param <P> The type of piece that is being moved and that may be captured.
 */
public class Move<P extends Piece> {

    /**
     * The instigator of this move.
     */
    private final @Nonnull PlayerType player;

    /**
     * The origin of the move. If this is {@code null}, it represents
     * moving a new piece onto the board.
     */
    private final @Nullable Tile from;

    /**
     * The piece on the board to be moved, or {@code null} if a new
     * piece is to be moved onto the board.
     */
    private final @Nullable P fromPiece;

    /**
     * The destination of the move. If this is {@code null}, it represents
     * moving a piece off of the board.
     */
    private final @Nullable Tile to;

    /**
     * The piece that will be placed at the destination of the move, or
     * {@code null} if moving a piece off of the board.
     */
    private final @Nullable P toPiece;

    /**
     * The piece that will be captured by this move, or {@code null}
     * if no piece would be captured by this move.
     */
    private final @Nullable P capturedPiece;

    /**
     * Creates a new move with origin {@code from} and destination {@code to}.
     * If {@code from} is {@code null}, it represents moving a new piece onto
     * the board. If {@code to} is {@code null}, it represents moving a piece
     * off of the board.
     *
     * @param player    The player that is the instigator of this move.
     * @param from      The origin of the move. If this is {@code null}, it represents
     *                  moving a new piece onto the board.
     * @param fromPiece The piece on the board to be moved, or {@code null} if a new
     *                  piece is to be moved onto the board.
     * @param to        The destination of the move. If this is {@code null}, it represents
     *                  moving a piece off of the board.
     * @param toPiece   The piece that will be placed at the destination of the move, or
     *                  {@code null} if moving a piece off of the board.
     * @param capturedPiece The piece that will be captured by this move, or {@code null}
     *                       if no piece would be captured by this move.
     */
    public Move(
            @Nonnull PlayerType player,
            @Nullable Tile from, @Nullable P fromPiece,
            @Nullable Tile to, @Nullable P toPiece,
            @Nullable P capturedPiece
    ) {
        if (from == null && to == null)
            throw new IllegalArgumentException("from and to cannot both be null");
        if ((from == null) ^ (fromPiece == null))
            throw new IllegalArgumentException("from and fromPiece must either be both null, or both non-null");
        if ((to == null) ^ (toPiece == null))
            throw new IllegalArgumentException("from and fromPiece must either be both null, or both non-null");

        this.player = player;
        this.from = from;
        this.to = to;
        this.fromPiece = fromPiece;
        this.toPiece = toPiece;
        this.capturedPiece = capturedPiece;
    }

    /**
     * Gets the instigator of this move.
     * @return The instigator of this move.
     */
    public @Nonnull PlayerType getPlayer() {
        return player;
    }

    /**
     * Determines whether this move is moving a new piece onto the board.
     * @return Whether this move is moving a new piece onto the board.
     */
    public boolean isIntroducingPiece() {
        return from == null;
    }

    /**
     * Determines whether this move is moving a piece off of the board.
     * @return Whether this move is moving a piece off of the board.
     */
    public boolean isScoringPiece() {
        return to == null;
    }

    /**
     * Determines whether this move is capturing an existing piece on the board.
     * @return Whether this move is capturing an existing piece on the board.
     */
    public boolean capturesPiece() {
        return capturedPiece != null;
    }

    /**
     * Determines whether this move will land a piece on a rosette. Under common
     * rule sets, this will give another turn to the player.
     * @param shape The shape of the board.
     * @return Whether this move will land a piece on a rosette.
     */
    public boolean isLandingOnRosette(@Nonnull BoardShape shape) {
        return to != null && shape.isRosette(to);
    }

    /**
     * Retrieves the source tile of this move. If there is no source tile, in the
     * case where a new piece is moved onto the board, this will throw an error.
     * @return The source tile of this move.
     */
    public @Nonnull Tile getSource() {
        if (from == null)
            throw new IllegalStateException("This move has no source, as it is introducing a piece");

        return from;
    }

    /**
     * Retrieves the source piece of this move. If there is no source piece, in the
     * case where a new piece is moved onto the board, this will throw an error.
     * @return The source piece of this move.
     */
    public @Nonnull P getSourcePiece() {
        if (fromPiece == null)
            throw new IllegalStateException("This move has no source, as it is introducing a piece");

        return fromPiece;
    }

    /**
     * Retrieves the destination tile of this move. If there is no destination tile,
     * in the case where a piece is moved off the board, this will throw an error.
     * @return The destination tile of this move.
     */
    public @Nonnull Tile getDestination() {
        if (to == null)
            throw new IllegalStateException("This move has no destination, as it is scoring a piece");

        return to;
    }

    /**
     * Retrieves the destination piece of this move. If there is no destination piece,
     * in the case where a piece is moved off the board, this will throw an error.
     * @return The destination piece of this move.
     */
    public @Nonnull P getDestinationPiece() {
        if (toPiece == null)
            throw new IllegalStateException("This move has no source, as it is introducing a piece");

        return toPiece;
    }

    /**
     * Retrieves the piece that will be captured by this move. If there is no piece
     * that will be displayed, this will throw an error.
     * @return The piece that will be captured by this move.
     */
    public @Nonnull P getCapturedPiece() {
        if (capturedPiece == null)
            throw new IllegalStateException("This move does not capture a piece");

        return capturedPiece;
    }

    /**
     * Apply this move to update the board {@code board}.
     * @param board The board to update by applying this move.
     */
    public void apply(@Nonnull Board<P> board) {
        if (from != null) {
            board.set(from, null);
        }
        if (to != null) {
            board.set(to, toPiece);
        }
    }

    /**
     * Gets an English description of this move.
     * @return An English description of this move.
     */
    public @Nonnull String describe() {
        StringBuilder builder = new StringBuilder();
        // Introduce a piece to capture A5
        // Introduce a piece to A3
        // Move A3 to B1
        // Move A3 to capture B1
        // Scored a piece from A3

        if (isScoringPiece()) {
            builder.append("Score a piece from ");
        } else if (isIntroducingPiece()) {
            builder.append("Introduce a piece to ");
        } else {
            builder.append("Move ");
        }

        if (!isIntroducingPiece()) {
            builder.append(getSource());
            if (!isScoringPiece()) {
                builder.append(" to ");
            }
        }

        if (capturesPiece()) {
            builder.append("capture ");
        }

        if (!isScoringPiece()) {
            builder.append(getDestination());
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(from) ^ (31 * Objects.hashCode(fromPiece)) ^
                (113 * Objects.hashCode(to)) ^ (149 * Objects.hashCode(toPiece)) ^
                (191 * Objects.hashCode(capturedPiece));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Move<?> other = (Move<?>) obj;
        return Objects.equals(from, other.from) && Objects.equals(fromPiece, other.fromPiece) &&
                Objects.equals(to, other.to) && Objects.equals(toPiece, other.toPiece) &&
                Objects.equals(capturedPiece, other.capturedPiece);
    }
}
