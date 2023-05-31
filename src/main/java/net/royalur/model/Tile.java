package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A tile represents location on a Royal Game of Ur board.
 */
public class Tile {

    /**
     * The x-coordinate of the tile. This coordinate is 1-based.
     */
    private final int x;

    /**
     * The x-index of the tile. This coordinate is 0-based.
     */
    private final int ix;

    /**
     * The y-coordinate of the tile. This coordinate is 1-based.
     */
    private final int y;

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

    /**
     * Determines whether the indices ({@code ix}, {@code iy}),
     * 0-based, could possibly represent a tile on a board. This
     * does not know the shape of the board, so it should only be
     * used as a quick common-sense check.
     *
     * @param ix The x-index of the tile. This coordinate is 0-based.
     * @param iy The y-index of the tile. This coordinate is 0-based.
     * @return Whether the tile at the given indices could possibly
     *         represent a tile on a board.
     */
    public static boolean isValidIndices(int ix, int iy) {
        return ix >= 0 && ix < 26 && iy >= 0;
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
}
