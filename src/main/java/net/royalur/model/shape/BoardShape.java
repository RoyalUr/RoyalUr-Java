package net.royalur.model.shape;

import net.royalur.model.Tile;
import net.royalur.model.path.PathPair;

import javax.annotation.Nullable;
import java.util.*;

/**
 * A type of board shape available for the Royal Game of Ur.
 */
public class BoardShape {

    /**
     * The ID of this board shape.
     */
    private final String id;

    /**
     * The set of tiles that fall within the bounds of this board shape.
     */
    private final Set<Tile> tiles;

    /**
     * The set of tiles that represent rosette tiles in this board shape.
     */
    private final Set<Tile> rosettes;

    /**
     * The number of x-coordinates that exist in this board shape.
     */
    private final int width;

    /**
     * The number of y-coordinates that exist in this board shape.
     */
    private final int height;

    /**
     * Instantiates a board shape with {@code tiles} representing the tiles
     * on the board.
     * @param id       The ID of this board shape.
     * @param tiles    The set of tiles that fall within this board shape.
     * @param rosettes The set of tiles that represent rosettes in this board shape.
     */
    public BoardShape(
            String id,
            Set<Tile> tiles,
            Set<Tile> rosettes
    ) {
        if (tiles.isEmpty())
            throw new IllegalArgumentException("A board shape requires at least one tile");

        this.id = id;
        this.tiles = Set.copyOf(tiles);
        this.rosettes = Set.copyOf(rosettes);

        for (Tile rosette : rosettes) {
            if (!contains(rosette))
                throw new IllegalArgumentException("Rosette " + rosette + " does not exist on the board");
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Tile tile : tiles) {
            minX = Math.min(minX, tile.getX());
            minY = Math.min(minY, tile.getY());
            maxX = Math.max(maxX, tile.getX());
            maxY = Math.max(maxY, tile.getY());
        }
        if (minX != 1 || minY != 1) {
            // This is done in an attempt to standardise board shapes.
            throw new IllegalArgumentException(
                    "The board shape must be translated such that it has tiles "
                            + "at an x-coordinate of 1, and at a y-coordinate of 1. "
                            + "Minimum X = " + minX + ", Minimum Y = " + minY
            );
        }
        this.width = maxX;
        this.height = maxY;
    }

    /**
     * Gets the ID of this board shape.
     * @return The ID of this board shape.
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the set of tiles that fall within the bounds of this board shape.
     * @return The set of tiles that fall within the bounds of this board shape.
     */
    public Set<Tile> getTiles() {
        return tiles;
    }

    /**
     * Gets the set of tiles that represent rosette tiles in this board shape.
     * @return The set of tiles that represent rosette tiles in this board shape.
     */
    public Set<Tile> getRosetteTiles() {
        return rosettes;
    }

    /**
     * Gets the width of the board shape.
     * @return The number of x-coordinates that exist in this board shape.
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the board shape.
     * @return The number of y-coordinates that exist in this board shape.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets the number of tiles contained in this board shape.
     * @return The number of tiles contained in this board shape.
     */
    public int getArea() {
        return tiles.size();
    }

    /**
     * Returns the tiles that fall within the bounds of this board shape,
     * ordered by ascending row number, and then ascending column number.
     * @return The tiles of this board ordered by ascending row, and then
     *         ascending column number.
     */
    public final List<Tile> getTilesByRow() {
        List<Tile> tilesByRow = new ArrayList<>();
        for (int iy = 0; iy < height; ++iy) {
            for (int ix = 0; ix < width; ++ix) {
                Tile tile = Tile.fromIndices(ix, iy);
                if (contains(tile)) {
                    tilesByRow.add(tile);
                }
            }
        }
        return tilesByRow;
    }

    /**
     * The tiles that fall within the bounds of this board shape,
     * ordered by ascending column number, and then ascending row number.
     * @return The tiles of this board ordered by ascending column, and then
     *         ascending row.
     */
    public final List<Tile> getTilesByColumn() {
        List<Tile> tilesByColumn = new ArrayList<>();
        for (int ix = 0; ix < width; ++ix) {
            for (int iy = 0; iy < height; ++iy) {
                Tile tile = Tile.fromIndices(ix, iy);
                if (contains(tile)) {
                    tilesByColumn.add(tile);
                }
            }
        }
        return tilesByColumn;
    }

    /**
     * Determines whether {@code tile} falls within this board shape.
     * @param tile The tile to be bounds-checked.
     * @return Whether the given tile falls within this board shape.
     */
    public boolean contains(Tile tile) {
        return tiles.contains(tile);
    }

    /**
     * Determines whether the tile at indices ({@code ix}, {@code iy}),
     * 0-based, falls within the bounds of this shape of board.
     * @param ix The x-index of the tile to be bounds-checked.
     *           This coordinate is 0-based.
     * @param iy The y-index of the tile to be bounds-checked.
     *           This coordinate is 0-based.
     * @return Whether the given tile falls within the bounds of this
     *         shape of board.
     */
    public boolean containsIndices(int ix, int iy) {
        if (ix < 0 || iy < 0 || ix >= width || iy >= height)
            return false;
        return contains(Tile.fromIndices(ix, iy));
    }

    /**
     * Determines whether all tiles in {@code tiles} are included
     * in this board shape.
     * @param tiles The tiles to check for.
     * @return Whether all of {@code tiles} exist on this board shape.
     */
    public boolean containsAll(Collection<Tile> tiles) {
        for (Tile tile : tiles) {
            if (!contains(tile))
                return false;
        }
        return true;
    }

    /**
     * Determines whether {@code tile} is a rosette tile in this board shape.
     * @param tile The tile to check if it is a rosette.
     * @return Whether the given tile is a rosette tile on this board.
     */
    public boolean isRosette(Tile tile) {
        return rosettes.contains(tile);
    }

    /**
     * Determines whether the tile at the indices ({@code ix}, {@code iy}),
     * 0-based, is a rosette tile in this board shape.
     * @param ix The x-index of the tile to be checked for being a rosette.
     *           This coordinate is 0-based.
     * @param iy The y-index of the tile to be checked for being a rosette.
     *           This coordinate is 0-based.
     * @return Whether the given tile is a rosette tile on this board.
     */
    public boolean isRosetteIndices(int ix, int iy) {
        if (ix < 0 || iy < 0 || ix >= width || iy >= height)
            return false;
        return isRosette(Tile.fromIndices(ix, iy));
    }

    /**
     Determines whether {@code paths} can be used on this shape of board.
     * @param paths The pair of paths.
     * @return Whether the pair of paths could be used on this shape
     *         of board.
     */
    public boolean isCompatible(PathPair paths) {
        return this.containsAll(paths.getLight()) && this.containsAll(paths.getDark());
    }

    /**
     * Determines whether this board shape covers the same tiles,
     * and has the same rosettes, as {@code other}.
     * @param other The board shape to compare with for equivalence.
     * @return Whether this board shape is equivalent to {@param other}.
     */
    public boolean isEquivalent(BoardShape other) {
        return tiles.equals(other.tiles) && rosettes.equals(other.rosettes);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        BoardShape other = (BoardShape) obj;
        return id.equals(other.id) && isEquivalent(other);
    }
}
