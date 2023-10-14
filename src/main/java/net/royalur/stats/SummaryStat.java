package net.royalur.stats;

import javax.annotation.Nonnull;
import java.util.Arrays;

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
    STD_DEV,

    /**
     * The median of all measurements.
     */
    MEDIAN,

    /**
     * The 25th percentile of measurements.
     */
    PERCENTILE_25,

    /**
     * The 75th percentile of measurements.
     */
    PERCENTILE_75,
    ;

    /**
     * Instantiate a statistic that can be used to summarise a set of measurements.
     */
    SummaryStat() {}

    private static double calculatePercentile(@Nonnull double[] sortedMeasurements, double percentile) {
        double index = (sortedMeasurements.length - 1) * percentile;
        int belowIndex = (int) Math.floor(index);
        int aboveIndex = (int) Math.ceil(index);

        double below = sortedMeasurements[belowIndex];
        double above = sortedMeasurements[aboveIndex];
        return below + (above - below) * (index - belowIndex);
    }

    /**
     * Computes all summary statistics for the measurements in {@code measurements}.
     * The indices into the returned array represent the ordinal of the SummaryStat
     * enum entries.
     * @param measurements The measurements to summarise.
     * @return The summary statistics for the measurements in {@code measurements}.
     */
    public static @Nonnull double[] compute(@Nonnull double[] measurements) {
        // Calculate the sum and mean.
        double sum = 0;
        for (double measurement : measurements) {
            sum += measurement;
        }
        double mean = sum / measurements.length;

        // Calculate the standard deviation.
        double variance = 0;
        for (double measurement : measurements) {
            double diff = measurement - mean;
            variance += diff * diff;
        }
        variance /= measurements.length;
        double stdDev = Math.sqrt(variance);

        // Calculate the percentiles.
        Arrays.sort(measurements);
        double percentile25 = calculatePercentile(measurements, 0.25);
        double median = calculatePercentile(measurements, 0.5);
        double percentile75 = calculatePercentile(measurements, 0.75);

        // Add the statistics to an array of values.
        double[] stats = new double[SummaryStat.values().length];
        stats[SummaryStat.SUM.ordinal()] = sum;
        stats[SummaryStat.MEAN.ordinal()] = mean;
        stats[SummaryStat.VARIANCE.ordinal()] = variance;
        stats[SummaryStat.STD_DEV.ordinal()] = stdDev;
        stats[SummaryStat.MEDIAN.ordinal()] = median;
        stats[SummaryStat.PERCENTILE_25.ordinal()] = percentile25;
        stats[SummaryStat.PERCENTILE_75.ordinal()] = percentile75;
        return stats;
    }
}
