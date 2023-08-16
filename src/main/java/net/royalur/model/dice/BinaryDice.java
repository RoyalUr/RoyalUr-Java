package net.royalur.model.dice;

import net.royalur.name.Name;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.random.RandomGenerator;

/**
 * Rolls a number of binary dice and counts the result.
 */
public class BinaryDice extends Dice<Roll> {

    /**
     * The number of binary dice to roll.
     */
    private final int numDie;

    /**
     * Instantiates this binary dice with {@code random} as the source
     * of randomness to generate rolls.
     * @param name The name of this dice.
     * @param random The source of randomness used to generate dice rolls.
     * @param numDie The number of binary dice to roll.
     */
    public BinaryDice(@Nonnull Name name, @Nonnull RandomGenerator random, int numDie) {
        super(name, random);
        if (numDie <= 0)
            throw new IllegalArgumentException("numDie must be at least 1");
        if (numDie >= 31)
            throw new IllegalArgumentException("numDie must be less than 32");

        this.numDie = numDie;
    }

    @Override
    public int getMaxRollValue() {
        return numDie;
    }

    @Override
    public int rollValue() {
        // Each generated bit represents a roll of a D2 dice.
        int bits = getRandom().nextInt(1 << numDie);
        return Integer.bitCount(bits);
    }

    @Override
    public @Nonnull Roll roll(int value) {
        if (value < 0 || value > getMaxRollValue())
            throw new IllegalArgumentException("This dice cannot roll " + value);
        return Roll.of(value);
    }
}
