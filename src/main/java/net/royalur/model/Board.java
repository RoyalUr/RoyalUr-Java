package net.royalur.model;

import net.royalur.model.shape.BoardShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Stores the placement of pieces on the tiles of a Royal Game of Ur board.
 * @param <P> The type of pieces that may be placed on this board.
 */
public class Board<P extends Piece> {

    /**
     * The shape of this board.
     */
    public final @Nonnull BoardShape shape;

    /**
     * The number of x-coordinates that exist in this board.
     */
    public final int width;

    /**
     * The number of y-coordinates that exist in this board.
     */
    public final int height;

    /**
     * The number of tiles contained in this board.
     */
    public final int area;

    /**
     * The pieces on the tiles of this board.
     */
    private final @Nonnull Piece[][] pieces;

    /**
     * Instantiates an empty board with the shape {@code shape}.
     * @param shape The shape of this board.
     */
    public Board(@Nonnull BoardShape shape) {
        this.shape = shape;
        this.width = shape.width;
        this.height = shape.height;
        this.area = shape.area;
        this.pieces = new Piece[width][height];
    }

    /**
     * Instantiates a board with the same shape and pieces as {@code template}.
     * @param template Another board to use as a template to copy from.
     */
    protected Board(@Nonnull Board<P> template) {
        this(template.shape);
        for (int ix = 0; ix < shape.width; ++ix) {
            System.arraycopy(template.pieces[ix], 0, pieces[ix], 0, shape.height);
        }
    }

    /**
     * Creates an exact copy of this board.
     * @return An exact copy of this board.
     */
    public @Nonnull Board<P> copy() {
        return new Board<>(this);
    }

    /**
     * Determines whether {@code tile} falls within the bounds of this board.
     *
     * @param tile The tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(@Nonnull Tile tile) {
        return shape.contains(tile);
    }

    /**
     * Determines whether the tile at the indices ({@code ix}, {@code iy}),
     * 0-based, falls within the bounds of this board.
     *
     * @param ix The x-index of the tile to be bounds-checked. This coordinate is 0-based.
     * @param iy The y-index of the tile to be bounds-checked. This coordinate is 0-based.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(int ix, int iy) {
        return shape.contains(ix, iy);
    }

    /**
     * Retrieves the piece on {@code tile}. Returns {@code null} if there is no piece on the tile.
     *
     * @param tile The tile to find the piece on.
     * @return The piece on the given tile if one exists, or else {@code null}.
     */
    public @Nullable P get(@Nonnull Tile tile) {
        return get(tile.ix, tile.iy);
    }

    /**
     * Retrieves the piece on the tile at the indices ({@code ix}, {@code iy}), 0-based.
     * Returns {@code null} if there is no piece on the tile.
     *
     * @param ix The x-index of the tile to find the piece on. This coordinate is 0-based.
     * @param iy The y-index of the tile to find the piece on. This coordinate is 0-based.
     * @return The piece on the given tile if one exists, or else {@code null}.
     */
    @SuppressWarnings("unchecked")
    public @Nullable P get(int ix, int iy) {
        if (!contains(ix, iy))
            throw new IllegalArgumentException("There is no tile at the 0-based indices (" + ix + ", " + iy + ")");

        return (P) pieces[ix][iy];
    }

    /**
     * Sets the piece on {@code tile} to {@code piece}. If
     * {@code piece} is {@code null}, it removes any piece on the tile.
     * Returns the piece that was previously on the tile, or
     * {@code null} if there was no piece on the tile.
     *
     * @param tile The tile to find the piece on.
     * @param piece The piece that should be placed on the tile.
     * @return The previous piece on the given tile if there was one, or else {@code null}.
     */
    public @Nullable P set(@Nonnull Tile tile, @Nullable P piece) {
        return set(tile.ix, tile.iy, piece);
    }

    /**
     * Sets the piece on the tile at the indices ({@code ix}, {@code iy}), 0-based,
     * to the piece {@code piece}. If {@code piece} is {@code null}, it
     * removes any piece on the tile. Returns the piece that was previously
     * on the tile, or {@code null} if there was no piece on the tile.
     *
     * @param ix The x-index of the tile to place the piece on. This coordinate is 0-based.
     * @param iy The y-index of the tile to place the piece on. This coordinate is 0-based.
     * @param piece The piece that should be placed on the tile.
     * @return The previous piece on the given tile if there was one, or else {@code null}.
     */
    @SuppressWarnings("unchecked")
    public @Nullable P set(int ix, int iy, @Nullable P piece) {
        if (!contains(ix, iy))
            throw new IllegalArgumentException("There is no tile at the 0-based indices (" + ix + ", " + iy + ")");

        P previous = (P) pieces[ix][iy];
        pieces[ix][iy] = piece;
        return previous;
    }

    /**
     * Counts the number of pieces that are on the board for {@code player}.
     * @param player The player to count the pieces of.
     * @return The number of pieces on the board for the given player.
     */
    public int countPieces(@Nonnull Player player) {
        int totalPieces = 0;
        for (int ix = 0; ix < width; ++ix) {
            for (int iy = 0; iy < height; ++iy) {
                if (!contains(ix, iy))
                    continue;

                Piece piece = get(ix, iy);
                if (piece != null && piece.owner == player) {
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
        if (width != other.width || height != other.height || !shape.equals(other.shape))
            return false;

        for (int ix = 0; ix < width; ++ix) {
            for (int iy = 0; iy < height; ++iy) {
                if (!contains(ix, iy))
                    continue;

                if (!Objects.equals(get(ix, iy), other.get(ix, iy)))
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
        for (int ix = 0; ix < shape.width; ++ix) {
            if (ix > 0) {
                builder.append(columnDelimiter);
            }

            for (int iy = 0; iy < shape.height; ++iy) {
                if (contains(ix, iy)) {
                    builder.append(Piece.toChar(get(ix, iy)));
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
}
