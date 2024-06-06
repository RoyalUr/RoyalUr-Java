package net.royalur.model.dice;

/**
 * A factory that creates dice.
 */
public interface DiceFactory {

    /**
     * Gets the ID of this dice type.
     * @return The ID of this dice type.
     */
    String getID();

    /**
     * Gets whether this dice has an associated dice type.
     * Custom dice may not have an associated dice type.
     * @return Whether this dice has an associated dice type.
     */
    default boolean hasDiceType() {
        return DiceType.getByIDOrNull(getID()) != null;
    }

    /**
     * Gets the type of this dice.
     * @return The type of this dice.
     */
    default DiceType getDiceType() {
        return DiceType.getByID(getID());
    }

    /**
     * Gets the name of this dice.
     * @return The name of this dice.
     */
    default String getName() {
        return getDiceType().getName();
    }

    /**
     * Create an instance of the dice using a default source of randomness.
     * @return The instance of the dice using a default source of randomness.
     */
    Dice createDice();

    /**
     * Generates a roll with the given value.
     * This is used solely for serialisation, and should not be used
     * for simulating games. Implementations may override this and
     * throw an error if they require additional information to create
     * their rolls.
     * @param value The value of the dice roll.
     * @return A roll with the given value.
     */
    default Roll createRoll(int value) {
        return createDice().generateRoll(value);
    }
}
