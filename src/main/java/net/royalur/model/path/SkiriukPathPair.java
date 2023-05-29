package net.royalur.model.path;

import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The paths proposed by Skiriuk for the Royal Game of Ur.
 * <p>
 * Citation: D. Skiriuk, The rules of royal game of ur (2021). Available at
 * <a href="https://skyruk.livejournal.com/231444.html">
 *     https://skyruk.livejournal.com/231444.html
 * </a>.
 */
public class SkiriukPathPair extends ConcreteNamedPathPair {

    /**
     * The name given to Skiriuk's paths.
     */
    public static final String NAME = "Skiriuk";

    /**
     * Instantiates Skiriuk's paths for the light and dark player.
     */
    public SkiriukPathPair() {
        super(NAME, new SkiriukLightPath(), new SkiriukDarkPath());
    }

    /**
     * A path proposed by Skiriuk.
     */
    public static abstract class SkiriukPath extends Path {

        /**
         * Instantiates Skiriuk's path for a specific player.
         * @param tiles  The ordered list of tiles that pieces must progress through on the board.
         * @param startTile The tile where a piece can be moved from to get on to the board.
         * @param endTile   The tile where a piece can be moved onto to get off of the board.
         */
        protected SkiriukPath(
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(tiles, startTile, endTile);
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
                new Tile(1, 4),
                new Tile(1, 3),
                new Tile(1, 2),
                new Tile(1, 1),
                new Tile(2, 1),
                new Tile(2, 2),
                new Tile(2, 3),
                new Tile(2, 4),
                new Tile(2, 5),
                new Tile(2, 6),
                new Tile(2, 7),
                new Tile(3, 7),
                new Tile(3, 8),
                new Tile(2, 8),
                new Tile(1, 8),
                new Tile(1, 7),
                new Tile(2, 7),
                new Tile(2, 6),
                new Tile(2, 5),
                new Tile(2, 4),
                new Tile(2, 3),
                new Tile(2, 2),
                new Tile(2, 1)
        );

        /**
         * The tile where a light piece can be moved from to get on to the board.
         * This tile does not exist on the standard board.
         */
        public static final @Nonnull Tile START_TILE = new Tile(1, 5);

        /**
         * The tile where a light piece can be moved onto to get off of the board.
         * This tile does not exist on the standard board.
         */
        public static final @Nonnull Tile END_TILE = new Tile(2, 0);

        /**
         * Instantiates Skiriuk's path for the light player.
         */
        public SkiriukLightPath() {
            super(TILES, START_TILE, END_TILE);
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
                new Tile(3, 4),
                new Tile(3, 3),
                new Tile(3, 2),
                new Tile(3, 1),
                new Tile(2, 1),
                new Tile(2, 2),
                new Tile(2, 3),
                new Tile(2, 4),
                new Tile(2, 5),
                new Tile(2, 6),
                new Tile(2, 7),
                new Tile(1, 7),
                new Tile(1, 8),
                new Tile(2, 8),
                new Tile(3, 8),
                new Tile(3, 7),
                new Tile(2, 7),
                new Tile(2, 6),
                new Tile(2, 5),
                new Tile(2, 4),
                new Tile(2, 3),
                new Tile(2, 2),
                new Tile(2, 1)
        );

        /**
         * The tile where a dark piece can be moved from to get on to the board.
         * This tile does not exist on the standard board.
         */
        public static final @Nonnull Tile START_TILE = new Tile(3, 5);

        /**
         * The tile where a dark piece can be moved onto to get off of the board.
         * This tile does not exist on the standard board.
         */
        public static final @Nonnull Tile END_TILE = new Tile(2, 0);

        /**
         * Instantiates Skiriuk's path for the dark player.
         */
        public SkiriukDarkPath() {
            super(TILES, START_TILE, END_TILE);
        }
    }
}
