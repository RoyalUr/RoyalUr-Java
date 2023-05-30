package net.royalur.model.shape;

import net.royalur.model.Tile;
import net.royalur.model.path.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A type of board shape available for the Royal Game of Ur.
 */
public class BoardShape {

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
     * Instantiates a board shape with {@code tiles} representing the tiles on the board.
     * @param tiles        The set of tiles that fall within the bounds of this board shape.
     * @param rosetteTiles The set of tiles that represent rosette tiles in this board shape.
     */
    public BoardShape(@Nonnull Set<Tile> tiles, @Nonnull Set<Tile> rosetteTiles) {
        if (tiles.size() == 0)
            throw new IllegalArgumentException("A board shape requires at least one tile");

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
        if (minX != 1 || minY != 1) {
            // This is done in an attempt to standardise board shapes.
            throw new IllegalArgumentException(
                    "The board shape must be translated such that it has tiles " +
                    "at an x-coordinate of 1, and at a y-coordinate of 1. " +
                    "Minimum X = " + minX + ", Minimum Y = " + minY
            );
        }

        this.width = maxX;
        this.height = maxY;
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
     * Gets a name to be used for identifying this board shape in debugging.
     * @return A name to be used for identifying this board shape in debugging.
     */
    public @Nonnull String getDebugName() {
        return getClass().getName();
    }

    /**
     * Returns the tiles that fall within the bounds of this board shape,
     * ordered by ascending row number, and then ascending column number.
     * @return The tiles of this board ordered by ascending row, and then
     *         ascending column number.
     */
    public final @Nonnull List<Tile> getTilesByRow() {
        if (this.tilesByRow == null) {
            List<Tile> tilesByRow = new ArrayList<>();
            for (int iy = 0; iy < height; ++iy) {
                for (int ix = 0; ix < width; ++ix) {
                    Tile tile = Tile.fromIndices(ix, iy);
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
     * ordered by ascending column number, and then ascending row number.
     * @return The tiles of this board ordered by ascending column, and then
     *         ascending row.
     */
    public final @Nonnull List<Tile> getTilesByColumn() {
        if (this.tilesByColumn == null) {
            List<Tile> tilesByColumn = new ArrayList<>();
            for (int ix = 0; ix < width; ++ix) {
                for (int iy = 0; iy < height; ++iy) {
                    Tile tile = Tile.fromIndices(ix, iy);
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
     * Determines whether {@code tile} falls within the bounds of this shape of board.
     * @param tile The tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(@Nonnull Tile tile) {
        return tiles.contains(tile);
    }

    /**
     * Determines whether the tile at the indices ({@code x}, {@code y}),
     * 0-based, falls within the bounds of this shape of board.
     * @param ix The x-index of the tile to be bounds-checked. This coordinate is 0-based.
     * @param iy The y-index of the tile to be bounds-checked. This coordinate is 0-based.
     * @return Whether the given tile falls within the bounds of this shape of board.
     */
    public boolean contains(int ix, int iy) {
        if (!Tile.isValidIndices(ix, iy))
            return false;
        return contains(Tile.fromIndices(ix, iy));
    }

    /**
     * Determines whether {@code tile} is a rosette tile on this board.
     * @param tile The tile to check if it is a rosette.
     * @return Whether the given tile is a rosette tile on this board.
     */
    public boolean isRosette(@Nonnull Tile tile) {
        return rosetteTiles.contains(tile);
    }

    /**
     * Determines whether the tile at the indices ({@code ix}, {@code iy}),
     * 0-based, is a rosette tile on this board.
     * @param ix The x-index of the tile to be checked for being a rosette.
     *           This coordinate is 0-based.
     * @param iy The y-index of the tile to be checked for being a rosette.
     *           This coordinate is 0-based.
     * @return Whether the given tile is a rosette tile on this board.
     */
    public boolean isRosette(int ix, int iy) {
        if (!Tile.isValidIndices(ix, iy))
            return false;
        return isRosette(Tile.fromIndices(ix, iy));
    }

    /**
     * Determines whether {@code path} could be traversed on this shape of board.
     * @param path The path to check for compatibility.
     * @return Whether {@code path} could be traversed on this shape of board.
     */
    public boolean isCompatible(@Nonnull Path path) {
        for (Tile tile : path.tiles) {
            if (!contains(tile))
                return false;
        }
        return true;
    }

    /**
     * Determines whether this board shape covers the same tiles,
     * and has the same rosettes, as {@code other}.
     * @param other The board shape to compare with for equivalence.
     * @return Whether this board shape is equivalent to {@param other}.
     */
    public boolean isEquivalent(@Nonnull BoardShape other) {
        return tiles.equals(other.tiles) && rosetteTiles.equals(other.rosetteTiles);
    }
}
