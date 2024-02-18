package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.Move;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * An agent that prioritises capturing, followed by moving on to rosettes,
 * and that prioritises moving the most advanced piece. This is not thread-safe.
 * @param <P> The type of pieces that this agent can interact with.
 * @param <S> The type of player state that this agent can interact with.
 * @param <R> The type of rolls that may be made by this agent.
 */
public class GreedyAgent<P extends Piece, S extends PlayerState, R extends Roll> extends BaseAgent<P, S, R> {

    @Override
    public @Nonnull Move<P> decideMove(
            @Nonnull Game<P, S, R> game,
            @Nonnull List<Move<P>> moves
    ) {
        // Sort in descending order of path index.
        moves = new ArrayList<>(moves);
        moves.sort((a, b) -> {
            return b.getSourceIndex() - a.getSourceIndex();
        });

        // Greedily select a capture.
        for (Move<P> move : moves) {
            if (move.isCapture())
                return move;
        }

        // Greedily move to a rosette.
        for (Move<P> move : moves) {
            if (move.isDestRosette(game.getBoard().getShape()))
                return move;
        }

        // Select the most advanced piece to move.
        return moves.get(0);
    }
}
