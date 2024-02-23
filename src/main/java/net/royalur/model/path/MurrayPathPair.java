package net.royalur.model.path;

import net.royalur.model.Tile;

import java.util.List;

/**
 * The paths proposed by Murray for the Royal Game of Ur.
 * <p>
 * Citation: H.J.R. Murray, A History of Board-games Other Than Chess,
 * Oxford University Press, Oxford, 1952.
 */
public class MurrayPathPair extends PathPair {

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
            1, 7,
            2, 7,
            2, 1,
            3, 1,
            3, 5
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
            3, 7,
            2, 7,
            2, 1,
            1, 1,
            1, 5
    ));

    /**
     * Instantiates Murray's paths for the light and dark player.
     */
    public MurrayPathPair() {
        super(PathType.MURRAY, LIGHT_PATH, DARK_PATH);
    }
}
