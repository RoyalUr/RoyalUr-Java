package net.royalur.model.path;

import net.royalur.model.Path;
import net.royalur.model.PathPair;
import net.royalur.model.Player;
import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The paths proposed by Murray for the Royal Game of Ur.
 * <p>
 * Citation: H.J.R. Murray, A History of Board-games Other Than Chess,
 * Oxford University Press, Oxford, 1952.
 */
public class MurrayPathPair extends PathPair {

    /**
     * The identifier given to Murray's paths.
     */
    public static final String ID = "Murray";

    /**
     * A path proposed by Murray.
     */
    public static abstract class MurrayPath extends Path {

        /**
         * Instantiates Murray's path for a specific player.
         * @param player The player that this path is intended for.
         * @param tiles  The ordered list of tiles that pieces must progress through on the board.
         * @param startTile The tile where a piece can be moved from to get on to the board.
         * @param endTile   The tile where a piece can be moved onto to get off of the board.
         */
        protected MurrayPath(
                @Nonnull Player player,
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(player, tiles, startTile, endTile);
        }
    }

    /**
     * Murray's path for the light player.
     */
    public static class MurrayLightPath extends MurrayPath {

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
                new Tile(2, 1),
                new Tile(3, 1),
                new Tile(3, 2),
                new Tile(3, 3),
                new Tile(3, 4)
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
        public static final @Nonnull Tile END_TILE = new Tile(3, 5);

        /**
         * Instantiates Murray's path for the light player.
         */
        public MurrayLightPath() {
            super(Player.LIGHT, TILES, START_TILE, END_TILE);
        }
    }

    /**
     * Murray's path for the dark player.
     */
    public static class MurrayDarkPath extends MurrayPath {

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
                new Tile(2, 1),
                new Tile(1, 1),
                new Tile(1, 2),
                new Tile(1, 3),
                new Tile(1, 4)
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
        public static final @Nonnull Tile END_TILE = new Tile(1, 5);

        /**
         * Instantiates Murray's path for the dark player.
         */
        public MurrayDarkPath() {
            super(Player.DARK, TILES, START_TILE, END_TILE);
        }
    }

    /**
     * Instantiates Murray's paths for the light and dark player.
     */
    public MurrayPathPair() {
        super(new MurrayLightPath(), new MurrayDarkPath());
    }

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
    }
}
