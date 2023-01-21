package net.royalur.model.path;

import net.royalur.model.Path;
import net.royalur.model.PathPair;
import net.royalur.model.Player;
import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The paths proposed by Bell for the Royal Game of Ur.
 * <p>
 * Citation: R.C. Bell, Board and Table Games From Many Civilizations,
 * revised ed., Vol. 1 and 2, Dover Publications, Inc., New York, 1979.
 */
public class BellPathPair extends PathPair {

    /**
     * The identifier given to Bell's paths.
     */
    public static final String ID = "Bell";

    /**
     * A path proposed by Bell.
     */
    public static abstract class BellPath extends Path {

        /**
         * Instantiates Bell's path for a specific player.
         * @param player    The player that this path is intended for.
         * @param tiles     The ordered list of tiles that pieces must progress through on the board.
         * @param startTile The tile where a piece can be moved from to get on to the board.
         * @param endTile   The tile where a piece can be moved onto to get off of the board.
         */
        protected BellPath(
                @Nonnull Player player,
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(player, tiles, startTile, endTile);
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
         * Instantiates Bell's path for the light player.
         */
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
         * Instantiates Bell's path for the dark player.
         */
        public BellDarkPath() {
            super(Player.DARK, TILES, START_TILE, END_TILE);
        }
    }

    /**
     * Instantiates Bell's paths for the light and dark player.
     */
    public BellPathPair() {
        super(new BellLightPath(), new BellDarkPath());
    }

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
    }
}
