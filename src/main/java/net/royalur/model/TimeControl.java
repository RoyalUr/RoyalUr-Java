package net.royalur.model;

import javax.annotation.Nullable;

public class TimeControl {
    public static final TimeControl NONE = new TimeControl(0);

    /**
     * It is important that these are ordered from longest to shortest suffix.
     */
    private static final String[] SECONDS_SUFFIXES = {
            " seconds", " second", "seconds", "second", " secs", " sec", "secs", "sec", " s", "s"
    };

    private final int perMoveSeconds;

    public TimeControl(int perMoveSeconds) {
        if (perMoveSeconds < 0)
            throw new IllegalArgumentException("perMoveSeconds must be >= 0");

        this.perMoveSeconds = perMoveSeconds;
    }

    public static TimeControl withPerMoveSeconds(int perMoveSeconds) {
        return new TimeControl(perMoveSeconds);
    }

    public boolean isTimed() {
        return hasPerMoveSeconds();
    }

    public boolean hasPerMoveSeconds() {
        return perMoveSeconds > 0;
    }

    public int getPerMoveSeconds() {
        if (perMoveSeconds == 0)
            throw new IllegalStateException("Time control does not have a per move time");
        return perMoveSeconds;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this)
            return true;
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        TimeControl other = (TimeControl) obj;
        return perMoveSeconds == other.perMoveSeconds;
    }

    @Override
    public String toString() {
        if (perMoveSeconds == 0)
            return "no time control";
        if (perMoveSeconds == 1)
            return "1 second per move";
        return perMoveSeconds + " seconds per move";
    }

    public static int parseSeconds(String text) {
        for (String suffix : SECONDS_SUFFIXES) {
            if (text.endsWith(suffix)) {
                String numberText = text.substring(0, text.length() - suffix.length());
                return Integer.parseInt(numberText);
            }
        }
        throw new IllegalArgumentException("Unable to recognise time duration: " + text);
    }

    public static TimeControl fromString(String text) {
        if (text.equals("no time control"))
            return NONE;

        if (text.endsWith(" per move")) {
            String perMoveText = text.substring(0, text.length() - " per move".length());
            int perMoveSeconds = parseSeconds(perMoveText);
            return new TimeControl(perMoveSeconds);
        }
        throw new IllegalArgumentException("Unable to recognise time control: " + text);
    }
}
