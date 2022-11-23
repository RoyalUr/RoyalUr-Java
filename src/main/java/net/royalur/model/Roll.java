package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A roll of a dice.
 */
public class Roll {

    /**
     * A constant representing a roll of zero.
     */
    public static final Roll ZERO = new Roll(0);

    /**
     * A constant representing a roll of one.
     */
    public static final Roll ONE = new Roll(1);

    /**
     * A constant representing a roll of two.
     */
    public static final Roll TWO = new Roll(2);

    /**
     * A constant representing a roll of three.
     */
    public static final Roll THREE = new Roll(3);

    /**
     * A constant representing a roll of four.
     */
    public static final Roll FOUR = new Roll(4);

    /**
     * The value of the dice roll.
     */
    public final int value;

    public Roll(int value) {
        if (value < 0)
            throw new IllegalArgumentException("Rolls cannot be negative. Initialised with roll of " + value);

        this.value = value;
    }

    /**
     * Generates a roll representing a roll of the value {@param value}.
     * @param value The value that was rolled on the dice.
     * @return A roll representing a roll of the value {@param value}.
     */
    public static @Nonnull Roll of(int value) {
        switch (value) {
            case 0: return ZERO;
            case 1: return ONE;
            case 2: return TWO;
            case 3: return THREE;
            case 4: return FOUR;
            default:
                return new Roll(value);
        }
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        Roll other = (Roll) obj;
        return value == other.value;
    }

    @Override
    public @Nonnull String toString() {
        return Integer.toString(value);
    }
}
