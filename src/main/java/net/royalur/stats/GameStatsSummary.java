package net.royalur.stats;

import javax.annotation.Nonnull;

/**
 * Combined sets of statistics from many games.
 */
public class GameStatsSummary {

    /**
     * Statistics about the number of rolls performed by each player in the game.
     */
    private final @Nonnull double[][] rolls;

    /**
     * Statistics about the number of moves made by each player in the game.
     */
    private final @Nonnull double[][] moves;

    protected GameStatsSummary(@Nonnull double[][] rolls, @Nonnull double[][] moves) {
        this.rolls = rolls;
        this.moves = moves;
    }

    /**
     * Retrieves the value of the summary statistic {@param statistic} about rolls for target {@param target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@param statistic} about rolls for target {@param target}.
     */
    public double getRollsStatistic(@Nonnull GameStatsTarget target, @Nonnull SummaryStat statistic) {
        return rolls[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Retrieves the value of the summary statistic {@param statistic} about moves for target {@param target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@param statistic} about moves for target {@param target}.
     */
    public double getMovesStatistic(@Nonnull GameStatsTarget target, @Nonnull SummaryStat statistic) {
        return moves[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Summarises the statistics from many games.
     * @param stats The game statistics to summarise.
     * @return The summarised statistics from many games.
     */
    public static @Nonnull GameStatsSummary summarise(GameStats... stats) {
        double[][] rolls = new double[GameStatsTarget.values().length][];
        double[][] moves = new double[GameStatsTarget.values().length][];

        int[] measurements = new int[stats.length];
        for (GameStatsTarget target : GameStatsTarget.values()) {
            // Summarise rolls.
            for (int index = 0; index < stats.length; ++index) {
                measurements[index] = stats[index].getRolls(target);
            }
            rolls[target.ordinal()] = SummaryStat.compute(measurements);

            // Summarise moves.
            for (int index = 0; index < stats.length; ++index) {
                measurements[index] = stats[index].getMoves(target);
            }
            moves[target.ordinal()] = SummaryStat.compute(measurements);
        }
        return new GameStatsSummary(rolls, moves);
    }
}
