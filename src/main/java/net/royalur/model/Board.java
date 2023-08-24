package net.royalur.model;

import net.royalur.model.shape.BoardShape;
import net.royalur.util.Cast;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Stores the placement of pieces on the tiles of a Royal Game of Ur board.
 * @param <P> The type of pieces that may be placed on this board.
 */
public class Board<P extends Piece> implements Iterable<P> {

    /**
     * The shape of this board.
     */
    private final @Nonnull BoardShape shape;

    /**
     * The number of x-coordinates that exist in this board.
     */
    private final int width;

    /**
     * The number of y-coordinates that exist in this board.
     */
    private final int height;

    /**
     * The pieces on the tiles of this board.
     * Tiles that contain no piece are null.
     */
    private final @Nonnull Piece[] pieces;

    /**
     * Instantiates an empty board with the shape {@code shape}.
     * @param shape The shape of this board.
     */
    public Board(@Nonnull BoardShape shape) {
        this.shape = shape;
        this.width = shape.getWidth();
        this.height = shape.getHeight();
        this.pieces = new Piece[width * height];
    }

    /**
     * Instantiates a board with the same shape and pieces as {@code template}.
     * @param template Another board to use as a template to copy from.
     */
    protected Board(@Nonnull Board<P> template) {
        this(template.shape);
        System.arraycopy(template.pieces, 0, pieces, 0, pieces.length);
    }

    /**
     * Creates an exact copy of this board.
     * @return An exact copy of this board.
     */
    public @Nonnull Board<P> copy() {
        return new Board<>(this);
    }

    private int calcTileIndex(int ix, int iy) {
        if (ix < 0 || iy < 0 || ix >= this.width || iy >= this.height) {
            throw new IllegalArgumentException(
                    "There is no tile at the indices (" + ix + ", " + iy + ")"
            );
        }

        return iy * this.width + ix;
    }

    private int calcTileIndex(Tile tile) {
        return calcTileIndex(tile.getXIndex(), tile.getYIndex());
    }

    /**
     * Gets the shape of this board.
     * @return The shape of this board.
     */
    public @Nonnull BoardShape getShape() {
        return shape;
    }

    /**
     * Gets the width of the board, which represents
     * the number of x-coordinates in this board.
     * @return The number of x-coordinates that exist in this board.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the board, which represents
     * the number of y-coordinates in this board.
     * @return The number of y-coordinates that exist in this board.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Determines whether {@code tile} falls within the bounds of this board.
     * @param tile The tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(@Nonnull Tile tile) {
        return shape.contains(tile);
    }

    /**
     * Determines whether the tile at the indices ({@code ix}, {@code iy}),
     * 0-based, falls within the bounds of this board.
     * @param ix The x-index of the tile to be bounds-checked.
     *           This coordinate is 0-based.
     * @param iy The y-index of the tile to be bounds-checked.
     *           This coordinate is 0-based.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean containsIndices(int ix, int iy) {
        return shape.containsIndices(ix, iy);
    }

    /**
     * Retrieves the piece on {@code tile}. Returns {@code null} if there
     * is no piece on the tile.
     * @param tile The tile to find the piece on.
     * @return The piece on the given tile if one exists, or else {@code null}.
     */
    public @Nullable P get(@Nonnull Tile tile) {
        return getByIndices(tile.getXIndex(), tile.getYIndex());
    }

    /**
     * Retrieves the piece on the tile at the indices ({@code ix}, {@code iy}),
     * 0-based. Returns {@code null} if there is no piece on the tile.
     * @param ix The x-index of the tile to find the piece on.
     *           This coordinate is 0-based.
     * @param iy The y-index of the tile to find the piece on.
     *           This coordinate is 0-based.
     * @return The piece on the given tile if one exists, or else {@code null}.
     */
    public @Nullable P getByIndices(int ix, int iy) {
        if (!containsIndices(ix, iy)) {
            throw new IllegalArgumentException(
                    "There is no tile at the indices (" + ix + ", " + iy + ")"
            );
        }
        int index = calcTileIndex(ix, iy);
        return Cast.unsafeCast(pieces[index]);
    }

