package net.royalur.rules;

import net.royalur.model.Roll;

import javax.annotation.Nonnull;

/**
 * A generator of dice rolls.
 */
public abstract class Dice<R extends Roll> {

    /**
     * The maximum value that this dice could possibly roll.
     */
    public final int maxRoll;

    protected Dice(int maxRoll) {
        this.maxRoll = maxRoll;
    }

    /**
     * Gets an identifier that can be used to uniquely identify this type of dice.
     * @return An identifier that can be used to uniquely identify this type of dice.
     */
    public abstract @Nonnull String getIdentifier();

    /**
     * Generates a random roll using this dice.
     * @return A random roll of this dice.
     */
    public abstract @Nonnull R roll();
}
