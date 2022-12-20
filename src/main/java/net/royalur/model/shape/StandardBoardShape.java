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
 *     xxxx  xx
 *     xxxxxxxx
 *     xxxx  xx
 * </code>
 */
public class StandardBoardShape extends BoardShape {

    /**
     * The identifier given to the standard board shape.
     */
    public static final String ID = "Standard";

    /**
     * The set of all tiles that exist on the standard board.
     * Any tile that exists in this set is on the standard board,
     * and any tiles that don't do not fall on the standard board.
     */
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

            new Tile(0, 7),
            new Tile(0, 6),

            new Tile(2, 7),
            new Tile(2, 6)
    );

    /**
     * The set of rosette tiles that exist on the standard board.
     */
    public static final @Nonnull Set<Tile> ROSETTE_TILES = Set.of(
            new Tile(0, 0),
            new Tile(2, 0),
            new Tile(1, 3),
            new Tile(0, 6),
            new Tile(2, 6)
    );

    public StandardBoardShape() {
        super(BOARD_TILES, ROSETTE_TILES);
    }

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
    }

    @Override
    public boolean contains(@Nonnull Tile tile) {
        return contains(tile.x, tile.y);
    }

    @Override
    public boolean contains(int x, int y) {
        return x >= 0 && x < 3 && y >= 0 && y < 8 && (x == 1 || (y < 4 || y >= 6));
    }

    @Override
    public boolean isRosette(@Nonnull Tile tile) {
        return isRosette(tile.x, tile.y);
    }

    @Override
    public boolean isRosette(int x, int y) {
        if (x == 1) {
            return y == 3;
        } else {
            return (x >= 0 && x < 3) && (y == 0 || y == 6);
        }
    }
}
