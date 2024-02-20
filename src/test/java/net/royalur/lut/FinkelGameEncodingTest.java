package net.royalur.lut;

import net.royalur.Game;
import net.royalur.lut.buffer.ValueType;
import net.royalur.lut.store.OrderedUInt32BufferSet;
import net.royalur.model.GameSettings;
import static org.junit.jupiter.api.Assertions.*;

import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Dice;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class FinkelGameEncodingTest {

    /*@Test
    public void testUniqueness() {
        LutTrainer lut = new LutTrainer(GameSettings.FINKEL.withStartingPieceCount(3));
        int stateCount = lut.countStates();

        OrderedUInt32BufferSet states = new OrderedUInt32BufferSet(ValueType.UINT32, ValueType.UINT8);
        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();

        long start1 = System.currentTimeMillis();
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);

            Integer value = states.getInt(state);
            if (value != null)
                return;

            states.addEntry(state, 0);
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

    @Test
    public void testPopulate() {
        GameSettings<?> settings = GameSettings.FINKEL.withStartingPieceCount(3);
        LutTrainer lut = new LutTrainer(settings);
        int stateCount = lut.countStates();
        System.out.println("Counted " + stateCount + " states");

        OrderedUInt32BufferSet states = new OrderedUInt32BufferSet(ValueType.UINT32, ValueType.UINT32);
        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();

        long start1 = System.currentTimeMillis();
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);
            states.addEntry(state, state);
        });
        long duration1MS = System.currentTimeMillis() - start1;
        System.out.println(
                "Population took " + duration1MS + " ms "
                        + "for " + states.getEntryCount() + " entries "
                        + "with " + states.getChunkCount() + " chunks"
        );
        assertEquals(stateCount, states.getEntryCount());

        long start2 = System.currentTimeMillis();
        double overlapsPerChunkBeforeSort = states.getOverlapsPerChunk();
        states.sort();
        double overlapsPerChunkAfterSort = states.getOverlapsPerChunk();
        long duration2MS = System.currentTimeMillis() - start2;
        System.out.println("Sort took " + duration2MS + " ms");
        System.out.printf(
                "* Overlaps per chunk: %.2f -> %.2f\n",
                overlapsPerChunkBeforeSort,
                overlapsPerChunkAfterSort
        );

        long start3 = System.currentTimeMillis();
        AtomicInteger unsortedHits = new AtomicInteger(0);
        lut.loopGameStates((game) -> {
            int state = encoding.encode(game);

            Integer value = states.getInt(state);
            if (value != null) {
                if (value != state) {
                    throw new IllegalArgumentException(
                            "Expected all values to be equal to the keys! "
                                    + value + " != " + state
                    );
                }
                unsortedHits.incrementAndGet();
            }
        });
        long duration3MS = System.currentTimeMillis() - start3;
        System.out.println("Check took " + duration3MS + " ms");
        assertEquals(stateCount, unsortedHits.get());

        long start4 = System.currentTimeMillis();
        Game<Piece, PlayerState, ?> initialGame = Game.create(settings);
        Dice<?> dice = initialGame.getDice();
        Random random = new Random();
        FastSimpleGame game = new FastSimpleGame(settings);
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        int seenStates = 0;
        for (int gameNo = 0; gameNo < 100000; ++gameNo) {
            game.copyFrom(initialGame);
            while (!game.isFinished) {
                if (game.isWaitingForRoll()) {
                    game.applyRoll(dice.rollValue(), moveList);
                } else {
                    game.applyMove(moveList.moves[random.nextInt(moveList.moveCount)]);
                    int state = encoding.encode(game);
                    seenStates += 1;
                    Integer value = states.getInt(state);
                    if (value == null) {
                        fail("State could not be found in map!");
                    }
                }
            }
        }
        long duration4MS = System.currentTimeMillis() - start4;
        System.out.println("Gameplay verification took " + duration4MS + " ms for " + seenStates + " states");
    }*/
}
