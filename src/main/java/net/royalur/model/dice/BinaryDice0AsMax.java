package net.royalur.model.dice;

import java.util.random.RandomGenerator;

/**
 * A set of binary dice where a roll of zero actually represents
 * the highest roll possible, rather than the lowest.
 */
public class BinaryDice0AsMax extends BinaryDice {

    /**
     * The maximum value that can be rolled by this dice.
     */
    private final int maxRollValue;

    /**
     * The probability of rolling each value with these dice.
     */
    private final float[] rollProbabilities;

    /**
     * Instantiates this binary dice with {@code random} as the source
     * of randomness to generate rolls.
     * @param id The ID of this dice.
     * @param random The source of randomness used to generate dice rolls.
     * @param numDie The number of binary dice to roll.
     */
    public BinaryDice0AsMax(String id, RandomGenerator random, int numDie) {
        super(id, random, numDie);
        this.maxRollValue = numDie + 1;
        this.rollProbabilities = new float[maxRollValue + 1];

        // Move the probability of 0 to the max roll slot.
        float[] dist = super.getRollProbabilities();
        rollProbabilities[0] = 0.0f;
        rollProbabilities[maxRollValue] = dist[0];
        System.arraycopy(dist, 1, rollProbabilities, 1, maxRollValue - 1);
    }

    @Override
    public int getMaxRollValue() {
        return maxRollValue;
    }

    @Override
    public float[] getRollProbabilities() {
        return rollProbabilities;
    }

    @Override
    public int rollValue() {
        int value = super.rollValue();
        return value > 0 ? value : maxRollValue;
    }

    @Override
    public Roll generateRoll(int value) {
        if (value <= 0 || value > getMaxRollValue())
            throw new IllegalArgumentException("This dice cannot roll " + value);

        return SimpleRoll.of(value);
    }
}
