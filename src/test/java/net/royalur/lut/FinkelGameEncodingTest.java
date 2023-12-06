package net.royalur.lut;

import net.royalur.model.GameSettings;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class FinkelGameEncodingTest {

    @Test
    public void testUniqueness() {
        StateLUT lut = new StateLUT(GameSettings.FINKEL.withStartingPieceCount(4));
        int stateCount = lut.countStates();

        BigMap states = new BigMap(
                BigMap.INT,
                BigMap.BYTE
        );
        FinkelGameEncoding encoding = new FinkelGameEncoding(7);

        long start1 = System.currentTimeMillis();
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);

            Integer value = states.getInt(state);
            if (value != null)
                return;

            states.put(state, 0);
        });
        long duration1MS = System.currentTimeMillis() - start1;
        System.out.println("Population took " + duration1MS + " ms");
        assertEquals(stateCount, states.getEntryCount());

        long start2 = System.currentTimeMillis();
        AtomicInteger unsortedHits = new AtomicInteger(0);
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);

            Integer value = states.getInt(state);
            if (value != null) {
                unsortedHits.incrementAndGet();
            }
        });
        long duration2MS = System.currentTimeMillis() - start2;
        System.out.println("Unsorted check took " + duration2MS + " ms");
        assertEquals(stateCount, unsortedHits.get());

        long start3 = System.currentTimeMillis();
        states.sort();
        long duration3MS = System.currentTimeMillis() - start3;
        System.out.println("Sort took " + duration3MS + " ms");

        long start4 = System.currentTimeMillis();
        AtomicInteger sortedHits = new AtomicInteger(0);
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);

            Integer value = states.getInt(state);
            if (value != null) {
                sortedHits.incrementAndGet();
            }
        });
        long duration4MS = System.currentTimeMillis() - start4;
        System.out.println("Sorted check took " + duration4MS + " ms");
        assertEquals(stateCount, sortedHits.get());
    }
}
