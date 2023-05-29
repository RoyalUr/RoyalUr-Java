package net.royalur.rules;

import net.royalur.model.Roll;
import net.royalur.rules.standard.StandardDice;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class DiceTest {

    public static class DiceProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new StandardDice())
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(DiceProvider.class)
    public void testDistribution(Dice<?> dice) {
        if (dice.getMaxRollValue() != 4)
            throw new IllegalArgumentException("This test only suppports dice with a max roll of 4");

        int samples = 160_000;
        int[] counts = new int[5];
        for (int i=0; i < samples; ++i) {
            Roll roll = dice.roll();
            assertNotNull(roll);
            assertTrue(roll.value >= 0 && roll.value <= 4);

            counts[roll.value] += 1;
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

    private double testDiceCorrelation(Dice<Roll> dice1, Dice<Roll> dice2) {
        int iterations = 160_000;
        int diceMatchCount = 0;
        int[] counts = new int[5];
        for (int i=0; i < iterations; ++i) {
            Roll roll1 = dice1.roll();
            Roll roll2 = dice2.roll();
            assertNotNull(roll1);
            assertNotNull(roll2);

            int sample1 = roll1.value;
            int sample2 = roll2.value;
            assertTrue(sample1 >= 0 && sample1 <= 4);
            assertTrue(sample2 >= 0 && sample2 <= 4);

            counts[sample1] += 1;
            counts[sample2] += 1;
            if (sample1 == sample2) {
                diceMatchCount += 1;
            }
        }

        int samples = 2 * iterations;
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
        return (double) diceMatchCount / iterations;
    }

    @Test
    public void testDiceNoSeed() {
        StandardDice dice1 = new StandardDice();
        StandardDice dice2 = new StandardDice();
        double diceMatchRatio = testDiceCorrelation(dice1, dice2);
        assertTrue(diceMatchRatio < 0.6);
    }

    @Test
    public void testDiceWithSeed() {
        StandardDice dice1 = new StandardDice(new Random(42), 4);
        StandardDice dice2 = new StandardDice(new Random(42), 4);
        double diceMatchRatio = testDiceCorrelation(dice1, dice2);
        assertEquals(1.0, diceMatchRatio);
    }
}
