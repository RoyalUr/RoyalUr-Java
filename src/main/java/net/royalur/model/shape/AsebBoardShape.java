package net.royalur.model.shape;

import net.royalur.model.Tile;
import net.royalur.model.path.AsebPathPair;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * The shape of the board used for the game Aseb:
 * <code>
 *     xxxx
 *     xxxxxxxxxxxx
 *     xxxx
 * </code>
 */
public class AsebBoardShape extends BoardShape {

    /**
     * The set of all tiles that exist on the Aseb board.
     */
    public static final @Nonnull Set<Tile> BOARD_TILES = Tile.unionLists(
            new AsebPathPair().getLight(),
            new AsebPathPair().getDark()
    );

    /**
     * The set of rosette tiles that exist on the Aseb board.
     */
    public static final @Nonnull Set<Tile> ROSETTE_TILES = Set.of(
            new Tile(1, 1),
            new Tile(3, 1),
            new Tile(2, 4),
            new Tile(2, 8),
            new Tile(2, 12)
    );

    /**
     * Instantiates the board shape used for Aseb.
     */
    public AsebBoardShape() {
        super(BoardType.ASEB, BOARD_TILES, ROSETTE_TILES);
    }
}
