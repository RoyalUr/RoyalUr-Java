package net.royalur.stats;

import javax.annotation.Nonnull;

/**
 * Combined set of statistics from many games.
 */
public class GameStatsSummary {

    /**
     * Statistics about the number of rolls performed in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final @Nonnull double[][] rolls;

    /**
     * Statistics about the number of moves made in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final @Nonnull double[][] moves;

    /**
     * Statistics about the number of turns in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final @Nonnull double[][] turns;

    /**
     * Statistics about the count of drama in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final @Nonnull double[][] drama;

    /**
     * Instantiates a summary of the statistics from several games.
     * @param rolls Statistics about the number of rolls performed in the summarised games.
     *              The first dimension of this array is indexed by the ordinal of an element of
     *              {@link GameStatsTarget}, and the second dimension is indexed by the ordinal of
     *              an element of {@link SummaryStat}.
     * @param moves Statistics about the number of moves made in the summarised games.
     *              The first dimension of this array is indexed by the ordinal of an element of
     *              {@link GameStatsTarget}, and the second dimension is indexed by the ordinal of
     *              an element of {@link SummaryStat}.
     * @param turns Statistics about the number of turns made in the summarised games.
     *              The first dimension of this array is indexed by the ordinal of an element of
     *              {@link GameStatsTarget}, and the second dimension is indexed by the ordinal of
     *              an element of {@link SummaryStat}.
     * @param drama Statistics about the count of drama made in the summarised games.
     *              The first dimension of this array is indexed by the ordinal of an element of
     *              {@link GameStatsTarget}, and the second dimension is indexed by the ordinal of
     *              an element of {@link SummaryStat}.
     */
    protected GameStatsSummary(
            @Nonnull double[][] rolls,
            @Nonnull double[][] moves,
            @Nonnull double[][] turns,
            @Nonnull double[][] drama
    ) {
        this.rolls = rolls;
        this.moves = moves;
        this.turns = turns;
        this.drama = drama;
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about rolls for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about rolls for target {@code target}.
     */
    public double getRollsStatistic(@Nonnull GameStatsTarget target, @Nonnull SummaryStat statistic) {
        return rolls[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about moves for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about moves for target {@code target}.
     */
    public double getMovesStatistic(@Nonnull GameStatsTarget target, @Nonnull SummaryStat statistic) {
        return moves[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about turns for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about turns for target {@code target}.
     */
    public double getTurnsStatistic(@Nonnull GameStatsTarget target, @Nonnull SummaryStat statistic) {
        return turns[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about drama for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about drama for target {@code target}.
     */
    public double getDramaStatistic(@Nonnull GameStatsTarget target, @Nonnull SummaryStat statistic) {
        return drama[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Summarises the statistics from many games.
     * @param stats The game statistics to summarise.
     * @return The summarised statistics from many games.
     */
    public static @Nonnull GameStatsSummary summarise(GameStats... stats) {
        double[][] rolls = new double[GameStatsTarget.values().length][];
        double[][] moves = new double[GameStatsTarget.values().length][];
        double[][] turns = new double[GameStatsTarget.values().length][];
        double[][] drama = new double[GameStatsTarget.values().length][];

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

            // Summarise turns.
            for (int index = 0; index < stats.length; ++index) {
                measurements[index] = stats[index].getTurns(target);
            }
            turns[target.ordinal()] = SummaryStat.compute(measurements);

            // Summarise drama.
            for (int index = 0; index < stats.length; ++index) {
                measurements[index] = stats[index].getDrama(target);
            }
            drama[target.ordinal()] = SummaryStat.compute(measurements);
        }
        return new GameStatsSummary(rolls, moves, turns, drama);
    }
}
