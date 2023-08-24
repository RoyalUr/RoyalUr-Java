package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * A tile represents location on a Royal Game of Ur board.
 */
public class Tile {

    /**
     * The x-coordinate of the tile. This coordinate is 1-based.
     */
    private final int x;

    /**
     * The y-coordinate of the tile. This coordinate is 1-based.
     */
    private final int y;

    /**
     * The x-index of the tile. This coordinate is 0-based.
     */
    private final int ix;

    /**
     * The y-index of the tile. This coordinate is 0-based.
     */
    private final int iy;

    /**
     * Instantiates a tile location.
     * @param x The x-coordinate of the tile. This coordinate starts at 1.
     * @param y The y-coordinate of the tile. This coordinate starts at 1.
     */
    public Tile(int x, int y) {
        if (x < 1 || x > 26)
            throw new IllegalArgumentException("x must fall within the range [1, 26]. Invalid value: " + x);
        if (y < 0)
            throw new IllegalArgumentException("y must not be negative. Invalid value: " + y);

        this.x = x;
        this.y = y;

        this.ix = x - 1;
        this.iy = y - 1;
    }

    /**
     * Gets the x-coordinate of the tile. This coordinate is 1-based.
     * @return The x-coordinate of the tile.
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the x-index of the tile. This coordinate is 0-based.
     * @return The x-index of the tile.
     */
    public int getXIndex() {
        return ix;
    }

    /**
     * Gets the y-coordinate of the tile. This coordinate is 1-based.
     * @return The y-coordinate of the tile.
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the y-index of the tile. This coordinate is 0-based.
     * @return The y-index of the tile.
     */
    public int getYIndex() {
        return iy;
    }

    /**
     * Creates a new tile representing the tile at the
     * indices ({@code ix}, {@code iy}), 0-based.
     * @param ix The x-index of the tile. This coordinate is 0-based.
     * @param iy The y-index of the tile. This coordinate is 0-based.
     * @return A tile representing the tile at indices ({@code ix}, {@code iy}).
     */
    public static @Nonnull Tile fromIndices(int ix, int iy) {
        return new Tile(ix + 1, iy + 1);
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) ^ (97 * Integer.hashCode(y));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Tile other))
            return false;

        return x == other.x && y == other.y;
    }

    /**
     * Takes a unit length step towards the other tile.
     * @param other The other vector to step towards.
     * @return A new vector that is one step closer to other.
     */
    public Tile stepTowards(Tile other) {
        int dx = other.x - x;
        int dy = other.y - y;
        if (Math.abs(dx) + Math.abs(dy) <= 1)
            return other;

        if (Math.abs(dx) < Math.abs(dy)) {
            return new Tile(x, y + (dy > 0 ? 1 : -1));
        } else {
            return new Tile(x + (dx > 0 ? 1 : -1), y);
        }
    }

    /**
     * Encodes the x-coordinate as an upper-case letter, and appends it to {@code builder}.
     * @param builder The builder to place the encoded x-coordinate into.
     */
    public void encodeX(@Nonnull StringBuilder builder) {
        builder.append((char) ('A' + (x - 1)));
    }

    /**
     * Encodes the x-coordinate as a lower-case letter, and appends it to {@code builder}.
     * @param builder The builder to place the encoded x-coordinate into.
     */
    public void encodeXLowerCase(@Nonnull StringBuilder builder) {
        builder.append((char) ('a' + (x - 1)));
    }

    /**
     * Encodes the y-coordinate as a number, and appends it to {@code builder}.
     * @param builder The builder to place the encoded y-coordinate into.
     */
    public void encodeY(@Nonnull StringBuilder builder) {
        builder.append(y);
    }

    /**
     * Converts the location of this tile into a text representation of the
     * format "[letter][number]".
     * @return A text representation of the location of this tile.
     */
    @Override
    public @Nonnull String toString() {
        StringBuilder builder = new StringBuilder();
        encodeX(builder);
        encodeY(builder);
        return builder.toString();
    }

    /**
     * Converts text representations of the tile of the format "[letter][number]".
     * For example:
     *  - A1 represents (1, 1)
     *  - C3 represents (3, 3)
     *  - B8 represents (2, 8)
     *  - B0 represents (2, 0)
     *
     * @param tile The text representation of the tile's location.
     * @return The tile that the given text is representing.
     */
    public static @Nonnull Tile fromString(@Nonnull String tile) {
        if (tile.length() < 2)
            throw new IllegalArgumentException("Expected a letter followed by a number");

        char letter = tile.charAt(0);
        int x;
        if (letter >= 'a' && letter <= 'z') {
            x = letter - 'a' + 1;
        } else if (letter >= 'A' && letter <= 'Z') {
            x = letter - 'A' + 1;
        } else {
            throw new IllegalArgumentException("Illegal letter representing the x-coordinate: " + letter);
        }

        int y = Integer.parseInt(tile.substring(1));
        return new Tile(x, y);
    }

    /**
     * Constructs a list of tiles from the tile coordinates.
     * @param coordinates The tile coordinates. Must be an even list
     *                    ordered following x0, y0, x1, y1, x2, y2, etc.
     * @return A list of tiles.
     */
    public static @Nonnull List<Tile> createList(int... coordinates) {
        if (coordinates.length == 0)
            throw new IllegalArgumentException("No coordinates provided");
        if (coordinates.length % 2 != 0)
            throw new IllegalArgumentException("Expected an even number of coordinates");

        List<Tile> tiles = new ArrayList<>();

        for (int index = 0; index < coordinates.length; index += 2) {
            int x = coordinates[index];
            int y = coordinates[index + 1];
            tiles.add(new Tile(x, y));
        }
        return tiles;
    }

    /**
     * Constructs a path from waypoints on the board.
     * @param coordinates The waypoint coordinates. Must be an even list
     *                    ordered following x0, y0, x1, y1, x2, y2, etc.
     * @return A list of tiles following the path between the given waypoints.
     */
    public static @Nonnull List<Tile> createPath(int... coordinates) {
        List<Tile> tiles = createList(coordinates);
        List<Tile> path = new ArrayList<>();
        path.add(tiles.get(0));

        for (int index = 1; index < tiles.size(); ++index) {
            Tile current = tiles.get(index - 1);
            Tile next = tiles.get(index);
            while (!current.equals(next)) {
                current = current.stepTowards(next);
                path.add(current);
            }
        }
        return path;
    }

    /**
     * Calculates the union of all given tile lists.
     * @param tileLists The lists of tiles.
     * @return A set of all tiles.
     */
    @SafeVarargs
    public static @Nonnull Set<Tile> unionLists(Collection<Tile>... tileLists) {
        Set<Tile> tiles = new HashSet<>();
        for (Collection<Tile> tileList : tileLists) {
            tiles.addAll(tileList);
        }
        return tiles;
    }
}
