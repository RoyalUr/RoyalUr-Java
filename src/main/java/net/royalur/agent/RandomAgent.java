package net.royalur.agent;

import net.royalur.model.*;
import net.royalur.model.state.WaitingForMoveGameState;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

/**
 * An agent that makes random moves in games. This is not thread-safe.
 * @param <P> The type of pieces that this agent can interact with.
 * @param <S> The type of player state that this agent can interact with.
 * @param <R> The type of rolls that may be made by this agent.
 */
public class RandomAgent<P extends Piece, S extends PlayerState, R extends Roll> extends Agent<P, S, R> {

    /**
     * The unique identifier of the random agent.
     */
    public static final @Nonnull String ID = "Random";

    /**
     * The source of randomness to use to decide the moves to make.
     */
    private final Random random;

    /**
     * Instantiates a random agent that uses {@code random} as its source of randomness.
     * @param random The source of randomness to use to decide the moves to make.
     */
    public RandomAgent(@Nonnull Random random) {
        this.random = random;
    }

    /**
     * Instantiates a random agent using a default source of randomness.
     */
    public RandomAgent() {
        this(new Random());
    }

    @Override
    public @Nonnull String getIdentifier() {
        return ID;
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
