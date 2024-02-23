package net.royalur;

import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.DiceType;

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
    public static DiceFactory createDeterministicDice(DiceType diceType) {
        return diceType.createFactory(new Supplier<>() {
            private int seed = 47;

            @Override
            public RandomGenerator get() {
                return new Random(++seed);
            }

            @Override
            public String toString() {
                return diceType.toString();
            }
        });
    }
}
