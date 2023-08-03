package net.royalur.model.path;

import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * The paths proposed by Bell for the Royal Game of Ur.
 * <p>
 * Citation: R.C. Bell, Board and Table Games From Many Civilizations,
 * revised ed., Vol. 1 and 2, Dover Publications, Inc., New York, 1979.
 */
public class BellPathPair extends NamedPathPair<PathType> {

    /**
     * The path of the light player's pieces.
     */
    public static final @Nonnull List<Tile> LIGHT_PATH = List.copyOf(PathPair.createPath(
            1, 5,
            1, 1,
            2, 1,
            2, 8,
            1, 8,
            1, 6
    ));

    /**
     * The path of the dark player's pieces.
     */
    public static final @Nonnull List<Tile> DARK_PATH = List.copyOf(PathPair.createPath(
            3, 5,
            3, 1,
            2, 1,
            2, 8,
            3, 8,
            3, 6
    ));

    /**
     * Instantiates Bell's paths for the light and dark player.
     */
    public BellPathPair() {
        super(PathType.BELL, LIGHT_PATH, DARK_PATH);
    }
}
