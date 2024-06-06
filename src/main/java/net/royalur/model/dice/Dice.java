package net.royalur.model.dice;

/**
 * A generator of dice rolls, that is _not_ necessarily thread-safe.
 */
public abstract class Dice {

    /**
     * The ID of this dice.
     */
    private final String id;

    /**
     * Instantiates this dice with the given ID.
     */
    public Dice(String id) {
        this.id = id;
    }

    /**
     * Gets the ID of this dice type.
     * @return The ID of this dice type.
     */
    public String getID() {
        return id;
    }

    /**
     * Gets whether this dice has an associated dice type.
     * Custom dice may not have an associated dice type.
     * @return Whether this dice has an associated dice type.
     */
    public boolean hasDiceType() {
        return DiceType.getByIDOrNull(id) != null;
    }

    /**
     * Gets the type of this dice.
     * @return The type of this dice.
     */
    public DiceType getDiceType() {
        return DiceType.getByID(id);
    }

    /**
     * Gets the name of this dice.
     * @return The name of this dice.
     */
    public String getName() {
        return getDiceType().getName();
    }

    /**
     * Returns whether this dice holds any state that affects its dice rolls.
     * If this is overridden, then {@link #copyFrom(Dice)} should also be overriden.
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
    public void copyFrom(Dice other) {
        // Nothing to do.
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
     * This does not update the state of the dice.
     * @param value The value of the dice to be rolled.
     * @return A roll with value {@code value} of this dice.
     */
    public abstract Roll generateRoll(int value);

    /**
     * Generates a random roll using this dice.
     * @return A random roll of this dice.
     */
    public Roll roll() {
        return generateRoll(rollValue());
    }

    /**
     * Generates a roll with value {@code value} using this dice.
     * @param value The value of the dice to be rolled.
     * @return A roll with value {@code value} of this dice.
     */
    public Roll roll(int value) {
        recordRoll(value);
        return generateRoll(value);
    }
}
