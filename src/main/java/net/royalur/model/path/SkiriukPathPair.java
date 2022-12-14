package net.royalur.model.path;

import net.royalur.model.Path;
import net.royalur.model.PathPair;
import net.royalur.model.Player;
import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The pair of paths proposed by Skiriuk for the Royal Game of Ur.
 */
public class SkiriukPathPair extends PathPair {

    /**
     * The name given to Skiriuk's paths.
     */
    public static final String NAME = "Skiriuk";

    /**
     * A path proposed by Skiriuk.
     */
    public static abstract class SkiriukPath extends Path {
        /**
         * @param player The player that this path is intended for.
         * @param tiles  The ordered list of tiles that pieces must progress through on the board.
         */
        protected SkiriukPath(
                @Nonnull Player player,
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(NAME, player, tiles, startTile, endTile);
        }
    }

    /**
     * Skiriuk's path for the light player.
     */
    public static class SkiriukLightPath extends SkiriukPath {

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
                new Tile(2, 6),
                new Tile(2, 7),
                new Tile(1, 7),
                new Tile(0, 7),
                new Tile(0, 6),
                new Tile(1, 6),
                new Tile(1, 5),
                new Tile(1, 4),
                new Tile(1, 3),
                new Tile(1, 2),
                new Tile(1, 1),
                new Tile(1, 0)
        );

        public static final @Nonnull Tile START_TILE = new Tile(0, 4);
        public static final @Nonnull Tile END_TILE = new Tile(1, -1);

        public SkiriukLightPath() {
            super(Player.LIGHT, TILES, START_TILE, END_TILE);
        }
    }

    /**
     * Skiriuk's path for the dark player.
     */
    public static class SkiriukDarkPath extends SkiriukPath {

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
                new Tile(0, 6),
                new Tile(0, 7),
                new Tile(1, 7),
                new Tile(2, 7),
                new Tile(2, 6),
                new Tile(1, 6),
                new Tile(1, 5),
                new Tile(1, 4),
                new Tile(1, 3),
                new Tile(1, 2),
                new Tile(1, 1),
                new Tile(1, 0)
        );

        public static final @Nonnull Tile START_TILE = new Tile(2, 4);
        public static final @Nonnull Tile END_TILE = new Tile(1, -1);

        public SkiriukDarkPath() {
            super(Player.DARK, TILES, START_TILE, END_TILE);
        }
    }

    public SkiriukPathPair() {
        super(NAME, new SkiriukLightPath(), new SkiriukDarkPath());
    }
}
