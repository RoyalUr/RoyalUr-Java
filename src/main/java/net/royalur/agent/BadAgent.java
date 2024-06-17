package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.Move;
import net.royalur.model.shape.BoardShape;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * An agent that prioritises moving the least advanced piece, and avoids
 * introducing pieces, moving on to rosettes, or capturing. This is not thread-safe.
 */
public class BadAgent extends BaseAgent {

    @Override
    public Move decideMove(Game game, List<Move> moves) {
        // Sort in ascending order of path index.
        moves = new ArrayList<>(moves);
        moves.sort(Comparator.comparingInt(Move::getSourceIndex));

        // Try to find a move that is not an introduction, score, rosette, or capture.
        BoardShape shape = game.getBoard().getShape();
        for (Move move : moves) {
            if (!move.isIntroduction() && !move.isScore()
                    && !move.isDestRosette(shape) && !move.isCapture()) {
                return move;
            }
        }

        // Try to find an introductory move.
        for (Move move : moves) {
            if (move.isIntroduction())
                return move;
        }

        // Try to find a scoring move.
        for (Move move : moves) {
            if (move.isScore())
                return move;
        }

        // Try to find a move with a destination that is a rosette.
        for (Move move : moves) {
            if (move.isDestRosette(shape))
                return move;
        }

        // Move the least advanced piece.
        return moves.get(0);
    }
}
