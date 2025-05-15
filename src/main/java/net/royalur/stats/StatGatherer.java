package net.royalur.stats;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Calculates running statistics (min, max, mean, variance, std‑dev) for large data streams.
 */
public class StatGatherer {

    /** 20 significant digits should be plenty. */
    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_EVEN);

    private long count;
    private double max = Double.NaN;
    private double min = Double.NaN;
    private BigDecimal mean = null;
    private BigDecimal m2 = null;

    public void add(double value) {
        count += 1;
        if (Double.isNaN(min) || value < min) {
            min = value;
        }
        if (Double.isNaN(max) | value > max) {
            max = value;
        }

        BigDecimal x = BigDecimal.valueOf(value);
        if (count == 1) {
            mean = x;
            m2   = BigDecimal.ZERO;
        } else {
            BigDecimal n = BigDecimal.valueOf(count);
            BigDecimal delta = x.subtract(mean, MC);
            mean = mean.add(delta.divide(n, MC), MC);
            BigDecimal delta2 = x.subtract(mean, MC);
            m2 = m2.add(delta.multiply(delta2, MC), MC);
        }
    }

    public double max() {
        return max;
    }

    public double min() {
        return min;
    }

    public double mean() {
        return count >= 1 ? mean.doubleValue() : Double.NaN;
    }

    public double variance() {
        if (count < 2)
            return Double.NaN;

        return m2.divide(BigDecimal.valueOf(count), MC).doubleValue();
    }

    public double stdDev() {
        return Math.sqrt(variance());
    }
}
