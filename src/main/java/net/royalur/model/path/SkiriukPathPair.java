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
public class SkiriukPathPair extends PathPair {

    /**
     * The path of the light player's pieces.
     */
    public static final @Nonnull List<Tile> LIGHT_PATH = List.copyOf(PathPair.createPath(
            1, 5,
            1, 1,
            2, 1,
            2, 7,
            3, 7,
            3, 8,
            1, 8,
            1, 7,
            2, 7,
            2, 0
    ));

    /**
     * The path of the dark player's pieces.
     */
    public static final @Nonnull List<Tile> DARK_PATH = List.copyOf(PathPair.createPath(
            3, 5,
            3, 1,
            2, 1,
            2, 7,
            1, 7,
            1, 8,
            3, 8,
            3, 7,
            2, 7,
            2, 0
    ));

    /**
     * Instantiates Skiriuk's paths for the light and dark player.
     */
    public SkiriukPathPair() {
        super(PathType.SKIRIUK, LIGHT_PATH, DARK_PATH);
    }
}
