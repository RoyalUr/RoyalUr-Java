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

    public final BoardShape shape;
    public final int width;
    public final int height;

    /**
     * The pieces that are on all tiles of this board.
     * Indexed by ix + iy * width. Empty tiles are zero.
     * Light tiles are positive path indices + 1. Dark
     * tiles are negative path indices - 1.
     */
    public final int[] pieces;
    public final int[] rosetteTiles;

    public FastSimpleBoard(BoardShape shape) {
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

    public void copyFrom(FastSimpleBoard other) {
        System.arraycopy(other.pieces, 0, pieces, 0, pieces.length);
    }

    public void copyFrom(Board<? extends Piece> board) {
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
     * Gets the underlying length of this board. This is not the area
     * of the board. Instead, it represents the number of indices that
     * can be used to reference a tile on the board.
     * @return The underlying length of this board.
     */
    public int length() {
        return pieces.length;
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

    public String toString(String linePrefix) {
        StringBuilder builder = new StringBuilder();
        for (int ix = 0; ix < shape.getWidth(); ++ix) {
            builder.append(linePrefix);
            for (int iy = 0; iy < shape.getHeight(); ++iy) {
                Tile tile = Tile.fromIndices(ix, iy);
                if (!shape.contains(tile)) {
                    builder.append(" ");
                    continue;
                }

                int piece = get(calcTileIndex(ix, iy));
                builder.append(piece < 0 ? "D" : (piece > 0 ? "L" : "-"));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return toString("");
    }
}
