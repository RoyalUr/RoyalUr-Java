package net.royalur.model.path;

import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The paths proposed by Masters for the Royal Game of Ur.
 * <p>
 * Citation: J. Masters, The Royal Game of Ur &amp; The Game of 20 Squares (2021).
 * Available at
 * <a href="https://www.tradgames.org.uk/games/Royal-Game-Ur.htm">
 *     https://www.tradgames.org.uk/games/Royal-Game-Ur.htm
 * </a>.
 */
public class MastersPathPair extends NamedPathPair<PathType> {

    /**
     * Instantiates Masters' paths for the light and dark player.
     */
    public MastersPathPair() {
        super(PathType.MASTERS, new MastersLightPath(), new MastersDarkPath());
    }

    /**
     * A path proposed by Masters.
     */
    public static abstract class MastersPath extends Path {

        /**
         * Instantiates Masters' path for a specific player.
         * @param tiles  The ordered list of tiles that pieces must progress through on the board.
         * @param startTile The tile where a piece can be moved from to get on to the board.
         * @param endTile   The tile where a piece can be moved onto to get off of the board.
         */
        protected MastersPath(
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(tiles, startTile, endTile);
        }
    }

    /**
     * Masters' path for the light player.
     */
    public static class MastersLightPath extends MastersPath {

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
                new Tile(1, 7)
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
        public static final @Nonnull Tile END_TILE = new Tile(1, 6);

        /**
         * Instantiates Masters' path for the light player.
         */
        public MastersLightPath() {
            super(TILES, START_TILE, END_TILE);
        }
    }

    /**
     * Masters' path for the dark player.
     */
    public static class MastersDarkPath extends MastersPath {

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
                new Tile(3, 7)
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
        public static final @Nonnull Tile END_TILE = new Tile(3, 6);

        /**
         * Instantiates Masters' path for the dark player.
         */
        public MastersDarkPath() {
            super(TILES, START_TILE, END_TILE);
        }
    }
}
