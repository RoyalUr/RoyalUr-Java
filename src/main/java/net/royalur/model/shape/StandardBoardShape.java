package net.royalur.model.shape;

import net.royalur.model.Tile;
import net.royalur.model.path.BellPathPair;

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
     */
    public static final @Nonnull Set<Tile> BOARD_TILES = Tile.unionLists(
            BellPathPair.LIGHT_PATH, BellPathPair.DARK_PATH
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
        super(BoardType.STANDARD, BOARD_TILES, ROSETTE_TILES);
    }

    @Override
    public boolean contains(@Nonnull Tile tile) {
        return contains(tile.getXIndex(), tile.getYIndex());
    }

    @Override
    public boolean contains(int ix, int iy) {
        return ix >= 0 && ix < 3 && iy >= 0 && iy < 8 && (ix == 1 || (iy < 4 || iy >= 6));
    }

    @Override
    public boolean isRosette(@Nonnull Tile tile) {
        return isRosette(tile.getXIndex(), tile.getYIndex());
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
