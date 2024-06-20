package net.royalur.agent;

import net.royalur.Game;
import net.royalur.lut.Lut;
import net.royalur.model.GameSettings;
import net.royalur.model.Move;
import net.royalur.model.PlayerType;
import net.royalur.rules.simple.fast.FastSimpleGame;

import java.util.List;

/**
 * A variant of LutAgent that tries to make the worst moves possible.
 */
public class FlippedLutAgent extends BaseAgent {

    private final Lut lut;
    private final FastSimpleGame fastGame;
    private final FastSimpleGame tempGame;

    public FlippedLutAgent(Lut lut) {
        this.lut = lut;
        GameSettings settings = lut.getMetadata().getGameSettings();
        this.fastGame = new FastSimpleGame(settings);
        this.tempGame = new FastSimpleGame(settings);
    }

    @Override
    public Move decideMove(Game game, List<Move> availableMoves) {
        if (availableMoves.isEmpty())
            throw new IllegalStateException();
        if (availableMoves.size() == 1)
            return availableMoves.get(0);

        double bestScore = 101.0d;
        Move bestMove = null;
        for (Move move : availableMoves) {
            Game moveGame = game.copy();
            moveGame.move(move);

            fastGame.copyFrom(moveGame);
            double score = lut.getLightWinPercent(fastGame, tempGame);
            if (game.getTurn() == PlayerType.DARK) {
                score = 100.0d - score;
            }
            if (score <= bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        if (bestMove == null)
            throw new NullPointerException();

        return bestMove;
    }
}
