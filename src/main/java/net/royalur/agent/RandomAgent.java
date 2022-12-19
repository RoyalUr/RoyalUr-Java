package net.royalur.agent;

import net.royalur.model.*;
import net.royalur.model.state.WaitingForMoveGameState;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * An agent that makes random moves in games. This is not thread-safe.
 */
public class RandomAgent<P extends Piece, S extends PlayerState, R extends Roll> extends Agent<P, S, R> {

    /**
     * The source of randomness to use for deciding moves.
     */
    private final Random random;

    public RandomAgent(@Nonnull Random random) {
        super("random");
        this.random = random;
    }

    public RandomAgent() {
        this(new Random());
    }

    @Override
    public @Nonnull Move<P> decideMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state,
            @Nonnull List<Move<P>> moves
    ) {
        int randomIndex = random.nextInt(moves.size());
        return moves.get(randomIndex);
    }
}
