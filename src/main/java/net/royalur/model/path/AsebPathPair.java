package net.royalur.model.path;

import net.royalur.model.Path;
import net.royalur.model.PathPair;
import net.royalur.model.Player;
import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The standard paths that are used for Aseb.
 * <p>
 * Citation: W. Crist, A.E. Dunn-Vaturi, and A. de Voogt,
 * Ancient Egyptians at Play: Board Games Across Borders,
 * Bloomsbury Egyptology, Bloomsbury Academic, London, 2016.
 */
public class AsebPathPair extends PathPair {

    /**
     * The identifier given to the Aseb paths.
     */
    public static final String ID = "Aseb";

    /**
     * A standard path used for Aseb.
     */
    public static abstract class AsebPath extends Path {

        /**
         * Instantiates a standard path used for Aseb.
         * @param player The player that this path is intended for.
         * @param tiles  The ordered list of tiles that pieces must progress through on the board.
         * @param startTile The tile where a piece can be moved from to get on to the board.
         * @param endTile   The tile where a piece can be moved onto to get off of the board.
         */
        protected AsebPath(
                @Nonnull Player player,
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(player, tiles, startTile, endTile);
        }
    }

    /**
     * The standard Aseb path for the light player.
     */
    public static class AsebLightPath extends AsebPath {

        /**
         * The ordered list of tiles that must be traversed by pieces
         * on the Aseb board for the light player.
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
                new Tile(2, 9),
                new Tile(2, 10),
                new Tile(2, 11),
                new Tile(2, 12)
        );

        /**
         * The tile where a light piece can be moved from to get on to the board.
         * This tile does not exist on the Aseb board.
         */
        public static final @Nonnull Tile START_TILE = new Tile(1, 5);

        /**
         * The tile where a light piece can be moved onto to get off of the board.
         * This tile does not exist on the Aseb board.
         */
        public static final @Nonnull Tile END_TILE = new Tile(2, 13);

        /**
         * Instantiates the standard path for the light player in Aseb.
         */
        public AsebLightPath() {
            super(Player.LIGHT, TILES, START_TILE, END_TILE);
        }
    }

    /**
     * The standard Aseb path for the dark player.
     */
    public static class AsebDarkPath extends AsebPath {

        /**
         * The ordered list of tiles that must be traversed by pieces
         * on the Aseb board for the dark player.
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
                new Tile(2, 9),
                new Tile(2, 10),
                new Tile(2, 11),
                new Tile(2, 12)
        );

        /**
         * The tile where a dark piece can be moved from to get on to the board.
         * This tile does not exist on the Aseb board.
         */
        public static final @Nonnull Tile START_TILE = new Tile(3, 5);

        /**
         * The tile where a dark piece can be moved onto to get off of the board.
         * This tile does not exist on the Aseb board.
         */
        public static final @Nonnull Tile END_TILE = new Tile(2, 13);

        /**
         * Instantiates the standard path for the dark player in Aseb.
         */
        public AsebDarkPath() {
            super(Player.DARK, TILES, START_TILE, END_TILE);
        }
    }

    /**
     * Instantiates the standard paths for the light and dark player in Aseb.
     */
    public AsebPathPair() {
        super(new AsebLightPath(), new AsebDarkPath());
    }

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
    }
}
