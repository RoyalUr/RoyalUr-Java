package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a pair of paths for the light and dark players.
 */
public class PathPair {

    /**
     * The path that the light player's pieces must take.
     */
    public final @Nonnull Path lightPath;

    /**
     * The path that the dark player's pieces must take.
     */
    public final @Nonnull Path darkPath;

    /**
     * @param lightPath The path that the light player's pieces must take.
     * @param darkPath The path that the dark player's pieces must take.
     */
    public PathPair(@Nonnull Path lightPath, @Nonnull Path darkPath) {
        if (lightPath.player != Player.LIGHT) {
            throw new IllegalArgumentException(
                    "The lightPath is not intended for the " + Player.LIGHT.name + " player. " +
                    "If this is desired anyway, create a copy of the path with " + Player.LIGHT.name +
                    " as the intended player."
            );
        }
        if (darkPath.player != Player.DARK) {
            throw new IllegalArgumentException(
                    "The darkPath is not intended for the " + Player.DARK.name + " player. " +
                    "If this is desired anyway, create a copy of the path with " + Player.LIGHT.name +
                    " as the intended player."
            );
        }
        this.lightPath = lightPath;
        this.darkPath = darkPath;
    }

    /**
     * Gets an identifier that can be used to uniquely identify these paths.
     * @return An identifier that can be used to uniquely identify these paths.
     */
    public @Nonnull String getIdentifier() {
        throw new UnsupportedOperationException("This path pair does not have an identifier (" + getClass() + ")");
    }

    /**
     * Retrieves the path for the player {@param player}.
     * @param player The player to get the path for.
     * @return The path for the given player.
     */
    public @Nonnull Path get(@Nonnull Player player) {
        switch (player) {
            case LIGHT: return lightPath;
            case DARK: return darkPath;
            default:
                throw new IllegalArgumentException("Unknown player " + player);
        }
    }

    @Override
    public int hashCode() {
        return lightPath.hashCode() ^ (91 * darkPath.hashCode());
    }

    /**
     * Determines whether the paths that the light player's pieces must take,
     * and the paths that the dark player's pieces must take, are equivalent
     * between this path pair and {@param other}.
     * @param other The other pair of paths to check for equivalency.
     * @return Whether the paths that the light and dark player's pieces must take
     *         around the board are equivalent for this path pair and {@param other}.
     */
    public boolean isEquivalent(@Nonnull PathPair other) {
        return lightPath.isEquivalent(other.lightPath) && darkPath.isEquivalent(other.darkPath);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        PathPair other = (PathPair) obj;
        return lightPath.equals(other.lightPath) && darkPath.equals(other.darkPath);
    }

    @Override
    public @Nonnull String toString() {
        try {
            return getIdentifier() + " Path";
        } catch (UnsupportedOperationException e) {
            return "Unknown Path";
        }
    }
}
