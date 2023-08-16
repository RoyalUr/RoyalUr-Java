package net.royalur.model.dice;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A roll of a dice.
 */
public interface Roll {

    /**
     * Gets the value of this dice roll.
     * @return The value of this dice roll.
     */
    int value();

    /**
     * Generates a roll representing a roll of the value {@code value}.
     * @param value The value that was rolled on the dice.
     * @return A roll representing a roll of the value {@code value}.
     */
    static @Nonnull Roll of(int value) {
        return switch (value) {
            case 0 -> BasicRoll.ZERO;
            case 1 -> BasicRoll.ONE;
            case 2 -> BasicRoll.TWO;
            case 3 -> BasicRoll.THREE;
            case 4 -> BasicRoll.FOUR;
            default -> new BasicRoll(value);
        };
    }
}
