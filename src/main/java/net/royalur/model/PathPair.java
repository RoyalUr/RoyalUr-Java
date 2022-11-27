package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a pair of paths for the light and dark players.
 */
public class PathPair {

    /**
     * The name of this pair of paths.
     */
    public final @Nonnull String name;

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
    public PathPair(@Nonnull String name, @Nonnull Path lightPath, @Nonnull Path darkPath) {
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
        this.name = name;
        this.lightPath = lightPath;
        this.darkPath = darkPath;
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
        return name.hashCode() ^ (37 * lightPath.hashCode()) ^ (91 * darkPath.hashCode());
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
        return name.equals(other.name) && lightPath.equals(other.lightPath) && darkPath.equals(other.darkPath);
    }

    @Override
    public @Nonnull String toString() {
        if (lightPath.name.equals(darkPath.name)) {
            if (lightPath.name.equals(name))
                return name;

            return name + " (of " + lightPath.name + " paths)";
        }
        return name + " (" +
                Player.LIGHT.name + ": " + lightPath.name + " path, " +
                Player.DARK.name + ": " + darkPath.name + " path)";
    }
}
