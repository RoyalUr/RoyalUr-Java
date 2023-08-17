package net.royalur.agent;

import net.royalur.Game;
import net.royalur.agent.utility.UtilityFunction;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An agent that makes deterministic move choices for testing. This is not thread-safe.
 * @param <P> The type of pieces that this agent can interact with.
 * @param <S> The type of player state that this agent can interact with.
 * @param <R> The type of rolls that may be made by this agent.
 */
public class LikelihoodAgent<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends BaseAgent<P, S, R> {

    /**
     * The utility function to use to evaluate game states.
     */
    private final @Nonnull UtilityFunction<P, S, R> utilityFunction;

    /**
     * Any sequence of rolls that has a lower likelihood of
     * occurring than this will be ignored.
     */
    private final float likelihoodThreshold;

    /**
     * Instantiates a likelihood agent.
     * @param utilityFunction The utility function to use to evaluate game states.
     * @param likelihoodThreshold The minimum likelihood threshold to explore a
     *                            sequence of rolls to further depth.
     */
    public LikelihoodAgent(
            @Nonnull UtilityFunction<P, S, R> utilityFunction,
            float likelihoodThreshold
    ) {
        this.utilityFunction = utilityFunction;
        this.likelihoodThreshold = likelihoodThreshold;
    }

    private float calculateBestMoveUtility(
            @Nonnull Game<P, S, R> game,
            int roll,
            float likelihood
    ) {
        if (!game.isWaitingForMove())
            throw new IllegalArgumentException("Game is not waiting for a move");

        List<Move<P>> availableMoves = game.findAvailableMoves();
        float maxUtility = Float.NEGATIVE_INFINITY;

        for (Move<P> move : availableMoves) {
            Game<P, S, R> newGame = game.copy();
            newGame.makeMove(move);
            float moveUtility = calculateProbabilityWeightedUtility(newGame, likelihood);
            if (newGame.getTurnOrWinner() != game.getTurn()) {
                moveUtility = -moveUtility;
            }
            if (moveUtility > maxUtility) {
                maxUtility = moveUtility;
            }
        }
        return maxUtility;
     }

    private float calculateProbabilityWeightedUtility(
            @Nonnull Game<P, S, R> game,
            float likelihood
    ) {
        if (game.isFinished() || likelihood < likelihoodThreshold)
            return utilityFunction.scoreGame(game);
        if (!game.isWaitingForRoll())
            throw new IllegalArgumentException("Game is not waiting for a roll of the dice");

        float utility = 0.0f;
        float[] probabilities = game.getDice().getRollProbabilities();
        for (int roll = 0; roll < probabilities.length; ++roll) {
            float prob = probabilities[roll];
            if (prob == 0.0f)
                continue;

            float rollLikelihood = prob * likelihood;
            Game<P, S, R> newGame = game.copy();
            newGame.rollDice(roll);

            float rollUtility;
            if (!newGame.isWaitingForMove()) {
                rollUtility = calculateProbabilityWeightedUtility(newGame, rollLikelihood);
            } else {

                rollUtility = calculateBestMoveUtility(newGame, roll, rollLikelihood);
            }
            if (newGame.getTurn() != game.getTurn()) {
                rollUtility = -rollUtility;
            }

            utility += prob * rollUtility;
        }
        return utility;
    }

    @Override
    public @Nonnull Move<P> decideMove(
            @Nonnull Game<P, S, R> game,
            @Nonnull List<Move<P>> moves
    ) {
        if (moves.isEmpty())
            throw new IllegalArgumentException("No moves available");
        if (moves.size() == 1)
            return moves.get(0);

        Move<P> bestMove = null;
        float bestUtility = 0.0f;

        for (Move<P> move : moves) {
            Game<P, S, R> newGame = game.copy();
            newGame.makeMove(move);

            float utility = calculateProbabilityWeightedUtility(newGame, 1.0f);
            if (game.getTurn() != newGame.getTurn()) {
                utility = -utility;
            }

            if (bestMove == null || utility > bestUtility) {
                bestMove = move;
                bestUtility = utility;
            }
        }
        return bestMove;
    }
}
