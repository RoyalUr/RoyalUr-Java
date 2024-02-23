package net.royalur.model;

import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * A move that can be made on a board.
 */
public class Move {

    /**
     * The instigator of this move.
     */
    private final PlayerType player;

    /**
     * The origin of the move. If this is {@code null}, it represents
     * moving a new piece onto the board.
     */
    private final @Nullable Tile source;

    /**
     * The piece on the board to be moved, or {@code null} if a new
     * piece is to be moved onto the board.
     */
    private final @Nullable Piece sourcePiece;

    /**
     * The destination of the move. If this is {@code null}, it represents
     * moving a piece off of the board.
     */
    private final @Nullable Tile dest;

    /**
     * The piece that will be placed at the destination of the move, or
     * {@code null} if moving a piece off of the board.
     */
    private final @Nullable Piece destPiece;

    /**
     * The piece that will be captured by this move, or {@code null}
     * if no piece would be captured by this move.
     */
    private final @Nullable Piece capturedPiece;

    /**
     * Creates a new move with origin {@code source} and destination {@code dest}.
     * If {@code source} is {@code null}, it represents moving a new piece onto
     * the board. If {@code dest} is {@code null}, it represents moving a piece
     * off of the board.
     *
     * @param player The player that is the instigator of this move.
     * @param source The origin of the move. If this is {@code null}, it represents
     *               moving a new piece onto the board.
     * @param sourcePiece The piece on the board to be moved, or {@code null} if a new
     *                    piece is to be moved onto the board.
     * @param dest The destination of the move. If this is {@code null}, it represents
     *             moving a piece off of the board.
     * @param destPiece The piece that will be placed at the destination of the move, or
     *                  {@code null} if moving a piece off of the board.
     * @param capturedPiece The piece that will be captured by this move, or {@code null}
     *                      if no piece would be captured by this move.
     */
    public Move(
            PlayerType player,
            @Nullable Tile source, @Nullable Piece sourcePiece,
            @Nullable Tile dest, @Nullable Piece destPiece,
            @Nullable Piece capturedPiece
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
    public PlayerType getPlayer() {
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
    public boolean isDestRosette(BoardShape shape) {
        return dest != null && shape.isRosette(dest);
    }

    /**
     * Gets the source tile of this move, or else returns null if there
     * is no source piece, in the case where a new piece is moved onto the board.
     * @return The source tile of this move, or null.
     */
    public @Nullable Tile getSourceOrNull() {
        return source;
    }

    /**
     * Gets the source tile of this move. If there is no source tile, in the
     * case where a new piece is moved onto the board, this will throw an error.
     * @return The source tile of this move.
     */
    public Tile getSource() {
        if (source == null)
            throw new IllegalStateException("This move has no source, as it is introducing a piece");

        return source;
    }

    /**
     * Gets the source tile of this move, including off the board tiles when introducing a piece.
     * @param paths The paths around the board that this move follows.
     * @return The source tile of this move, including off the board tiles when introducing a piece.
     */
    public Tile getSource(PathPair paths) {
        return source != null ? source : paths.getStart(player);
    }

    /**
     * Gets the source piece of this move, or null if there is no source
     * piece, in the case where a new piece is moved onto the board.
     * @return The source piece of this move, or null.
     */
    public @Nullable Piece getSourcePieceOrNull() {
        return sourcePiece;
    }

    /**
     * Gets the source piece of this move. If there is no source piece, in the
     * case where a new piece is moved onto the board, this will throw an error.
     * @return The source piece of this move.
     */
    public Piece getSourcePiece() {
        if (sourcePiece == null)
            throw new IllegalStateException("This move has no source, as it is introducing a piece");

        return sourcePiece;
    }

    /**
     * Gets the destination tile of this move, or else returns null if there
     * is no destination piece, in the case where a piece is moved off the board.
     * @return The destination tile of this move, or null.
     */
    public @Nullable Tile getDestOrNull() {
        return dest;
    }

    /**
     * Gets the destination tile of this move. If there is no destination tile,
     * in the case where a piece is moved off the board, this will throw an error.
     * @return The destination tile of this move.
     */
    public Tile getDest() {
        if (dest == null)
            throw new IllegalStateException("This move has no destination, as it is scoring a piece");

        return dest;
    }

    /**
     * Gets the destination tile of this move, including off the board tiles when scoring a piece.
     * @param paths The paths around the board that this move follows.
     * @return The destination tile of this move, including off the board tiles when scoring a piece.
     */
    public Tile getDest(PathPair paths) {
        return dest != null ? dest : paths.getEnd(player);
    }

    /**
     * Gets the destination piece of this move, or null if there is no
     * destination piece, in the case where a piece is moved off the board.
     * @return The destination piece of this move, or null.
     */
    public @Nullable Piece getDestPieceOrNull() {
        if (destPiece == null)
            throw new IllegalStateException("This move has no destination, as it is scoring a piece");

        return destPiece;
    }

    /**
     * Gets the destination piece of this move. If there is no destination piece,
     * in the case where a piece is moved off the board, this will throw an error.
     * @return The destination piece of this move.
     */
    public Piece getDestPiece() {
        if (destPiece == null)
            throw new IllegalStateException("This move has no destination, as it is scoring a piece");

        return destPiece;
    }

    /**
     * Gets the index of the source piece in the path, or -1 if there is no source piece.
     * @return The index of the source piece in the path.
     */
    public int getSourceIndex() {
        if (sourcePiece == null)
            return -1;

        return sourcePiece.getPathIndex();
    }

    /**
     * Gets the index of the destination piece in the path.
     * @param paths The paths used for this move.
     * @return The index of the destination piece in the path.
     */
    public int getDestIndex(PathPair paths) {
        if (destPiece == null)
            return paths.get(player).size();

        return destPiece.getPathIndex();
    }

    /**
     * Gets the piece that will be captured by this move, or null
     * if there is no piece that will be captured.
     * @return The piece that will be captured by this move, or null.
     */
    public @Nullable Piece getCapturedPieceOrNull() {
        return capturedPiece;
    }

    /**
     * Gets the piece that will be captured by this move. If there is no piece
     * that will be captured, this will throw an error.
     * @return The piece that will be captured by this move.
     */
    public Piece getCapturedPiece() {
        if (capturedPiece == null)
            throw new IllegalStateException("This move does not capture a piece");

        return capturedPiece;
    }

    /**
     * Apply this move to update the board {@code board}.
     * @param board The board to update by applying this move.
     */
    public void apply(Board board) {
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
    public String describe() {
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

        Move other = (Move) obj;
        return Objects.equals(source, other.source) && Objects.equals(sourcePiece, other.sourcePiece) &&
                Objects.equals(dest, other.dest) && Objects.equals(destPiece, other.destPiece) &&
                Objects.equals(capturedPiece, other.capturedPiece);
    }
}
