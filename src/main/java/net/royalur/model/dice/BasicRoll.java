package net.royalur.model.dice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A roll of dice that contains just a value.
 *
 * @param value The value of the dice roll.
 */
public record BasicRoll(int value) implements Roll {

    /**
     * A constant representing a roll of zero.
     */
    public static final BasicRoll ZERO = new BasicRoll(0);

    /**
     * A constant representing a roll of one.
     */
    public static final BasicRoll ONE = new BasicRoll(1);

    /**
     * A constant representing a roll of two.
     */
    public static final BasicRoll TWO = new BasicRoll(2);

    /**
     * A constant representing a roll of three.
     */
    public static final BasicRoll THREE = new BasicRoll(3);

    /**
     * A constant representing a roll of four.
     */
    public static final BasicRoll FOUR = new BasicRoll(4);

    /**
     * Instantiates a dice roll.
     * @param value The value of the roll.
     */
    public BasicRoll {
        if (value < 0) {
            throw new IllegalArgumentException(
                    "Rolls cannot be negative. Initialised with roll of " + value
            );
        }
    }

    /**
     * Gets the value of this dice roll.
     * @return The value of this dice roll.
     */
    @Override
    public int value() {
        return value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        BasicRoll other = (BasicRoll) obj;
        return value == other.value;
    }

    @Override
    public @Nonnull String toString() {
        return Integer.toString(value);
    }
}
