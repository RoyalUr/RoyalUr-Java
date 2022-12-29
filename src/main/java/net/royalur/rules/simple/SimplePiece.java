package net.royalur.rules.simple;

import net.royalur.model.Piece;
import net.royalur.model.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A piece used by the simple rule set, which includes its position in its
 * path as well as its owner. This is required when paths overlap themselves.
 */
public class SimplePiece extends Piece {

    /**
     * The index of the piece on its owner player's path.
     */
    public final int pathIndex;

    /**
     * Instantiates a piece used in a game using the simple rules.
     * @param owner The player that owns the piece.
     * @param pathIndex The index of this piece on its owner's path around the board.
     */
    public SimplePiece(@Nonnull Player owner, int pathIndex) {
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
        return owner.hashCode() ^ (37 * Integer.hashCode(pathIndex));
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        SimplePiece other = (SimplePiece) obj;
        return owner == other.owner && pathIndex == other.pathIndex;
    }

}
