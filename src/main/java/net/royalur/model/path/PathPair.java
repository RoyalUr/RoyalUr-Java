package net.royalur.model.path;

import net.royalur.model.Player;

import javax.annotation.Nonnull;

/**
 * Represents a pair of paths for the light and dark players to
 * move their pieces along in a game of the Royal Game of Ur.
 */
public interface PathPair {

    /**
     * The path that the light player's pieces must take.
     */
    @Nonnull Path getLight();

    /**
     * The path that the dark player's pieces must take.
     */
    @Nonnull Path getDark();

    /**
     * Retrieves the path for the player {@code player}.
     * @param player The player to get the path for.
     * @return The path for the given player.
     */
    default @Nonnull Path get(@Nonnull Player player) {
        return switch (player) {
            case LIGHT -> getLight();
            case DARK -> getDark();
        };
    }

    /**
     * Determines whether the paths that the light player's pieces must take,
     * and the paths that the dark player's pieces must take, are equivalent
     * between this path pair and {@code other}.
     * @param other The other pair of paths to check for equivalency.
     * @return Whether the paths that the light and dark player's pieces must take
     *         around the board are equivalent for this path pair and {@code other}.
     */
    default boolean isEquivalent(@Nonnull PathPair other) {
        return getLight().isEquivalent(other.getLight()) && getDark().isEquivalent(other.getDark());
    }

    /**
     * Gets a name to be used for identifying this path pair in debugging.
     * @return A name to be used for identifying this path pair in debugging.
     */
    default @Nonnull String getDebugName() {
        if (this instanceof NamedPathPair)
            return ((NamedPathPair) this).getName();

        return getClass().getName();
    }

    /**
     * Create a new path pair with the paths {@code lightPath} and {@code darkPath}.
     * @param lightPath The path for light pieces.
     * @param darkPath The path for dark pieces.
     * @return A new path pair with the given paths.
     */
    static @Nonnull PathPair create(@Nonnull Path lightPath, @Nonnull Path darkPath) {
        return new ConcretePathPair(lightPath, darkPath);
    }

    /**
     * Create a new path pair with the name {@code name} and the
     * paths {@code lightPath} and {@code darkPath}.
     * @param name The name of the path pair.
     * @param lightPath The path for light pieces.
     * @param darkPath The path for dark pieces.
     * @return A new path pair with the given name and paths.
     */
    static @Nonnull NamedPathPair create(@Nonnull String name, @Nonnull Path lightPath, @Nonnull Path darkPath) {
        return new ConcreteNamedPathPair(name, lightPath, darkPath);
    }
}
