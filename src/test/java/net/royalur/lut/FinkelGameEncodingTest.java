package net.royalur.lut;

import net.royalur.model.GameSettings;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class FinkelGameEncodingTest {

    @Test
    public void testUniqueness() {
        StateLUT lut = new StateLUT(GameSettings.FINKEL.withStartingPieceCount(3));
        int stateCount = lut.countStates();

        AtomicInteger stateIndex = new AtomicInteger(0);
        int[] states = new int[stateCount];

        FinkelGameEncoding encoding = new FinkelGameEncoding(7);
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);
            int processedStateCount = stateIndex.get();
            for (int index = 0; index < processedStateCount; ++index) {
                if (states[index] == state)
                    return;
            }
            states[stateIndex.getAndIncrement()] = state;
        });
        assertEquals(stateCount, stateIndex.get());
    }
}
