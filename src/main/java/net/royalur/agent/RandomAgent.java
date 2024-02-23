package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.*;

import java.util.List;
import java.util.Random;

/**
 * An agent that makes random moves in games. This is not thread-safe.
 */
public class RandomAgent extends BaseAgent {

    /**
     * The source of randomness to use to decide the moves to make.
     */
    private final Random random;

    /**
     * Instantiates a random agent that uses {@code random} as its source of randomness.
     * @param random The source of randomness to use to decide the moves to make.
     */
    public RandomAgent(Random random) {
        this.random = random;
    }

    /**
     * Instantiates a random agent using a default source of randomness.
     */
    public RandomAgent() {
        this(new Random());
    }

    @Override
    public Move decideMove(Game game, List<Move> moves) {
        int randomIndex = random.nextInt(moves.size());
        return moves.get(randomIndex);
    }
}
