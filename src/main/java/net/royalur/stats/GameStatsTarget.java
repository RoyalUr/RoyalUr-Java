package net.royalur.stats;

import net.royalur.model.Player;

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
    LIGHT("Light Player", Player.LIGHT),

    /**
     * The statistics of the dark player.
     */
    DARK("Dark Player", Player.DARK);

    /**
     * A human-readable name representing this target, in English.
     */
    public final @Nonnull String name;

    /**
     * The player associated with this target, if this target
     * is associated with a player, or else {@code null}.
     */
    public final @Nullable Player player;

    /**
     * Instantiates a target for statistics about a game.
     * @param name A human-readable name representing this target, in English.
     * @param player The player associated with this target, if this target is
     *               associated with a player, or else {@code null}.
     */
    GameStatsTarget(@Nonnull String name, @Nullable Player player) {
        this.name = name;
        this.player = player;
    }

    /**
     * Retrieves the target associated with {@param player}.
     * @param player The player to retrieve the statistics for.
     * @return The target associated with {@param player}.
     */
    public static @Nonnull GameStatsTarget get(@Nonnull Player player) {
        switch (player) {
            case LIGHT: return GameStatsTarget.LIGHT;
            case DARK: return GameStatsTarget.DARK;
            default:
                throw new IllegalArgumentException("Unknown player " + player);
        }
    }
}
