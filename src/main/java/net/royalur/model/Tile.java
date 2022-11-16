package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A tile represents a location on a Royal Game of Ur board.
 */
public class Tile {

    /**
     * The x-coordinate of the tile. This coordinate is 0-based.
     */
    public final int x;

    /**
     * The y-coordinate of the tile. This coordinate is 0-based.
     */
    public final int y;

    /**
     * @param x The x-coordinate of the tile. This coordinate is 0-based.
     * @param y The y-coordinate of the tile. This coordinate is 0-based.
     */
    public Tile(int x, int y) {
        if (x < 0 || x >= 26)
            throw new IllegalArgumentException("x must fall within the range [0, 25]. Invalid value: " + x);
        if (y < 0)
            throw new IllegalArgumentException("y must be at least zero. Invalid value: " + y);

        this.x = x;
        this.y = y;
    }

    /**
     * Determines whether the coordinates ({@param x}, {@param y}) could
     * possibly represent a tile on a board.
     *
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return Whether the tile at the given coordinates could possibly represent a tile on a board.
     */
    public static boolean isValid(int x, int y) {
        return x >= 0 && x < 26 && y >= 0;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(x) ^ (97 * Integer.hashCode(y));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Tile))
            return false;

        Tile other = (Tile) obj;
        return x == other.x && y == other.y;
    }

    /**
     * Converts the location of this tile into a text representation of the
     * format "[letter][number]".
     *
     * @return A text representation of the location of this tile.
     */
    @Override
    public @Nonnull String toString() {
        return Character.toString((char) ('A' + x)) + y;
    }

    /**
     * Converts text representations of the tile of the format "[letter][number]".
     * For example:
     *  - A0 represents (0, 0)
     *  - C2 represents (2, 2)
     *  - B7 represents (1, 7)
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
            x = letter - 'a';
        } else if (letter >= 'A' && letter <= 'Z') {
            x = letter - 'A';
        } else {
            throw new IllegalArgumentException("Illegal letter representing the x-coordinate: " + letter);
        }

        int y = Integer.parseInt(tile.substring(1));
        return new Tile(x, y);
    }
}
