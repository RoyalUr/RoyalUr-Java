package net.royalur.stats;

import javax.annotation.Nonnull;

/**
 * A statistic that can be used to summarise a set of measurements.
 */
public enum SummaryStat {

    /**
     * The sum of all the measurements.
     */
    SUM,

    /**
     * The mean of all the measurements.
     */
    MEAN,

    /**
     * The variance of all the measurements.
     */
    VARIANCE,

    /**
     * The standard deviation of all the measurements.
     */
    STD_DEV;

    /**
     * Instantiate a statistic that can be used to summarise a set of measurements.
     */
    SummaryStat() {}

    /**
     * Computes all summary statistics for the measurements in {@param measurements}.
     * The indices into the returned array represent the ordinal of the SummaryStat
     * enum entries.
     * @param measurements The measurements to summarise.
     * @return The summary statistics for the measurements in {@param measurements}.
     */
    public static @Nonnull double[] compute(@Nonnull int[] measurements) {
        // Calculate the sum and mean.
        long sum = 0;
        for (int measurement : measurements) {
            sum += measurement;
        }
        double mean = (double) sum / measurements.length;

        // Calculate the standard deviation.
        double variance = 0;
        for (int measurement : measurements) {
            double diff = measurement - mean;
            variance += diff * diff;
        }
        variance /= measurements.length;
        double stdDev = Math.sqrt(variance);

        // Add the statistics to an array of values.
        double[] stats = new double[4];
        assert stats.length == SummaryStat.values().length;
        stats[SummaryStat.SUM.ordinal()] = sum;
        stats[SummaryStat.MEAN.ordinal()] = mean;
        stats[SummaryStat.VARIANCE.ordinal()] = variance;
        stats[SummaryStat.STD_DEV.ordinal()] = stdDev;
        return stats;
    }
}
