package net.royalur.model.shape;

import net.royalur.model.Tile;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * The standard shape of board used for The Royal Game of Ur that
 * follows the game boards that were excavated by Sir Leonard Woolley.
 * It has the following shape:
 * <code>
 *     xxxx  xx
 *     xxxxxxxx
 *     xxxx  xx
 * </code>
 */
public class StandardBoardShape extends BoardShape {

    /**
     * The set of all tiles that exist on the standard board.
     * Any tile that exists in this set is on the standard board,
     * and any tiles that don't do not fall on the standard board.
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

            new Tile(1, 8),
            new Tile(1, 7),

            new Tile(3, 8),
            new Tile(3, 7)
    );

    /**
     * The set of rosette tiles that exist on the standard board.
     */
    public static final @Nonnull Set<Tile> ROSETTE_TILES = Set.of(
            new Tile(1, 1),
            new Tile(3, 1),
            new Tile(2, 4),
            new Tile(1, 7),
            new Tile(3, 7)
    );

    /**
     * Instantiates the standard board shape used for the Royal Game of Ur.
     */
    public StandardBoardShape() {
        super(BOARD_TILES, ROSETTE_TILES);
    }

    @Override
    public boolean contains(@Nonnull Tile tile) {
        return contains(tile.ix, tile.iy);
    }

    @Override
    public boolean contains(int ix, int iy) {
        return ix >= 0 && ix < 3 && iy >= 0 && iy < 8 && (ix == 1 || (iy < 4 || iy >= 6));
    }

    @Override
    public boolean isRosette(@Nonnull Tile tile) {
        return isRosette(tile.ix, tile.iy);
    }

    @Override
    public boolean isRosette(int ix, int iy) {
        if (ix == 1) {
            return iy == 3;
        } else {
            return (ix >= 0 && ix < 3) && (iy == 0 || iy == 6);
        }
    }
}
