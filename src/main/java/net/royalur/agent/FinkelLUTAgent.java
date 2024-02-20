package net.royalur.agent;

import net.royalur.Game;
import net.royalur.lut.Lut;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.rules.simple.fast.FastSimpleGame;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Uses a lookup-table to decide the move to make.
 */
public class FinkelLUTAgent<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends BaseAgent<P, S, R> {

    private final @Nonnull Lut<R> lut;
    private final @Nonnull FastSimpleGame fastGame;

    public FinkelLUTAgent(@Nonnull Lut<R> lut) {
        this.lut = lut;
        this.fastGame = new FastSimpleGame(lut.getMetadata().getGameSettings());
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

        double bestScore = -1.0d;
        Move<P> bestMove = null;
        for (Move<P> move : availableMoves) {
            Game<P, S, R> moveGame = game.copy();
            moveGame.makeMove(move);

            fastGame.copyFrom(moveGame);
            double score = lut.getLightWinPercent(fastGame);
            if (game.getTurn() == PlayerType.DARK) {
                score = 100.0d - score;
            }
            score *= (game.getTurn() == PlayerType.DARK ? -1 : 1);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        if (bestMove == null)
            throw new NullPointerException();

        return bestMove;
    }
}
