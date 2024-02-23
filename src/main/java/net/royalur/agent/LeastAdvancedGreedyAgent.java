package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.Move;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An agent that prioritises capturing, followed by moving on to rosettes,
 * and that prioritises moving the least advanced piece. This is not thread-safe.
 */
public class LeastAdvancedGreedyAgent extends BaseAgent {

    @Override
    public Move decideMove(Game game, List<Move> moves) {
        // Sort in ascending order of path index.
        moves = new ArrayList<>(moves);
        moves.sort(Comparator.comparingInt(Move::getSourceIndex));

        // Greedily select a capture.
        for (Move move : moves) {
            if (move.isCapture())
                return move;
        }

        // Greedily move to a rosette.
        for (Move move : moves) {
            if (move.isDestRosette(game.getBoard().getShape()))
                return move;
        }

        // Select the most advanced piece to move.
        return moves.get(0);
    }
}
