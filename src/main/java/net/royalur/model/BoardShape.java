package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A type of board shape available for the Royal Game of Ur.
 */
public class BoardShape {

    /**
     * The name of this shape of board.
     */
    public final @Nonnull String name;

    /**
     * The set of tiles that fall within the bounds of this board shape.
     */
    public final @Nonnull Set<Tile> tiles;

    /**
     * The set of tiles that represent rosette tiles in this board shape.
     */
    public final @Nonnull Set<Tile> rosetteTiles;

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
     * The tiles that fall within the bounds of this board shape,
     * ordered by ascending row number and then ascending column number.
     */
    private @Nullable List<Tile> tilesByRow = null;

    /**
     * The tiles that fall within the bounds of this board shape,
     * ordered into columns with ascending row number.
     */
    private @Nullable List<Tile> tilesByColumn = null;

    /**
     * @param name         A name for this shape of board.
     * @param tiles        The set of tiles that fall within the bounds of this board shape.
     * @param rosetteTiles The set of tiles that represent rosette tiles in this board shape.
     */
    public BoardShape(@Nonnull String name, @Nonnull Set<Tile> tiles, @Nonnull Set<Tile> rosetteTiles) {
        if (tiles.size() == 0)
            throw new IllegalArgumentException("A board shape requires at least one tile");

        this.name = name;
        this.tiles = tiles;
        this.rosetteTiles = rosetteTiles;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Tile tile : tiles) {
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
        this.area = tiles.size();

        for (Tile tile : rosetteTiles) {
            if (!contains(tile)) {
                throw new IllegalArgumentException(
                        "rosetteTiles should not include any tiles that are off of the game board," +
                                "but it contains " + tile
                );
            }
        }
    }

    /**
     * The tiles that fall within the bounds of this board shape,
     * ordered by ascending row number and then ascending column number.
     */
    public final @Nonnull List<Tile> getTilesByRow() {
        if (this.tilesByRow == null) {
            List<Tile> tilesByRow = new ArrayList<>();
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    Tile tile = new Tile(x, y);
                    if (contains(tile)) {
                        tilesByRow.add(tile);
                    }
                }
            }
            this.tilesByRow = Collections.unmodifiableList(tilesByRow);
        }
        return this.tilesByRow;
    }

    /**
     * The tiles that fall within the bounds of this board shape,
     * ordered into columns with ascending row number.
     */
    public final @Nonnull List<Tile> getTilesByColumn() {
        if (this.tilesByColumn == null) {
            List<Tile> tilesByColumn = new ArrayList<>();
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    Tile tile = new Tile(x, y);
                    if (contains(tile)) {
                        tilesByColumn.add(tile);
                    }
                }
            }
            this.tilesByColumn = Collections.unmodifiableList(tilesByColumn);
        }
        return this.tilesByColumn;
    }

    /**
     * Determines whether {@param tile} falls within the bounds of this shape of board.
     * @param tile The tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(@Nonnull Tile tile) {
        return tiles.contains(tile);
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

    /**
     * Determines whether {@param tile} is a rosette tile on this board.
     * @param tile The tile to check if it is a rosette.
     * @return Whether the given tile is a rosette tile on this board.
     */
    public boolean isRosette(@Nonnull Tile tile) {
        return rosetteTiles.contains(tile);
    }

    /**
     * Determines whether the tile at the coordinates ({@param x}, {@param y})
     * is a rosette tile on this board.
     * @param x The x-coordinate of the tile to be checked for being a rosette.
     * @param y The y-coordinate of the tile to be checked for being a rosette.
     * @return Whether the given tile is a rosette tile on this board.
     */
    public boolean isRosette(int x, int y) {
        if (!Tile.isValid(x, y))
            return false;
        return isRosette(new Tile(x, y));
    }

    /**
     * Determines whether {@param path} could be traversed on this shape of board.
     * @param path The path to check for compatibility.
     * @return Whether {@param path} could be traversed on this shape of board.
     */
    public boolean isCompatible(@Nonnull Path path) {
        for (Tile tile : path.tiles) {
            if (!contains(tile))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ (31 * tiles.hashCode()) ^ (97 * rosetteTiles.hashCode());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof BoardShape))
            return false;

        BoardShape other = (BoardShape) obj;
        return name.equals(other.name) && tiles.equals(other.tiles) && rosetteTiles.equals(other.rosetteTiles);
    }

    @Override
    public @Nonnull String toString() {
        return name;
    }
}
