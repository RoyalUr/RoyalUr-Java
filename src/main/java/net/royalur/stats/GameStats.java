package net.royalur.stats;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.state.*;

import javax.annotation.Nonnull;

/**
 * Statistics about a game of the Royal Game of Ur.
 */
public class GameStats {

    /**
     * The number of rolls performed in the game, indexed by the ordinal of an element of {@link GameStatsTarget}.
     */
    public final @Nonnull int[] rolls;

    /**
     * The number of moves made in the game, indexed by the ordinal of an element of {@link GameStatsTarget}.
     */
    public final @Nonnull int[] moves;

    /**
     * Instantiates statistics about a game of the Royal Game of Ur.
     * @param rolls The number of rolls performed in the game,
     *              indexed by the ordinal of an element of {@link GameStatsTarget}.
     * @param moves The number of moves made in the game,
     *              indexed by the ordinal of an element of {@link GameStatsTarget}.
     */
    protected GameStats(@Nonnull int[] rolls, @Nonnull int[] moves) {
        int targetCount = GameStatsTarget.values().length;
        if (rolls.length != targetCount) {
            throw new IllegalArgumentException(
                    "The rolls array should contain one entry for each of the " + targetCount + " GameStatsTargets, " +
                            "but instead it contained " + rolls.length + " elements"
            );
        }
        if (moves.length != targetCount) {
            throw new IllegalArgumentException(
                    "The moves array should contain one entry for each of the " + targetCount + " GameStatsTargets, " +
                            "but instead it contained " + rolls.length + " elements"
            );
        }

        this.rolls = rolls;
        this.moves = moves;
    }

    /**
     * Retrieves the number of rolls counted for the target {@code target}.
     * @param target The target to retrieve the statistic about.
     * @return The number of rolls counted for the target {@code target}.
     */
    public int getRolls(@Nonnull GameStatsTarget target) {
        return rolls[target.ordinal()];
    }

    /**
     * Retrieves the number of rolls performed by {@code player}.
     * @param player The player to retrieve the statistic about.
     * @return The number of rolls performed by {@code player}.
     */
    public int getRolls(@Nonnull Player player) {
        return getRolls(GameStatsTarget.get(player));
    }

    /**
     * Retrieves the number of moves counted for the target {@code target}.
     * @param target The target to retrieve the statistic about.
     * @return The number of moves counted for the target {@code target}.
     */
    public int getMoves(@Nonnull GameStatsTarget target) {
        return moves[target.ordinal()];
    }

    /**
     * Retrieves the number of moves made by {@code player}.
     * @param player The player to retrieve the statistic about.
     * @return The number of moves made by {@code player}.
     */
    public int getMoves(@Nonnull Player player) {
        return getMoves(GameStatsTarget.get(player));
    }

    /**
     * Retrieves the total number of rolls performed by both players.
     * @return The total number of rolls performed by both players.
     */
    public int getTotalRolls() {
        return getRolls(GameStatsTarget.OVERALL);
    }

    /**
     * Retrieves the total number of moves made by both players.
     * @return The total number of moves made by both players.
     */
    public int getTotalMoves() {
        return getMoves(GameStatsTarget.OVERALL);
    }

    /**
     * Gathers statistics about the game {@code game}.
     * @param game The game to gather statistics about.
     * @return The statistics gathered about the game.
     */
    public static @Nonnull GameStats gather(@Nonnull Game<?, ?, ?> game) {
        int[] rolls = new int[GameStatsTarget.values().length];
        int[] moves = new int[GameStatsTarget.values().length];

        // Count all the rolls and moves.
        for (GameState<?, ?, ?> state : game.getStates()) {
            if (!(state instanceof ActionGameState))
                continue;

            ActionGameState<?, ?, ?> actionState = (ActionGameState<?, ?, ?>) state;
            Player player = actionState.getTurnPlayer().player;

            if (actionState instanceof RolledGameState) {
                rolls[GameStatsTarget.OVERALL.ordinal()] += 1;
                rolls[GameStatsTarget.get(player).ordinal()] += 1;
            } else if (actionState instanceof MovedGameState) {
                moves[GameStatsTarget.OVERALL.ordinal()] += 1;
                moves[GameStatsTarget.get(player).ordinal()] += 1;
            }
        }

        // Create the statistics container.
        return new GameStats(rolls, moves);
    }

    /**
     * Summarises the statistics of all the given game statistics from {@code stats}.
     * This includes the generation of statistics such as sum, mean, variance, and
     * standard deviation.
     * @param stats The statistics to summarise.
     * @return The summarised statistics of all the statistics in {@code stats}.
     */
    public static @Nonnull GameStatsSummary summarise(GameStats... stats) {
        return GameStatsSummary.summarise(stats);
    }
}
