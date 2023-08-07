package net.royalur.stats;

import net.royalur.model.PlayerType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A target for the accumulation of statistics for games of the Royal Game of Ur.
 */
public enum GameStatsTarget {
    /**
     * The overall statistics of both players.
     */
    OVERALL("Overall", null),

    /**
     * The statistics of the light player.
     */
    LIGHT("Light Player", PlayerType.LIGHT),

    /**
     * The statistics of the dark player.
     */
    DARK("Dark Player", PlayerType.DARK);

    /**
     * A human-readable name representing this target, in English.
     */
    private final @Nonnull String name;

    /**
     * The player associated with this target, if this target
     * is associated with a player, or else {@code null}.
     */
    private final @Nullable PlayerType player;

    /**
     * Instantiates a target for statistics about a game.
     * @param name A human-readable name representing this target, in English.
     * @param player The player associated with this target, if this target is
     *               associated with a player, or else {@code null}.
     */
    GameStatsTarget(@Nonnull String name, @Nullable PlayerType player) {
        this.name = name;
        this.player = player;
    }

    /**
     * Gets a human-readable name representing this target, in English.
     * @return A human-readable name representing this target, in English.
     */
    public @Nonnull String getName() {
        return name;
    }

    /**
     * Gets whether this target is associated with a specific player.
     * @return Whether this target is associated with a specific player.
     */
    public boolean hasAssociatedPlayer() {
        return player != null;
    }

    /**
     * Gets the specific player associated with this target.
     * @return The specific player associated with this target.
     * @throws UnsupportedOperationException if this target does not have an associated player.
     */
    public @Nonnull PlayerType getAssociatedPlayer() {
        if (player == null)
            throw new UnsupportedOperationException("This target does not have an associated player");

        return player;
    }

    /**
     * Retrieves the target associated with {@code player}.
     * @param player The player to retrieve the statistics for.
     * @return The target associated with {@code player}.
     */
    public static @Nonnull GameStatsTarget get(@Nonnull PlayerType player) {
        return switch (player) {
            case LIGHT -> GameStatsTarget.LIGHT;
            case DARK -> GameStatsTarget.DARK;
        };
    }
}
