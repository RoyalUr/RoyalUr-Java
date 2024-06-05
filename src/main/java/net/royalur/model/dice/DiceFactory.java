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
