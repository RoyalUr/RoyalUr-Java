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
    public final @Nonnull Player owner;

    /**
     * Instantiates a piece that can be placed on a board.
     * @param owner The player that owns the piece.
     */
    public Piece(@Nonnull Player owner) {
        this.owner = owner;
    }

    /**
     * Returns a piece with {@param player} as its owner.
     * @param player The player to retrieve a piece of.
     * @return The piece of the given player.
     */
    public static @Nonnull Piece of(@Nonnull Player player) {
        switch (player) {
            case LIGHT: return LIGHT;
            case DARK: return DARK;
            default:
                throw new IllegalArgumentException("Unknown player " + player);
        }
    }

    /**
     * Converts {@param piece} to a single character that can be used
     * to textually represent the owner of a piece.
     *
     * @param piece The piece or {@code null} to convert to a character.
     * @return The character representing {@param piece}.
     */
    public static char toChar(@Nullable Piece piece) {
        return Player.toChar(piece != null ? piece.owner : null);
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
        return owner.name;
    }
}
