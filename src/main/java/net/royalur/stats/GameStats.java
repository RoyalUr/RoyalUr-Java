package net.royalur.stats;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.state.PlayableGameState;
import net.royalur.model.state.WaitingForMoveGameState;
import net.royalur.model.state.WaitingForRollGameState;

import javax.annotation.Nonnull;

/**
 * A container of statistics about a game of the Royal Game of Ur.
 */
public class GameStats {

    /**
     * The number of rolls performed in the game.
     */
    public final @Nonnull int[] rolls;

    /**
     * The number of moves made in the game.
     */
    public final @Nonnull int[] moves;

    protected GameStats(@Nonnull int[] rolls, @Nonnull int[] moves) {
        this.rolls = rolls;
        this.moves = moves;
    }

    /**
     * Retrieves the number of rolls counted for the target {@param target}.
     * @param target The target to retrieve the statistic about.
     * @return The number of rolls counted for the target {@param target}.
     */
    public int getRolls(@Nonnull GameStatsTarget target) {
        return rolls[target.ordinal()];
    }

    /**
     * Retrieves the number of rolls performed by {@param player}.
     * @param player The player to retrieve the statistic about.
     * @return The number of rolls performed by {@param player}.
     */
    public int getRolls(@Nonnull Player player) {
        return getRolls(GameStatsTarget.get(player));
    }

    /**
     * Retrieves the number of moves counted for the target {@param target}.
     * @param target The target to retrieve the statistic about.
     * @return The number of moves counted for the target {@param target}.
     */
    public int getMoves(@Nonnull GameStatsTarget target) {
        return rolls[target.ordinal()];
    }

    /**
     * Retrieves the number of moves made by {@param player}.
     * @param player The player to retrieve the statistic about.
     * @return The number of moves made by {@param player}.
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
     * Gathers statistics about the game {@param game}.
     * @param game The game to gather statistics about.
     * @return The statistics gathered about the game.
     */
    public static @Nonnull GameStats gather(@Nonnull Game<?, ?, ?> game) {
        int[] rolls = new int[GameStatsTarget.values().length];
        int[] moves = new int[GameStatsTarget.values().length];

        // Count all the rolls and moves by counting pending states.
        PlayableGameState<?, ?, ?> lastPlayableState = null;
        for (GameState<?, ?, ?> state : game.getStates()) {
            if (!state.isPlayable())
                continue;

            PlayableGameState<?, ?, ?> playableState = (PlayableGameState<?, ?, ?>) state;
            lastPlayableState = playableState;
            PlayerState playerState = playableState.getTurnPlayer();
            Player player = playerState.player;

            if (playableState instanceof WaitingForRollGameState) {
                rolls[GameStatsTarget.OVERALL.ordinal()] += 1;
                rolls[GameStatsTarget.get(player).ordinal()] += 1;
            } else if (playableState instanceof WaitingForMoveGameState) {
                moves[GameStatsTarget.OVERALL.ordinal()] += 1;
                moves[GameStatsTarget.get(player).ordinal()] += 1;
            }
        }

        // Revert the last pending state.
        if (lastPlayableState != null && !game.isFinished()) {
            Player player = lastPlayableState.getTurnPlayer().player;
            if (lastPlayableState instanceof WaitingForRollGameState) {
                rolls[GameStatsTarget.OVERALL.ordinal()] -= 1;
                rolls[GameStatsTarget.get(player).ordinal()] -= 1;
            }
            if (lastPlayableState instanceof WaitingForMoveGameState) {
                moves[GameStatsTarget.OVERALL.ordinal()] -= 1;
                moves[GameStatsTarget.get(player).ordinal()] -= 1;
            }
        }

        // Create the statistics container.
        return new GameStats(rolls, moves);
    }

    /**
     * Summarises the statistics of all the given game statistics from {@param stats}.
     * This includes the generation of statistics such as sum, mean, variance, and
     * standard deviation.
     * @param stats The statistics to summarise.
     * @return The summarised statistics of all the statistics in {@param stats}.
     */
    public static @Nonnull GameStatsSummary summarise(GameStats... stats) {
        return GameStatsSummary.summarise(stats);
    }
}
