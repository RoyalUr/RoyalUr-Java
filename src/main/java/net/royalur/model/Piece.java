package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A piece on a board.
 */
public class Piece {

    /**
     * A piece of the light player.
     */
    private static final Piece LIGHT = new Piece(Player.LIGHT);

    /**
     * A piece of the dark player.
     */
    private static final Piece DARK = new Piece(Player.DARK);

    /**
     * The player that owns this piece.
     */
    private final @Nonnull Player owner;

    /**
     * Instantiates a piece that can be placed on a board.
     * @param owner The player that owns the piece.
     */
    public Piece(@Nonnull Player owner) {
        this.owner = owner;
    }

    /**
     * Gets the player that owns this piece.
     * @return The player that owns this piece.
     */
    public @Nonnull Player getOwner() {
        return owner;
    }

    /**
     * Returns a piece with {@code player} as its owner.
     * @param player The player to retrieve a piece of.
     * @return The piece of the given player.
     */
    public static @Nonnull Piece of(@Nonnull Player player) {
        return switch (player) {
            case LIGHT -> LIGHT;
            case DARK -> DARK;
        };
    }

    /**
     * Converts {@code piece} to a single character that can be used
     * to textually represent the owner of a piece.
     *
     * @param piece The piece or {@code null} to convert to a character.
     * @return The character representing {@code piece}.
     */
    public static char toChar(@Nullable Piece piece) {
        return Player.toChar(piece != null ? piece.owner : null);
    }

    /**
     * Returns whether this piece has a stored path index.
     * @return Whether this piece has a stored path index.
     */
    public boolean hasPathIndex() {
        return false;
    }

    /**
     * Retrieves the path index of this piece.
     * @return The path index of this piece.
     */
    public int getPathIndex() {
        throw new UnsupportedOperationException("This piece does not have a path index");
    }

    @Override
    public int hashCode() {
        return owner.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Piece other = (Piece) obj;
        return owner == other.owner;
    }

    @Override
    public @Nonnull String toString() {
        return owner.getTextName();
    }
}
