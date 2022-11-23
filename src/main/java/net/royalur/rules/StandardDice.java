package net.royalur.rules;

import net.royalur.model.Roll;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Follows the standard probability distribution for dice
 * of the Royal Game of Ur, which consist of four D2 die.
 */
public class StandardDice implements Dice<Roll> {

    /**
     * The source of randomness used to generate dice rolls.
     */
    private final Random random;

    /**
     * @param random The source of randomness used to generate dice rolls.
     */
    public StandardDice(Random random) {
        this.random = random;
    }

    public StandardDice() {
        this(new Random());
    }

    @Override
    public @Nonnull Roll roll() {
        // Each generated bit represents a roll of a D2 dice.
        return Roll.of(Integer.bitCount(random.nextInt(16)));
    }
}
