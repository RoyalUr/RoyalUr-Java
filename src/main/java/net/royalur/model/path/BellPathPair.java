package net.royalur.model.path;

import net.royalur.model.Path;
import net.royalur.model.PathPair;
import net.royalur.model.Player;
import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The pair of paths proposed by Bell for the Royal Game of Ur.
 */
public class BellPathPair extends PathPair {

    /**
     * The name given to Bell's paths.
     */
    public static final String NAME = "Bell";

    /**
     * A path proposed by Bell.
     */
    public static abstract class BellPath extends Path {
        /**
         * @param player The player that this path is intended for.
         * @param tiles  The ordered list of tiles that pieces must progress through on the board.
         */
        protected BellPath(
                @Nonnull Player player,
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(NAME, player, tiles, startTile, endTile);
        }
    }

    /**
     * Bell's path for the light player.
     */
    public static class BellLightPath extends BellPath {

        /**
         * The ordered list of tiles that must be traversed by pieces
         * on the standard board for the light player.
         */
        public static final @Nonnull List<Tile> TILES = List.of(
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
                new Tile(0, 7),
                new Tile(0, 6)
        );

        public static final @Nonnull Tile START_TILE = new Tile(0, 4);
        public static final @Nonnull Tile END_TILE = new Tile(0, 5);

        public BellLightPath() {
            super(Player.LIGHT, TILES, START_TILE, END_TILE);
        }
    }

    /**
     * Bell's path for the dark player.
     */
    public static class BellDarkPath extends BellPath {

        /**
         * The ordered list of tiles that must be traversed by pieces
         * on the standard board for the dark player.
         */
        public static final @Nonnull List<Tile> TILES = List.of(
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
                new Tile(2, 7),
                new Tile(2, 6)
        );

        public static final @Nonnull Tile START_TILE = new Tile(2, 4);
        public static final @Nonnull Tile END_TILE = new Tile(2, 5);

        public BellDarkPath() {
            super(Player.DARK, TILES, START_TILE, END_TILE);
        }
    }

    public BellPathPair() {
        super(NAME, new BellLightPath(), new BellDarkPath());
    }
}
