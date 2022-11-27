package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A move that can be made on a board.
 */
public class Move<P extends Piece> {

    /**
     * The origin of the move. If this is {@code null}, it represents
     * moving a new piece onto the board.
     */
    protected final @Nullable Tile from;

    /**
     * The piece on the board to be moved, or {@code null} if a new
     * piece is to be moved onto the board.
     */
    protected final @Nullable P fromPiece;

    /**
     * The destination of the move. If this is {@code null}, it represents
     * moving a piece off of the board.
     */
    protected final @Nullable Tile to;

    /**
     * The piece that will be placed at the destination of the move, or
     * {@code null} if moving a piece off of the board.
     */
    protected final @Nullable P toPiece;

    /**
     * The piece that will be displaced by this move, or {@code null}
     * if no piece would be displaced by this move.
     */
    protected final @Nullable P displacedPiece;

    /**
     * Creates a new move with origin {@param from} and destination {@param to}.
     * If {@param from} is {@code null}, it represents moving a new piece onto
     * the board. If {@param to} is {@code null}, it represents moving a piece
     * off of the board.
     *
     * @param from      The origin of the move. If this is {@code null}, it represents
     *                  moving a new piece onto the board.
     * @param fromPiece The piece on the board to be moved, or {@code null} if a new
     *                  piece is to be moved onto the board.
     * @param to        The destination of the move. If this is {@code null}, it represents
     *                  moving a piece off of the board.
     * @param toPiece   The piece that will be placed at the destination of the move, or
     *                  {@code null} if moving a piece off of the board.
     * @param displacedPiece The piece that will be displaced by this move, or {@code null}
     *                       if no piece would be displaced by this move.
     */
    public Move(
            @Nullable Tile from, @Nullable P fromPiece,
            @Nullable Tile to, @Nullable P toPiece,
            @Nullable P displacedPiece
    ) {
        if (from == null && to == null)
            throw new IllegalArgumentException("from and to cannot both be null");
        if ((from == null) ^ (fromPiece == null))
            throw new IllegalArgumentException("from and fromPiece must either be both null, or both non-null");
        if ((to == null) ^ (toPiece == null))
            throw new IllegalArgumentException("from and fromPiece must either be both null, or both non-null");

        this.from = from;
        this.to = to;
        this.fromPiece = fromPiece;
        this.toPiece = toPiece;
        this.displacedPiece = displacedPiece;
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
     * Determines whether this move is displacing an existing piece on the board.
     * @return Whether this move is displacing an existing piece on the board.
     */
    public boolean displacesPiece() {
        return displacedPiece != null;
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
     * Retrieves the piece that will be displaced by this move. If there is no piece
     * that will be displayed, this will throw an error.
     * @return The piece that will be displaced by this move.
     */
    public @Nonnull P getDisplacedPiece() {
        if (displacedPiece == null)
            throw new IllegalStateException("This move does not displace a piece");

        return displacedPiece;
    }

    /**
     * Apply this move to update the board {@param board}.
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

    @Override
    public int hashCode() {
        return Objects.hashCode(from) ^ (31 * Objects.hashCode(fromPiece)) ^
                (113 * Objects.hashCode(to)) ^ (149 * Objects.hashCode(toPiece)) ^
                (191 * Objects.hashCode(displacedPiece));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Move<?> other = (Move<?>) obj;
        return Objects.equals(from, other.from) && Objects.equals(fromPiece, other.fromPiece) &&
                Objects.equals(to, other.to) && Objects.equals(toPiece, other.toPiece) &&
                Objects.equals(displacedPiece, other.displacedPiece);
    }
}
