package net.royalur.model;

import net.royalur.model.path.PathPair;
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
    private final @Nullable Tile source;

    /**
     * The piece on the board to be moved, or {@code null} if a new
     * piece is to be moved onto the board.
     */
    private final @Nullable P sourcePiece;

    /**
     * The destination of the move. If this is {@code null}, it represents
     * moving a piece off of the board.
     */
    private final @Nullable Tile dest;

    /**
     * The piece that will be placed at the destination of the move, or
     * {@code null} if moving a piece off of the board.
     */
    private final @Nullable P destPiece;

    /**
     * The piece that will be captured by this move, or {@code null}
     * if no piece would be captured by this move.
     */
    private final @Nullable P capturedPiece;

    /**
     * Creates a new move with origin {@code source} and destination {@code dest}.
     * If {@code source} is {@code null}, it represents moving a new piece onto
     * the board. If {@code dest} is {@code null}, it represents moving a piece
     * off of the board.
     *
     * @param player    The player that is the instigator of this move.
     * @param source      The origin of the move. If this is {@code null}, it represents
     *                  moving a new piece onto the board.
     * @param sourcePiece The piece on the board to be moved, or {@code null} if a new
     *                  piece is to be moved onto the board.
     * @param dest        The destination of the move. If this is {@code null}, it represents
     *                  moving a piece off of the board.
     * @param destPiece   The piece that will be placed at the destination of the move, or
     *                  {@code null} if moving a piece off of the board.
     * @param capturedPiece The piece that will be captured by this move, or {@code null}
     *                       if no piece would be captured by this move.
     */
    public Move(
            @Nonnull PlayerType player,
            @Nullable Tile source, @Nullable P sourcePiece,
            @Nullable Tile dest, @Nullable P destPiece,
            @Nullable P capturedPiece
    ) {
        if ((source == null) ^ (sourcePiece == null))
            throw new IllegalArgumentException("source and sourcePiece must either be both null, or both non-null");
        if ((dest == null) ^ (destPiece == null))
            throw new IllegalArgumentException("source and sourcePiece must either be both null, or both non-null");
        if (dest == null && capturedPiece != null)
            throw new IllegalArgumentException("Moves without a destination cannot have captured a piece");

        this.player = player;
        this.source = source;
        this.dest = dest;
        this.sourcePiece = sourcePiece;
        this.destPiece = destPiece;
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
     * Determines whether this move is moving a piece on the board.
     * @return Whether this move is moving a piece on the board.
     */
    public boolean hasSource() {
        return source != null;
    }

    /**
     * Determines whether this move is moving a new piece onto the board.
     * @return Whether this move is moving a new piece onto the board.
     */
    public boolean isIntroducingPiece() {
        return source == null;
    }

    /**
     * Determines whether this moves a piece to a destination on the board.
     * @return Whether this moves a piece to a destination on the board.
     */
    public boolean hasDest() {
        return dest != null;
    }

    /**
     * Determines whether this move is moving a piece off of the board.
     * @return Whether this move is moving a piece off of the board.
     */
    public boolean isScoringPiece() {
        return dest == null;
    }

    /**
     * Determines whether this move is capturing an existing piece on the board.
     * @return Whether this move is capturing an existing piece on the board.
     */
    public boolean isCapture() {
        return capturedPiece != null;
    }

    /**
     * Determines whether this move will land a piece on a rosette. Under common
     * rule sets, this will give another turn to the player.
     * @param shape The shape of the board.
     * @return Whether this move will land a piece on a rosette.
     */
    public boolean isDestRosette(@Nonnull BoardShape shape) {
        return dest != null && shape.isRosette(dest);
    }

    /**
     * Gets the source tile of this move. If there is no source tile, in the
     * case where a new piece is moved onto the board, this will throw an error.
     * @return The source tile of this move.
     */
    public @Nonnull Tile getSource() {
        if (source == null)
            throw new IllegalStateException("This move has no source, as it is introducing a piece");

        return source;
    }

    /**
     * Gets the source tile of this move, including off the board tiles when introducing a piece.
     * @param paths The paths around the board that this move follows.
     * @return The source tile of this move, including off the board tiles when introducing a piece.
     */
    public @Nonnull Tile getSource(@Nonnull PathPair paths) {
        return source != null ? source : paths.getStart(player);
    }

    /**
     * Gets the source piece of this move. If there is no source piece, in the
     * case where a new piece is moved onto the board, this will throw an error.
     * @return The source piece of this move.
     */
    public @Nonnull P getSourcePiece() {
        if (sourcePiece == null)
            throw new IllegalStateException("This move has no source, as it is introducing a piece");

        return sourcePiece;
    }

    /**
     * Gets the destination tile of this move. If there is no destination tile,
     * in the case where a piece is moved off the board, this will throw an error.
     * @return The destination tile of this move.
     */
    public @Nonnull Tile getDest() {
        if (dest == null)
            throw new IllegalStateException("This move has no destination, as it is scoring a piece");

        return dest;
    }

    /**
     * Gets the destination tile of this move, including off the board tiles when scoring a piece.
     * @param paths The paths around the board that this move follows.
     * @return The destination tile of this move, including off the board tiles when scoring a piece.
     */
    public @Nonnull Tile getDest(@Nonnull PathPair paths) {
        return dest != null ? dest : paths.getEnd(player);
    }

    /**
     * Gets the destination piece of this move. If there is no destination piece,
     * in the case where a piece is moved off the board, this will throw an error.
     * @return The destination piece of this move.
     */
    public @Nonnull P getDestPiece() {
        if (destPiece == null)
            throw new IllegalStateException("This move has no destination, as it is scoring a piece");

        return destPiece;
    }

    /**
     * Gets the piece that will be captured by this move. If there is no piece
     * that will be captured, this will throw an error.
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
        if (source != null) {
            board.set(source, null);
        }
        if (dest != null) {
            board.set(dest, destPiece);
        }
    }

    /**
     * Generates an English description of this move.
     * @return An English description of this move.
     */
    public @Nonnull String describe() {
        boolean scoring = isScoringPiece();
        boolean introducing = isIntroducingPiece();

        if (scoring && introducing)
            return "Introduce and score a piece.";

        if (scoring)
            return "Score a piece from " + getSource() + ".";

        StringBuilder builder = new StringBuilder();
        if (introducing) {
            builder.append("Introduce a piece to ");
        } else {
            builder.append("Move ").append(getSource()).append(" to ");
        }
        if (isCapture()) {
            builder.append("capture ");
        }
        builder.append(getDest());
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(source) ^ (31 * Objects.hashCode(sourcePiece)) ^
                (113 * Objects.hashCode(dest)) ^ (149 * Objects.hashCode(destPiece)) ^
                (191 * Objects.hashCode(capturedPiece));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Move<?> other = (Move<?>) obj;
        return Objects.equals(source, other.source) && Objects.equals(sourcePiece, other.sourcePiece) &&
                Objects.equals(dest, other.dest) && Objects.equals(destPiece, other.destPiece) &&
                Objects.equals(capturedPiece, other.capturedPiece);
    }
}
