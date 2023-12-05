package net.royalur.lut;

import net.royalur.model.GameSettings;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class FinkelGameEncodingTest {

    @Test
    public void testUniqueness() {
        StateLUT lut = new StateLUT(GameSettings.FINKEL);
        int stateCount = lut.countStates();

        AtomicInteger stateIndex = new AtomicInteger(0);
        int[] states = new int[stateCount];

        FinkelGameEncoding encoding = new FinkelGameEncoding(7);
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);
            int gameIndex = stateIndex.getAndIncrement();
            for (int index = 0; index < gameIndex; ++index) {
                if (states[gameIndex] == state)
                    throw new IllegalArgumentException("Duplicate!");
            }
            states[gameIndex] = state;
        });
        assertEquals(stateCount, stateIndex.get());
    }
}
