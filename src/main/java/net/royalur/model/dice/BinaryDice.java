package net.royalur.model.dice;

import net.royalur.name.Name;

import javax.annotation.Nonnull;
import java.util.random.RandomGenerator;

/**
 * Rolls a number of binary die and counts the result.
 */
public class BinaryDice extends Dice<Roll> {

    /**
     * The number of binary dice to roll.
     */
    private final int numDie;

    /**
     * The probability of rolling each value with these dice.
     */
    private final float[] rollProbabilities;

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
        this.rollProbabilities = new float[numDie + 1];

        // Binomial Distribution
        double baseProb = Math.pow(0.5, numDie);
        int nChooseK = 1;
        for (int roll = 0; roll <= numDie; ++roll) {
            rollProbabilities[roll] = (float) (baseProb * nChooseK);
            nChooseK = nChooseK * (numDie - roll) / (roll + 1);
        }
    }

    @Override
    public int getMaxRollValue() {
        return numDie;
    }

    @Override
    public float[] getRollProbabilities() {
        return rollProbabilities;
    }

    @Override
    public int rollValue() {
        // Each generated bit represents a roll of a D2 dice.
        int bits = getRandom().nextInt(1 << numDie);
        return Integer.bitCount(bits);
    }

    @Override
    public @Nonnull Roll generateRoll(int value) {
        if (value < 0 || value > getMaxRollValue())
            throw new IllegalArgumentException("This dice cannot roll " + value);

        return SimpleRoll.of(value);
    }
}
