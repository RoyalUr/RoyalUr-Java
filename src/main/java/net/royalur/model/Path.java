package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The path that a player's pieces must take around the board.
 */
public class Path implements Iterable<Tile> {

    /**
     * The name of this path.
     */
    public final @Nonnull String name;

    /**
     * The player that this path is intended for.
     */
    public final @Nonnull Player player;

    /**
     * The ordered list of tiles that pieces must progress through on the board.
     * This does not include the start and end tiles that exist off of the board.
     */
    public final @Nonnull List<Tile> tiles;

    /**
     * The number of tiles in this path.
     */
    public final int length;

    /**
     * @param name The name of this path.
     * @param player The player that this path is intended for.
     * @param tiles The ordered list of tiles that pieces must progress through on the board.
     */
    public Path(@Nonnull String name, @Nonnull Player player, @Nonnull List<Tile> tiles) {
        if (tiles.isEmpty())
            throw new IllegalArgumentException("Paths must have at least one tile");

        this.name = name;
        this.player = player;
        this.tiles = Collections.unmodifiableList(new ArrayList<>(tiles));
        this.length = tiles.size();
    }

    /**
     * Retrieves the tile at the index {@param index} in this path. The index
     * is treated as a 0-based index into the tiles of this path.
     * @param index The index of the tile in this path.
     * @return The tile at the given index in this path.
     */
    public @Nonnull Tile get(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException(
                    "Index " + index + " is out of bounds of the tiles of this path " +
                    "(" + length + " tiles)"
            );
        }
        return tiles.get(index);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ (37 * player.hashCode()) ^ (97 * tiles.hashCode());
    }

    /**
     * Determines whether the path the tiles must take around the board is
     * equivalent between this path and {@param other}. This ignores the name
     * and intended player of the paths.
     * @param other The path to check for equivalency.
     * @return Whether the path the tiles must take around the board is equivalent
     *         between this path and {@param other}.
     */
    public boolean isEquivalent(@Nonnull Path other) {
        return tiles.equals(other.tiles);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Path other = (Path) obj;
        return name.equals(other.name) && player == other.player && isEquivalent(other);
    }

    @Override
    public @Nonnull String toString() {
        StringBuilder builder = new StringBuilder();
        if (!name.isEmpty()) {
            builder.append(name).append(" (").append(player.name).append(")");
        } else {
            builder.append(player.name);
        }
        builder.append(": ");
        for (int index = 0; index < tiles.size(); ++index) {
            if (index > 0) {
                builder.append(", ");
            }
            builder.append(tiles.get(index));
        }
        return builder.toString();
    }

    @Override
    public Iterator<Tile> iterator() {
        return tiles.iterator();
    }
}
