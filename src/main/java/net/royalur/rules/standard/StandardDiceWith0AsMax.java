package net.royalur.rules.standard;

import net.royalur.model.Roll;
import net.royalur.rules.Dice;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * A set of dice where rolling a zero actually gives you
 * the highest roll, instead of no moves.
 */
public class StandardDiceWith0AsMax implements Dice<Roll> {

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
    public StandardDiceWith0AsMax(@Nonnull Random random, int numDie) {
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
    public StandardDiceWith0AsMax() {
        this(new Random(), 3);
    }

    @Override
    public int getMaxRollValue() {
        return numDie + 1;
    }

    @Override
    public @Nonnull Roll roll() {
        // Each generated bit represents a roll of a D2 dice.
        int value = Integer.bitCount(random.nextInt(1 << numDie));
        return value > 0 ? Roll.of(value) : Roll.of(numDie + 1);
    }

    @Override
    public @Nonnull Roll roll(int value) {
        if (value <= 0 || value > getMaxRollValue())
            throw new IllegalArgumentException("This dice cannot roll " + value);
        return Roll.of(value);
    }
}
