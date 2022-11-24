package net.royalur.model.shape;

import net.royalur.model.BoardShape;
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

    public static final @Nonnull Set<Tile> BOARD_TILES = Set.of(
            new Tile(0, 3),
            new Tile(0, 2),
            new Tile(0, 1),
            new Tile(0, 0),

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
            new Tile(1, 7),

            new Tile(1, 8),
            new Tile(1, 9),
            new Tile(1, 10),
            new Tile(1, 11)
    );

    public AsebBoardShape() {
        super("Aseb", BOARD_TILES);
    }
}
