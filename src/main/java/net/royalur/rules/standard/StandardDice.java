package net.royalur.rules.standard;

import net.royalur.model.Roll;
import net.royalur.rules.Dice;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Follows the standard probability distribution for dice
 * of the Royal Game of Ur, which consist of four D2 die.
 * Dice are not thread safe.
 */
public class StandardDice implements Dice<Roll> {

    /**
     * The source of randomness used to generate dice rolls.
     */
    private final Random random;

    /**
     * The number of D2 dice to roll.
     */
    private final int numDie;

    /**
     * Instantiates the standard dice with {@code random} as the source
     * of randomness to generate rolls.
     * @param random The source of randomness used to generate dice rolls.
     * @param numDie The number of D2 dice to roll.
     */
    public StandardDice(@Nonnull Random random, int numDie) {
        if (numDie <= 0)
            throw new IllegalArgumentException("numDice must be at least 1");
        if (numDie >= 31)
            throw new IllegalArgumentException("numDice must be less than 32");

        this.random = random;
        this.numDie = numDie;
    }

    /**
     * Instantiates the standard dice with a default random number generator, and 4 dice.
     */
    public StandardDice() {
        this(new Random(), 4);
    }

    @Override
    public int getMaxRollValue() {
        return numDie;
    }

    @Override
    public @Nonnull Roll roll() {
        // Each generated bit represents a roll of a D2 dice.
        return Roll.of(Integer.bitCount(random.nextInt(1 << numDie)));
    }

    @Override
    public @Nonnull Roll roll(int value) {
        return Roll.of(value);
    }
}
