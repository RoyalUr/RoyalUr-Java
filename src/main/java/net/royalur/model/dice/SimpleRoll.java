package net.royalur.model.dice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A roll of dice that contains just a value.
 *
 * @param value The value of the dice roll.
 */
public record SimpleRoll(int value) implements Roll {

    /**
     * A constant representing a roll of zero.
     */
    public static final SimpleRoll ZERO = new SimpleRoll(0);

    /**
     * A constant representing a roll of one.
     */
    public static final SimpleRoll ONE = new SimpleRoll(1);

    /**
     * A constant representing a roll of two.
     */
    public static final SimpleRoll TWO = new SimpleRoll(2);

    /**
     * A constant representing a roll of three.
     */
    public static final SimpleRoll THREE = new SimpleRoll(3);

    /**
     * A constant representing a roll of four.
     */
    public static final SimpleRoll FOUR = new SimpleRoll(4);

    /**
     * Instantiates a dice roll.
     * @param value The value of the roll.
     */
    public SimpleRoll {
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

        SimpleRoll other = (SimpleRoll) obj;
        return value == other.value;
    }

    @Override
    public @Nonnull String toString() {
        return Integer.toString(value);
    }

    /**
     * Generates a roll representing a roll of the value {@code value}.
     * @param value The value that was rolled on the dice.
     * @return A roll representing a roll of the value {@code value}.
     */
    public static @Nonnull SimpleRoll of(int value) {
        return switch (value) {
            case 0 -> ZERO;
            case 1 -> ONE;
            case 2 -> TWO;
            case 3 -> THREE;
            case 4 -> FOUR;
            default -> new SimpleRoll(value);
        };
    }
}
