package net.royalur.model.path;

import net.royalur.model.Path;
import net.royalur.model.PathPair;
import net.royalur.model.Player;
import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The pair of paths proposed by Masters for the Royal Game of Ur.
 * <p>
 * Citation: J. Masters, The Royal Game of Ur & The Game of 20 Squares (2021).
 * Available at
 * <a href="https://www.tradgames.org.uk/games/Royal-Game-Ur.htm">
 *     https://www.tradgames.org.uk/games/Royal-Game-Ur.htm
 * </a>.
 */
public class MastersPathPair extends PathPair {

    /**
     * The identifier given to Masters' paths.
     */
    public static final String ID = "Masters";

    /**
     * A path proposed by Masters.
     */
    public static abstract class MastersPath extends Path {
        /**
         * @param player The player that this path is intended for.
         * @param tiles  The ordered list of tiles that pieces must progress through on the board.
         */
        protected MastersPath(
                @Nonnull Player player,
                @Nonnull List<Tile> tiles,
                @Nonnull Tile startTile,
                @Nonnull Tile endTile
        ) {
            super(player, tiles, startTile, endTile);
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
                new Tile(0, 6)
        );

        public static final @Nonnull Tile START_TILE = new Tile(0, 4);
        public static final @Nonnull Tile END_TILE = new Tile(0, 5);

        public MastersLightPath() {
            super(Player.LIGHT, TILES, START_TILE, END_TILE);
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
                new Tile(2, 6)
        );

        public static final @Nonnull Tile START_TILE = new Tile(2, 4);
        public static final @Nonnull Tile END_TILE = new Tile(2, 5);

        public MastersDarkPath() {
            super(Player.DARK, TILES, START_TILE, END_TILE);
        }
    }

    public MastersPathPair() {
        super(new MastersLightPath(), new MastersDarkPath());
    }

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
    }
}
