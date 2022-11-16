package net.royalur.model.boardshape;

import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * A type of board shape available for the Royal Game of Ur.
 */
public abstract class BoardShape {

    /**
     * The name of this shape of board.
     */
    public final @Nonnull String name;

    /**
     * The set of tiles that fall within the bounds of this board shape.
     */
    public final @Nonnull Set<Tile> boardTiles;

    /**
     * The number of x-coordinates that exist in this board shape.
     */
    public final int width;

    /**
     * The number of y-coordinates that exist in this board shape.
     */
    public final int height;

    /**
     * The number of tiles contained in this board shape.
     */
    public final int area;

    /**
     * @param name A name for this shape of board.
     * @param boardTiles The set of tiles that fall within the bounds of this board shape.
     */
    public BoardShape(@Nonnull String name, @Nonnull Set<Tile> boardTiles) {
        if (boardTiles.size() == 0)
            throw new IllegalArgumentException("A board shape requires at least one tile");

        this.name = name;
        this.boardTiles = boardTiles;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Tile tile : boardTiles) {
            minX = Math.min(minX, tile.x);
            minY = Math.min(minY, tile.y);
            maxX = Math.max(maxX, tile.x);
            maxY = Math.max(maxY, tile.y);
        }
        if (minX != 0 || minY != 0) {
            // This is done in an attempt to standardise board shapes.
            throw new IllegalArgumentException(
                    "The board shape must be translated such that it has tiles " +
                    "at an x-coordinate of 0, and at a y-coordinate of 0. " +
                    "Minimum X = " + minX + ", Minimum Y = " + minY
            );
        }

        this.width = maxX + 1;
        this.height = maxY + 1;
        this.area = boardTiles.size();
    }

    /**
     * Determines whether {@param tile} falls within the bounds of this shape of board.
     * @param tile The tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(@Nonnull Tile tile) {
        return boardTiles.contains(tile);
    }

    /**
     * Determines whether the tile at the coordinates ({@param x}, {@param y})
     * falls within the bounds of this shape of board.
     * @param x The x-coordinate of the tile to be bounds-checked.
     * @param y The y-coordinate of the tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this shape of board.
     */
    public boolean contains(int x, int y) {
        if (!Tile.isValid(x, y))
            return false;
        return contains(new Tile(x, y));
    }

    @Override
    public int hashCode() {
        return boardTiles.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BoardShape))
            return false;

        BoardShape other = (BoardShape) obj;
        return boardTiles.equals(other.boardTiles);
    }

    @Override
    public @Nonnull String toString() {
        return name;
    }
}
