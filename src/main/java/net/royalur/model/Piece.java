package net.royalur.model;

import javax.annotation.Nullable;

/**
 * A piece on a board.
 */
public class Piece {

    /**
     * The player that owns this piece.
     */
    private final PlayerType owner;

    /**
     * The index of the piece on its owner player's path.
     */
    private final int pathIndex;

    /**
     * Instantiates a piece that can be placed on a board.
     * @param owner The player that owns the piece.
     * @param pathIndex The index of this piece on its owner's path
     *                  around the board.
     */
    public Piece(PlayerType owner, int pathIndex) {
        if (pathIndex < 0) {
            throw new IllegalArgumentException(
                    "The path index cannot be negative: " + pathIndex
            );
        }
        this.owner = owner;
        this.pathIndex = pathIndex;
    }

    /**
     * Gets the player that owns this piece.
     * @return The player that owns this piece.
     */
    public PlayerType getOwner() {
        return owner;
    }

    /**
     * Gets the path index of this piece.
     * @return The path index of this piece.
     */
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

        Piece other = (Piece) obj;
        return owner == other.owner && pathIndex == other.pathIndex;
    }

    @Override
    public String toString() {
        return owner.getTextName();
    }

    /**
     * Converts {@code piece} to a single character that can be used
     * to textually represent the owner of a piece.
     * @param piece The piece or {@code null} to convert to a character.
     * @return The character representing {@code piece}.
     */
    public static char toChar(@Nullable Piece piece) {
        return PlayerType.toChar(piece != null ? piece.owner : null);
    }
}
