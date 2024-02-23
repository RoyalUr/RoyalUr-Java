package net.royalur.stats;

/**
 * Combined set of statistics from many games.
 */
public class GameStatsSummary {

    /**
     * Statistics about the number of rolls performed in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final double[][] rolls;

    /**
     * Statistics about the number of moves made in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final double[][] moves;

    /**
     * Statistics about the number of turns in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final double[][] turns;

    /**
     * Statistics about the count of drama in the summarised games.
     * The first dimension of this array is indexed by the ordinal of an element of {@link GameStatsTarget},
     * and the second dimension is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final double[][] drama;

    /**
     * Statistics about the number of turns that the winner held the lead in a game before winning.
     * This array is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final double[] turnsInLead;

    /**
     * Statistics about the percentage of turns that the winner held the lead in a game before winning.
     * This array is indexed by the ordinal of an element of {@link SummaryStat}.
     */
    private final double[] percentInLead;

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
     * @param turnsInLead Statistics about the number of turns that the winner held the lead in a
     *                    game before winning. This array is indexed by the ordinal of an element
     *                    of {@link SummaryStat}.
     * @param percentInLead Statistics about the percentage of turns that the winner held the lead in a
     *                      game before winning. This array is indexed by the ordinal of an element
     *                      of {@link SummaryStat}.
     */
    protected GameStatsSummary(
            double[][] rolls,
            double[][] moves,
            double[][] turns,
            double[][] drama,
            double[] turnsInLead,
            double[] percentInLead
    ) {
        this.rolls = rolls;
        this.moves = moves;
        this.turns = turns;
        this.drama = drama;
        this.turnsInLead = turnsInLead;
        this.percentInLead = percentInLead;
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about rolls for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about rolls for target {@code target}.
     */
    public double getRollsStatistic(GameStatsTarget target, SummaryStat statistic) {
        return rolls[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about moves for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about moves for target {@code target}.
     */
    public double getMovesStatistic(GameStatsTarget target, SummaryStat statistic) {
        return moves[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about turns for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about turns for target {@code target}.
     */
    public double getTurnsStatistic(GameStatsTarget target, SummaryStat statistic) {
        return turns[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about drama for target {@code target}.
     * @param target The target to retrieve the summary statistic about.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about drama for target {@code target}.
     */
    public double getDramaStatistic(GameStatsTarget target, SummaryStat statistic) {
        return drama[target.ordinal()][statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about turns in lead.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about turns in lead.
     */
    public double getTurnsInLeadStatistic(SummaryStat statistic) {
        return turnsInLead[statistic.ordinal()];
    }

    /**
     * Gets the value of the summary statistic {@code statistic} about percentage in lead.
     * @param statistic The summary statistic to retrieve.
     * @return The value of the summary statistic {@code statistic} about turns in lead.
     */
    public double getPercentInLeadStatistic(SummaryStat statistic) {
        return percentInLead[statistic.ordinal()];
    }

    /**
     * Summarises the statistics from many games.
     * @param stats The game statistics to summarise.
     * @return The summarised statistics from many games.
     */
    public static GameStatsSummary summarise(GameStats... stats) {
        double[][] rolls = new double[GameStatsTarget.values().length][];
        double[][] moves = new double[GameStatsTarget.values().length][];
        double[][] turns = new double[GameStatsTarget.values().length][];
        double[][] drama = new double[GameStatsTarget.values().length][];

        double[] measurements = new double[stats.length];
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

        // Summarise turnsInLead.
        for (int index = 0; index < stats.length; ++index) {
            measurements[index] = stats[index].getTurnsInLead();
        }
        double[] turnsInLead = SummaryStat.compute(measurements);

        // Summarise percentInLead.
        for (int index = 0; index < stats.length; ++index) {
            measurements[index] = stats[index].getPercentInLead();
        }
        double[] percentInLead = SummaryStat.compute(measurements);

        return new GameStatsSummary(rolls, moves, turns, drama, turnsInLead, percentInLead);
    }
}
