package net.royalur.rules;

import net.royalur.TestUtils;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.DiceType;
import net.royalur.model.dice.Dice;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DiceTest {

    public static final Function<Dice<?>, Integer> ROLLVALUE_FN = Dice::rollValue;
    public static final Function<Dice<?>, Integer> ROLL_FN = dice -> dice.roll().value();

    public static class NamedRollFunction {
        public final String name;
        public final Function<Dice<?>, Integer> rollFunction;

        public NamedRollFunction(
                String name,
                Function<Dice<?>, Integer> rollFunction
        ) {
            this.name = name;
            this.rollFunction = rollFunction;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static class RollFunctionProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new NamedRollFunction(
                            "Dice.rollValue()",
                            ROLLVALUE_FN
                    )),
                    Arguments.of(new NamedRollFunction(
                            "Dice.roll().value()",
                            ROLL_FN
                    ))
            );
        }
    }

    public static class DiceTypeProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            DiceType[] diceTypes = DiceType.values();
            Arguments[] arguments = new Arguments[diceTypes.length];
            for (int index = 0; index < diceTypes.length; ++index) {
                arguments[index] = Arguments.of(diceTypes[index]);
            }
            return Stream.of(arguments);
        }
    }

    public static class DiceFactoryProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            DiceType[] diceTypes = DiceType.values();
            Arguments[] arguments = new Arguments[diceTypes.length];
            for (int index = 0; index < diceTypes.length; ++index) {
                arguments[index] = Arguments.of(
                        TestUtils.createDeterministicDice(diceTypes[index])
                );
            }
            return Stream.of(arguments);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DiceFactoryProvider.class)
    public void testNoInvalidRolls(DiceFactory<?> diceFactory) {
        int diceSamples = 10;
        int samplesPerDice = 10_000;
        for (int diceIndex = 0; diceIndex < diceSamples; ++diceIndex) {
            Dice<?> dice1 = diceFactory.createDice();
            Dice<?> dice2 = diceFactory.createDice();

            for (int sample = 0; sample < samplesPerDice; ++sample) {
                int value1 = dice1.rollValue();
                int value2 = dice2.roll().value();
                assertTrue(value1 >= 0 && value1 <= dice1.getMaxRollValue());
                assertTrue(value2 >= 0 && value2 <= dice2.getMaxRollValue());
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(RollFunctionProvider.class)
    public void testFourBinaryDiceDistribution(NamedRollFunction nrf) {
        Function<Dice<?>, Integer> rollFunction = nrf.rollFunction;

        Dice<?> dice = DiceType.FOUR_BINARY.createDice(new Random(47));
        assertEquals(dice.getMaxRollValue(), 4);

        int samples = 160_000;
        int[] counts = new int[5];
        for (int sample = 0; sample < samples; ++sample) {
            int value = rollFunction.apply(dice);
            assertTrue(value >= 0 && value <= 4);
            counts[value] += 1;
        }

        int[] expectedCounts = {
                samples / 16,
                4 * samples / 16,
                6 * samples / 16,
                4 * samples / 16,
                samples / 16
        };
        for (int roll = 0; roll <= 4; ++roll) {
            int count = counts[roll];
            int expected = expectedCounts[roll];
            int tolerance = expected / 2;
            assertTrue(count >= expected - tolerance && count <= expected + tolerance, "Roll " + roll);
        }
    }

    private double testDiceCorrelation(
            Dice<?> dice1,
            Dice<?> dice2,
            Function<Dice<?>, Integer> rollFunction1,
            Function<Dice<?>, Integer> rollFunction2
    ) {
        assertEquals(dice1.getMaxRollValue(), dice2.getMaxRollValue());
        int maxRollValue = dice1.getMaxRollValue();

        int iterations = 160_000;
        int diceMatchCount = 0;
        int[] counts1 = new int[maxRollValue + 1];
        int[] counts2 = new int[counts1.length];
        for (int i=0; i < iterations; ++i) {
            int sample1 = rollFunction1.apply(dice1);
            int sample2 = rollFunction2.apply(dice2);
            assertTrue(sample1 >= 0 && sample1 <= 4);
            assertTrue(sample2 >= 0 && sample2 <= 4);

            counts1[sample1] += 1;
            counts2[sample2] += 1;
            if (sample1 == sample2) {
                diceMatchCount += 1;
            }
        }

        for (int roll = 0; roll <= maxRollValue; ++roll) {
            int count1 = counts1[roll];
            int count2 = counts2[roll];
            int averageCount = (count1 + count2) / 2;
            int tolerance = averageCount / 2;

            assertTrue(
                    count1 >= count2 - tolerance && count1 <= count2 + tolerance,
                    "Roll " + roll + ": count1 = " + count1 + ", count2 = " + count2
            );
        }
        return (double) diceMatchCount / iterations;
    }

    @ParameterizedTest
    @ArgumentsSource(DiceFactoryProvider.class)
    public void testDiceNoSeed(DiceFactory<?> diceFactory) {
        Dice<?> dice1 = diceFactory.createDice();
        Dice<?> dice2 = diceFactory.createDice();
        double diceMatchRatio;

        // The roll function used should not affect correlation.
        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLL_FN, ROLL_FN);
        assertTrue(diceMatchRatio < 0.6);

        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLLVALUE_FN, ROLLVALUE_FN);
        assertTrue(diceMatchRatio < 0.6);

        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLL_FN, ROLLVALUE_FN);
        assertTrue(diceMatchRatio < 0.6);

        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLLVALUE_FN, ROLL_FN);
        assertTrue(diceMatchRatio < 0.6);
    }

    @ParameterizedTest
    @ArgumentsSource(DiceTypeProvider.class)
    public void testDiceWithSeed(DiceType diceType) {
        int seed = 763;
        Dice<?> dice1 = diceType.createDice(new Random(seed));
        Dice<?> dice2 = diceType.createDice(new Random(seed));
        double diceMatchRatio;

        // The roll function used should not affect correlation.
        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLL_FN, ROLL_FN);
        assertEquals(1.0, diceMatchRatio);

        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLLVALUE_FN, ROLLVALUE_FN);
        assertEquals(1.0, diceMatchRatio);

        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLL_FN, ROLLVALUE_FN);
        assertEquals(1.0, diceMatchRatio);

        diceMatchRatio = testDiceCorrelation(dice1, dice2, ROLLVALUE_FN, ROLL_FN);
        assertEquals(1.0, diceMatchRatio);
    }
}
