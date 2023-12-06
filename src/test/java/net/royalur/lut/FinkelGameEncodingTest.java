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

        BigMap states = new BigMap(
                ArrayBufferBuilder.INT,
                ArrayBufferBuilder.BYTE
        );

        FinkelGameEncoding encoding = new FinkelGameEncoding(7);
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);

            Integer value = states.getInt(state);
            if (value != null)
                return;

            states.put(state, 0);
        });
        assertEquals(stateCount, states.getEntryCount());
    }
}
