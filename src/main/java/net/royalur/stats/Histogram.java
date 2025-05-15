package net.royalur.stats;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Histogram {

    private final double min;
    private final double max;
    private final long[] bins;

    public Histogram(double min, double max, int numBins) {
        if (max <= min)
            throw new IllegalArgumentException("max must be greater than min");
        if (numBins <= 0)
            throw new IllegalArgumentException("numBins must be positive");

        this.min = min;
        this.max = max;
        this.bins = new long[numBins];
    }

    private int getBinIndex(double value) {
        int binIndex = (int) Math.floor(bins.length * (value - min) / (max - min));
        return Math.max(0, Math.min(bins.length - 1, binIndex));
    }

    public void add(double value) {
        bins[getBinIndex(value)] += 1;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int n = bins.length;
        for (int i = 0; i < n; i++) {
            double binStart = min + i * (max - min) / n;
            sb.append(String.format("%.3f: %d%n", binStart, bins[i]));
        }
        sb.append(String.format("%.3f", max));
        return sb.toString();
    }

    private static final DecimalFormat SCIENTIFIC_FORMAT = new DecimalFormat(
            "0.00E00", DecimalFormatSymbols.getInstance(Locale.US)
    );

    private String formatLog10Bin(double value) {
        return SCIENTIFIC_FORMAT.format(value).replaceAll("E(?![+-])", "E+");
    }

    public String toLog10String() {
        StringBuilder sb = new StringBuilder();
        int n = bins.length;
        for (int i = 0; i < n; i++) {
            double exponent = min + i * (max - min) / n;
            double lowerBound = Math.pow(10.0, exponent);
            sb.append(String.format("%8s: %d%n", formatLog10Bin(lowerBound), bins[i]));
        }
        double upperBound = Math.pow(10.0, max);
        sb.append(String.format("%8s", formatLog10Bin(upperBound)));
        return sb.toString();
    }
}
