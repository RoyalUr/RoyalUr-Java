package net.royalur.model.path;

import net.royalur.model.Path;
import net.royalur.model.Player;
import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The standard path that is used for Aseb.
 */
public class AsebPath extends Path {

    /**
     * The ordered list of tiles that must be traversed by pieces
     * on the Aseb board for the light player.
     */
    public static final @Nonnull List<Tile> LIGHT_TILES = List.of(
            new Tile(0, 3),
            new Tile(0, 2),
            new Tile(0, 1),
            new Tile(0, 0),
            new Tile(1, 0),
            new Tile(1, 1),
            new Tile(1, 2),
            new Tile(1, 3),
            new Tile(1, 4),
            new Tile(1, 5),
            new Tile(1, 6),
            new Tile(1, 7),
            new Tile(1, 8),
            new Tile(1, 9),
            new Tile(1, 10),
            new Tile(1, 11)
    );

    /**
     * The ordered list of tiles that must be traversed by pieces
     * on the Aseb board for the dark player.
     */
    public static final @Nonnull List<Tile> DARK_TILES = List.of(
            new Tile(2, 3),
            new Tile(2, 2),
            new Tile(2, 1),
            new Tile(2, 0),
            new Tile(1, 0),
            new Tile(1, 1),
            new Tile(1, 2),
            new Tile(1, 3),
            new Tile(1, 4),
            new Tile(1, 5),
            new Tile(1, 6),
            new Tile(1, 7),
            new Tile(1, 8),
            new Tile(1, 9),
            new Tile(1, 10),
            new Tile(1, 11)
    );

    /**
     * The Aseb path for the light player.
     */
    public static final @Nonnull AsebPath LIGHT = new AsebPath(Player.LIGHT, LIGHT_TILES);

    /**
     * The Aseb path for the dark player.
     */
    public static final @Nonnull AsebPath DARK = new AsebPath(Player.DARK, DARK_TILES);

    /**
     * @param player The player that this path is intended for.
     * @param tiles  The ordered list of tiles that pieces must progress through on the board.
     */
    private AsebPath(@Nonnull Player player, @Nonnull List<Tile> tiles) {
        super("Aseb", player, tiles);
    }
}
