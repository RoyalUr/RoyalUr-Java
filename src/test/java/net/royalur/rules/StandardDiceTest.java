package net.royalur.rules;

import net.royalur.model.Roll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class StandardDiceTest {

    private double testDice(Dice<Roll> dice1, Dice<Roll> dice2) {
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
    public void testDice() {
        StandardDice dice1 = new StandardDice();
        StandardDice dice2 = new StandardDice();
        double diceMatchRatio = testDice(dice1, dice2);
        assertTrue(diceMatchRatio < 0.6);

        dice1 = new StandardDice(new Random(42));
        dice2 = new StandardDice(new Random(42));
        diceMatchRatio = testDice(dice1, dice2);
        assertEquals(1.0, diceMatchRatio);
    }
}
