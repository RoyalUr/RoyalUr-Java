package net.royalur;

import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.DiceType;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;

public class TestUtils {

    /**
     * Creates dice that always produce the same values, to help
     * with creating deterministic tests.
     * @param diceType The dice type to produce.
     * @return A factory that produces dice of the given type.
     */
    public static @Nonnull DiceFactory<Roll> createDeterministicDice(
            @Nonnull DiceType diceType
    ) {
        return diceType.createFactory(new Supplier<>() {
            private int seed = 47;

            @Override
            public @Nonnull RandomGenerator get() {
                return new Random(++seed);
            }

            @Override
            public @Nonnull String toString() {
                return diceType.toString();
            }
        });
    }
}
