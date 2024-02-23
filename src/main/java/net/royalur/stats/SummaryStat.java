package net.royalur.stats;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * A statistic that can be used to summarise a set of measurements.
 */
public enum SummaryStat {

    /**
     * The minimum of all the measurements.
     */
    MIN,

    /**
     * The maximum of all the measurements.
     */
    MAX,

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
     * The 5th percentile of measurements.
     */
    PERCENTILE_5,

    /**
     * The 25th percentile of measurements.
     */
    PERCENTILE_25,

    /**
     * The 75th percentile of measurements.
     */
    PERCENTILE_75,

    /**
     * The 95th percentile of measurements.
     */
    PERCENTILE_95,
    ;

    /**
     * Instantiate a statistic that can be used to summarise a set of measurements.
     */
    SummaryStat() {}

    private static double calculatePercentile(double[] sortedMeasurements, double percentile) {
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
    public static double[] compute(double[] measurements) {
        if (measurements.length == 0)
            throw new IllegalArgumentException("No measurements provided");

        // Calculate the min and max.
        double min = measurements[0];
        double max = measurements[0];
        for (double measurement : measurements) {
            min = Math.min(min, measurement);
            max = Math.max(max, measurement);
        }

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
        double percentile5 = calculatePercentile(measurements, 0.05);
        double percentile25 = calculatePercentile(measurements, 0.25);
        double median = calculatePercentile(measurements, 0.5);
        double percentile75 = calculatePercentile(measurements, 0.75);
        double percentile95 = calculatePercentile(measurements, 0.95);

        // Add the statistics to an array of values.
        double[] stats = new double[SummaryStat.values().length];
        stats[SummaryStat.MIN.ordinal()] = min;
        stats[SummaryStat.MAX.ordinal()] = max;
        stats[SummaryStat.SUM.ordinal()] = sum;
        stats[SummaryStat.MEAN.ordinal()] = mean;
        stats[SummaryStat.VARIANCE.ordinal()] = variance;
        stats[SummaryStat.STD_DEV.ordinal()] = stdDev;
        stats[SummaryStat.MEDIAN.ordinal()] = median;
        stats[SummaryStat.PERCENTILE_5.ordinal()] = percentile5;
        stats[SummaryStat.PERCENTILE_25.ordinal()] = percentile25;
        stats[SummaryStat.PERCENTILE_75.ordinal()] = percentile75;
        stats[SummaryStat.PERCENTILE_95.ordinal()] = percentile95;
        return stats;
    }
}
