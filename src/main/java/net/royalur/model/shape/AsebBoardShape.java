package net.royalur.model.shape;

import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * The standard shape of board used for The Royal Game of Ur that
 * follows the game boards that were excavated by Sir Leonard Woolley.
 * It has the following shape:
 * <code>
 *     xxxx
 *     xxxxxxxxxxxx
 *     xxxx
 * </code>
 */
public class AsebBoardShape extends BoardShape {

    /**
     * The set of all tiles that exist on the Aseb board.
     * Any tile that exists in this set is on the Aseb board,
     * and any tiles that don't do not fall on the Aseb board.
     */
    public static final @Nonnull Set<Tile> BOARD_TILES = Set.of(
            new Tile(1, 4),
            new Tile(1, 3),
            new Tile(1, 2),
            new Tile(1, 1),

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
        super(BOARD_TILES, ROSETTE_TILES);
    }
}
