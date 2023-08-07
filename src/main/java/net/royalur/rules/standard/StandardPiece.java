package net.royalur.rules.standard;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A piece that does not store unique information, and which does not support
 * stacking. Stores the position of the piece on its path and its owner.
 */
public class StandardPiece extends Piece {

    /**
     * The index of the piece on its owner player's path.
     */
    private final int pathIndex;

    /**
     * Instantiates a piece used in a game using the simple rules.
     * @param owner The player that owns the piece.
     * @param pathIndex The index of this piece on its owner's path around the board.
     */
    public StandardPiece(@Nonnull PlayerType owner, int pathIndex) {
        super(owner);

        if (pathIndex < 0)
            throw new IllegalArgumentException("The path index cannot be negative. Invalid value: " + pathIndex);

        this.pathIndex = pathIndex;
    }

    @Override
    public boolean hasPathIndex() {
        return true;
    }

    @Override
    public int getPathIndex() {
        return pathIndex;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ (37 * Integer.hashCode(pathIndex));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        StandardPiece other = (StandardPiece) obj;
        return super.equals(other) && pathIndex == other.pathIndex;
    }

}