    /**
     * Sets the piece on {@code tile} to {@code piece}. If {@code piece}
     * is {@code null}, it removes any piece on the tile. Returns the piece
     * that was previously on the tile, or {@code null} if there was no
     * piece on the tile.
     * @param tile The tile to find the piece on.
     * @param piece The piece that should be placed on the tile.
     * @return The previous piece on the given tile if there was one,
     *         or else {@code null}.
     */
    public @Nullable P set(@Nonnull Tile tile, @Nullable P piece) {
        return setByIndices(tile.getXIndex(), tile.getYIndex(), piece);
    }

    /**
     * Sets the piece on the tile at the indices ({@code ix}, {@code iy}),
     * 0-based, to the piece {@code piece}. If {@code piece} is {@code null},
     * it removes any piece on the tile. Returns the piece that was previously
     * on the tile, or {@code null} if there was no piece on the tile.
     * @param ix The x-index of the tile to place the piece on.
     *           This coordinate is 0-based.
     * @param iy The y-index of the tile to place the piece on.
     *           This coordinate is 0-based.
     * @param piece The piece that should be placed on the tile.
     * @return The previous piece on the given tile if there was one,
     *         or else {@code null}.
     */
    public @Nullable P setByIndices(int ix, int iy, @Nullable P piece) {
        if (!containsIndices(ix, iy)) {
            throw new IllegalArgumentException(
                    "There is no tile at indices (" + ix + ", " + iy + ")"
            );
        }

        int index = calcTileIndex(ix, iy);
        P previous = Cast.unsafeCast(pieces[index]);
        pieces[index] = piece;
        return previous;
    }

    /**
     * Removes all pieces from this board.
     */
    public void clear() {
        Arrays.fill(this.pieces, null);
    }

    /**
     * Counts the number of pieces that are on the board for {@code player}.
     * @param player The player to count the pieces of.
     * @return The number of pieces on the board for the given player.
     */
    public int countPieces(@Nonnull PlayerType player) {
        int totalPieces = 0;
        for (int iy = 0; iy < height; ++iy) {
            for (int ix = 0; ix < width; ++ix) {
                if (!containsIndices(ix, iy))
                    continue;

                Piece piece = getByIndices(ix, iy);
                if (piece != null && piece.getOwner() == player) {
                    totalPieces += 1;
                }
            }
        }
        return totalPieces;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Board<?> other = (Board<?>) obj;
        if (!shape.equals(other.shape))
            return false;

        for (int iy = 0; iy < height; ++iy) {
            for (int ix = 0; ix < width; ++ix) {
                if (!containsIndices(ix, iy))
                    continue;

                Piece thisPiece = getByIndices(ix, iy);
                Piece otherPiece = other.getByIndices(ix, iy);
                if (!Objects.equals(thisPiece, otherPiece))
                    return false;
            }
        }
        return true;
    }

    /**
     * Writes the contents of this board into a String, where each column is placed on new line.
     * @param columnDelimiter The delimiter to use to distinguish columns of the board.
     * @param includeOffBoardTiles Whether to include tiles that don't fall on the board as spaces.
     * @return A String representing the contents of this board.
     */
    public @Nonnull String toString(char columnDelimiter, boolean includeOffBoardTiles) {
        StringBuilder builder = new StringBuilder();
        for (int ix = 0; ix < shape.getWidth(); ++ix) {
            if (ix > 0) {
                builder.append(columnDelimiter);
            }

            for (int iy = 0; iy < shape.getHeight(); ++iy) {
                if (containsIndices(ix, iy)) {
                    builder.append(Piece.toChar(getByIndices(ix, iy)));
                } else if (includeOffBoardTiles) {
                    builder.append(' ');
                }
            }
        }
        return builder.toString();
    }

    @Override
    public @Nonnull String toString() {
        return toString('\n', true);
    }

    @Override
    public @Nonnull Iterator<P> iterator() {
        return new BoardIterator();
    }

    private class BoardIterator implements Iterator<P> {

        private int index;

        public BoardIterator() {
            this.index = 0;
        }

        private void moveToNext() {
            if (index >= pieces.length)
                throw new IllegalStateException("");

            index += 1;
        }

        @Override
        public boolean hasNext() {
            if (index >= pieces.length)
                return false;

            while (pieces[index] == null) {
                moveToNext();
                if (index >= pieces.length)
                    return false;
            }
            return true;
        }

        @Override
        public P next() {
            if (!hasNext())
                throw new NoSuchElementException();

            P piece = Cast.unsafeCast(pieces[index]);
            moveToNext();
            return piece;
        }
    }
}
