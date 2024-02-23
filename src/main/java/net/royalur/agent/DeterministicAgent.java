package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.*;

import java.util.List;

/**
 * An agent that makes deterministic move choices for testing. This is not thread-safe.
 */
public class DeterministicAgent extends BaseAgent {

    @Override
    public Move decideMove(Game game, List<Move> moves) {
        Move chosen = null;
        for (Move move : moves) {
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
            int chosenY = chosenSource.getY();
            int chosenX = chosenSource.getX();

            Tile moveSource = move.getSource();
            int moveY = moveSource.getY();
            int moveX = moveSource.getX();

            if (moveY < chosenY || (moveY == chosenY && moveX < chosenX)) {
                chosen = move;
            }
        }
        if (chosen == null)
            throw new IllegalArgumentException("No moves provided");

        return chosen;
    }
}
