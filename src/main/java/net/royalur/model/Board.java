package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Stores the placement of pieces on the tiles of a Royal Game of Ur board.
 */
public class Board {

    /**
     * The shape of this board.
     */
    public final @Nonnull BoardShape shape;

    /**
     * The pieces on the tiles of this board.
     */
    private final @Nonnull Player[][] pieces;

    /**
     * @param shape The shape of this board.
     */
    public Board(@Nonnull BoardShape shape) {
        this.shape = shape;
        this.pieces = new Player[shape.width][shape.height];
    }

    /**
     * Determines whether {@param tile} falls within the bounds of this board.
     *
     * @param tile The tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(@Nonnull Tile tile) {
        return shape.contains(tile);
    }

    /**
     * Determines whether the tile at the coordinates ({@param x}, {@param y})
     * falls within the bounds of this board.
     *
     * @param x The x-coordinate of the tile to be bounds-checked.
     * @param y The y-coordinate of the tile to be bounds-checked.
     * @return Whether the given tile falls within the bounds of this board.
     */
    public boolean contains(int x, int y) {
        return shape.contains(x, y);
    }

    /**
     * Retrieves the piece on {@param tile}. Returns {@code null} if there is no piece on the tile.
     *
     * @param tile The tile to find the piece on.
     * @return The piece on the given tile if one exists, or else {@code null}.
     */
    public @Nullable Player get(@Nonnull Tile tile) {
        return get(tile.x, tile.y);
    }

    /**
     * Retrieves the piece on the tile at the coordinates ({@param x}, {@param y}).
     * Returns {@code null} if there is no piece on the tile.
     *
     * @param x The x-coordinate of the tile to find the piece on.
     * @param y The y-coordinate of the tile to find the piece on.
     * @return The piece on the given tile if one exists, or else {@code null}.
     */
    public @Nullable Player get(int x, int y) {
        if (!contains(x, y))
            throw new IllegalArgumentException("There is no tile at (" + x + ", " + y + ")");

        return pieces[x][y];
    }

    /**
     * Sets the piece on {@param tile} to a piece of {@param player}. If
     * {@param player} is {@code null}, it removes any piece on the tile.
     * Returns the player whose piece was previously on the tile, or
     * {@code null} if there was no piece on the tile.
     *
     * @param tile The tile to find the piece on.
     * @param player The player whose piece should be placed on the tile.
     * @return The previous piece on the given tile if there was one, or else {@code null}.
     */
    public @Nullable Player set(@Nonnull Tile tile, @Nullable Player player) {
        return set(tile.x, tile.y, player);
    }

    /**
     * Sets the piece on the tile at the coordinates ({@param x}, {@param y})
     * to a piece of {@param player}. If {@param player} is {@code null}, it
     * removes any piece on the tile. Returns the player whose piece was
     * previously on the tile, or {@code null} if there was no piece on the
     * tile.
     *
     * @param x The x-coordinate of the tile to place the piece on.
     * @param y The y-coordinate of the tile to place the piece on.
     * @param player The player whose piece should be placed on the tile.
     * @return The previous piece on the given tile if there was one, or else {@code null}.
     */
    public @Nullable Player set(int x, int y, @Nullable Player player) {
        if (!contains(x, y))
            throw new IllegalArgumentException("There is no tile at (" + x + ", " + y + ")");

        Player previous = pieces[x][y];
        pieces[x][y] = player;
        return previous;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int x = 0; x < shape.width; ++x) {
            if (x > 0) {
                builder.append("\n");
            }

            for (int y = 0; y < shape.height; ++y) {
                builder.append(contains(x, y) ? Player.toChar(get(x, y)) : ' ');
            }
        }
        return builder.toString();
    }
}
