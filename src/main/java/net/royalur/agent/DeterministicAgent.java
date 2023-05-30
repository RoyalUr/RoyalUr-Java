package net.royalur.agent;

import net.royalur.model.*;
import net.royalur.rules.state.WaitingForMoveGameState;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An agent that makes deterministic move choices for testing. This is not thread-safe.
 * @param <P> The type of pieces that this agent can interact with.
 * @param <S> The type of player state that this agent can interact with.
 * @param <R> The type of rolls that may be made by this agent.
 */
public class DeterministicAgent<P extends Piece, S extends PlayerState, R extends Roll> extends BaseAgent<P, S, R> {

    @Override
    public @Nonnull Move<P> decideMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state,
            @Nonnull List<Move<P>> moves
    ) {
        Move<P> chosen = null;
        for (Move<P> move : moves) {
            if (chosen == null) {
                chosen = move;
                continue;
            }

            // Always introduce a piece if it is possible.
            if (move.isIntroducingPiece()) {
                chosen = move;
                break;
            }

            // Otherwise move the piece that is closest to the top-left.
            Tile chosenSource = chosen.getSource();
            int chosenY = chosenSource.y;
            int chosenX = chosenSource.x;

            Tile moveSource = move.getSource();
            int moveY = moveSource.y;
            int moveX = moveSource.x;

            if (moveY < chosenY || (moveY == chosenY && moveX < chosenX)) {
                chosen = move;
            }
        }
        if (chosen == null)
            throw new IllegalArgumentException("No moves provided");

        return chosen;
    }
}
