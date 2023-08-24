package net.royalur.model.dice;

import net.royalur.name.Name;
import net.royalur.name.Named;

import javax.annotation.Nonnull;
import java.util.random.RandomGenerator;

/**
 * A generator of dice rolls.
 * @param <R> The type of rolls generated by this dice.
 */
public abstract class Dice<R extends Roll> implements Named<Name> {

    /**
     * The name of this dice.
     */
    private final @Nonnull Name name;

    /**
     * The source of randomness used to generate dice rolls.
     */
    private final @Nonnull RandomGenerator random;

    /**
     * Instantiates this dice with {@code random} as the source
     * of randomness to generate dice rolls.
     * @param name The name of this dice.
     * @param random The source of randomness used to generate dice rolls.
     */
    public Dice(@Nonnull Name name, @Nonnull RandomGenerator random) {
        this.name = name;
        this.random = random;
    }

    /**
     * Returns whether this dice holds any state that affects its dice rolls.
     * If this is overriden, then {@link #copyFrom(Dice)} should also be overriden.
     * @return Whether this dice holds any state that affects its dice rolls.
     */
    public boolean hasState() {
        return false;
    }

    /**
     * Copies the state of {@code other} into this dice. If the dice does
     * not have state, this is a no-op. The state copied does _not_ include
     * the seeding of the random number generator. If this is overriden,
     * then {@link #hasState()} should also be overriden.
     * @param other The dice to copy the state from.
     */
    public void copyFrom(@Nonnull Dice<R> other) {
        // Nothing to do.
    }

    @Override
    public @Nonnull Name getName() {
        return name;
    }

    /**
     * Gets the source of randomness that is used to generate dice rolls.
     * @return The source of randomness that is used to generate dice rolls.
     */
    public @Nonnull RandomGenerator getRandom() {
        return random;
    }

    /**
     * Gets the maximum value that could be rolled by this dice.
     * @return The maximum value that this dice could possibly roll.
     */
    public abstract int getMaxRollValue();

    /**
     * Gets the probability of rolling each value of the dice, where the
     * index into the returned array represents the value of the roll.
     * @return The probability of rolling each value of the dice.
     */
    public abstract float[] getRollProbabilities();

    /**
     * Generates a random roll using this dice, and returns just the value.
     * If this dice has state, this should call {@link #recordRoll(int)}.
     * @return A random roll of this dice, and returns just the value.
     */
    public abstract int rollValue();

    /**
     * Updates the state of this dice after having rolled {@code value}.
     * @param value The value that was rolled using this dice.
     */
    public void recordRoll(int value) {
        // Nothing to do.
    }

    /**
     * Generates a roll with value {@code value} using this dice.
     * @param value The value of the dice to be rolled.
     * @return A roll with value {@code value} of this dice.
     */
    public abstract @Nonnull R generateRoll(int value);

    /**
     * Generates a random roll using this dice.
     * @return A random roll of this dice.
     */
    public @Nonnull R roll() {
        return generateRoll(rollValue());
    }
}