package net.royalur.agent;

import net.royalur.Game;
import net.royalur.lut.FinkelGameEncoding;
import net.royalur.lut.store.BigEntryStore;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.rules.simple.fast.FastSimpleGame;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Uses a lookup-table to decide the move to make.
 */
public class BadFinkelLUTAgent<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends BaseAgent<P, S, R> {

    private final @Nonnull BigEntryStore states;
    private final @Nonnull FinkelGameEncoding encoding;
    private final @Nonnull FastSimpleGame fastGame;

    public BadFinkelLUTAgent(@Nonnull BigEntryStore states) {
        this.states = states;
        this.encoding = new FinkelGameEncoding();
        this.fastGame = new FastSimpleGame(GameSettings.FINKEL);
    }

    @Override
    public @Nonnull Move<P> decideMove(
            @Nonnull Game<P, S, R> game,
            @Nonnull List<Move<P>> availableMoves
    ) {
        if (availableMoves.isEmpty())
            throw new IllegalStateException();
        if (availableMoves.size() == 1)
            return availableMoves.get(0);

        Float bestScore = null;
        Move<P> bestMove = null;
        for (Move<P> move : availableMoves) {
            Game<P, S, R> moveGame = game.copy();
            moveGame.makeMove(move);

            fastGame.copyFrom(moveGame);
            int key = encoding.encode(fastGame);
            Integer scoreBits = states.getInt(key);
            if (scoreBits == null)
                throw new IllegalStateException("State does not exist in map!");

            float score = Float.intBitsToFloat(scoreBits);
            score *= -1 * (game.getTurn() == PlayerType.DARK ? -1 : 1);
            if (bestScore == null || score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }
}
