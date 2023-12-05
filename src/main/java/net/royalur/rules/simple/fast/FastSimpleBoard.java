package net.royalur.rules.simple.fast;

import net.royalur.model.Board;
import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.model.Tile;
import net.royalur.model.shape.BoardShape;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * The board of a simple game that is optimised for speed.
 * This speed comes at the cost of error checking and convenience.
 */
public class FastSimpleBoard {

    public final @Nonnull BoardShape shape;
    public final int width;
    public final int height;

    /**
     * The pieces that are on all tiles of this board.
     * Indexed by ix + iy * width. Empty tiles are zero.
     * Light tiles are positive path indices + 1. Dark
     * tiles are negative path indices - 1.
     */
    public final @Nonnull int[] pieces;
    public final @Nonnull int[] rosetteTiles;

    public FastSimpleBoard(@Nonnull BoardShape shape) {
        this.shape = shape;
        this.width = shape.getWidth();
        this.height = shape.getHeight();
        this.pieces = new int[width * height];

        this.rosetteTiles = new int[shape.getRosetteTiles().size()];
        int rosetteTilesIndex = 0;
        for (Tile tile : shape.getRosetteTiles()) {
            rosetteTiles[rosetteTilesIndex++] = calcTileIndex(
                    tile.getXIndex(), tile.getYIndex()
            );
        }
    }

    public void clear() {
        Arrays.fill(pieces, 0);
    }

    public void copyFrom(@Nonnull FastSimpleBoard other) {
        System.arraycopy(other.pieces, 0, pieces, 0, pieces.length);
    }

    public void copyFrom(@Nonnull Board<? extends Piece> board) {
        if (!shape.isEquivalent(board.getShape()))
            throw new IllegalArgumentException("board has a different shape");

        for (int ix = 0; ix < board.getWidth(); ++ix) {
            for (int iy = 0; iy < board.getHeight(); ++iy) {
                Piece piece = board.containsIndices(ix, iy) ? board.getByIndices(ix, iy) : null;

                int piecePathIndex;
                if (piece == null) {
                    piecePathIndex = 0;
                } else {
                    int pieceSign = (piece.getOwner() == PlayerType.LIGHT ? 1 : -1);
                    piecePathIndex = pieceSign * (piece.getPathIndex() + 1);
                }
                set(calcTileIndex(ix, iy), piecePathIndex);
            }
        }
    }

    public int calcTileIndex(int ix, int iy) {
        return ix + iy * width;
    }

    /**
     * Gets a piece from the board.
     * @param tileIndex The tile index.
     * @return Zero if empty, otherwise the absolute value of the returned
     *         value gives the path index + 1, and the sign gives the player
     *         (positive is light, negative is dark).
     */
    public int get(int tileIndex) {
        return pieces[tileIndex];
    }

    public void set(int tileIndex, int piece) {
        this.pieces[tileIndex] = piece;
    }

    public boolean isTileRosette(int tileIndex) {
        for (int rosetteTileIndex : rosetteTiles) {
            if (rosetteTileIndex == tileIndex)
                return true;
        }
        return false;
    }
}
