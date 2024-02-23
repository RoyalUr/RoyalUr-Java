package net.royalur.model.path;

import net.royalur.model.Tile;

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
public class MastersPathPair extends PathPair {

    /**
     * The path of the light player's pieces.
     */
    public static final List<Tile> LIGHT_PATH = List.copyOf(Tile.createPath(
            1, 5,
            1, 1,
            2, 1,
            2, 7,
            3, 7,
            3, 8,
            1, 8,
            1, 6
    ));

    /**
     * The path of the dark player's pieces.
     */
    public static final List<Tile> DARK_PATH = List.copyOf(Tile.createPath(
            3, 5,
            3, 1,
            2, 1,
            2, 7,
            1, 7,
            1, 8,
            3, 8,
            3, 6
    ));

    /**
     * Instantiates Masters' paths for the light and dark player.
     */
    public MastersPathPair() {
        super(PathType.MASTERS, LIGHT_PATH, DARK_PATH);
    }
}
