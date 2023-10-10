package net.royalur.agent;

import net.royalur.Game;
import net.royalur.agent.utility.UtilityFunction;
import net.royalur.model.*;
import net.royalur.model.dice.Dice;
import net.royalur.model.dice.Roll;
import net.royalur.rules.standard.StandardRuleSet;
import net.royalur.rules.standard.fast.FastGame;
import net.royalur.rules.standard.fast.FastMoveList;
import net.royalur.util.Cast;

import javax.annotation.Nonnull;
import java.util.Arrays;
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
     * The rules used for games given to this agent.
     */
    private final @Nonnull StandardRuleSet<P, S, R> rules;

    /**
     * The utility function to use to evaluate game states.
     */
    private final @Nonnull UtilityFunction utilityFunction;

    /**
     * Any sequence of rolls that has a lower likelihood of
     * occurring than this will be ignored.
     */
    private final float likelihoodThreshold;

    /**
     * Game objects used to hold the state of games
     * while exploring the game tree.
     */
    private @Nonnull FastGame[] gameHolders;

    /**
     * Move lists used to hold available moves while
     * exploring the game tree.
     */
    private @Nonnull FastMoveList[] moveListHolders;

    /**
     * Dice used to hold the state of dice while exploring
     * the game tree.
     */
    private @Nonnull Dice<R>[] diceHolders;

    /**
     * Instantiates a likelihood agent.
     * @param utilityFunction The utility function to use to evaluate game states.
     * @param likelihoodThreshold The minimum likelihood threshold to explore a
     *                            sequence of rolls to further depth.
     */
    public LikelihoodAgent(
            @Nonnull StandardRuleSet<P, S, R> rules,
            @Nonnull UtilityFunction utilityFunction,
            float likelihoodThreshold
    ) {
        this.rules = rules;
        this.utilityFunction = utilityFunction;
        this.likelihoodThreshold = likelihoodThreshold;
        this.gameHolders = new FastGame[0];
        this.moveListHolders = new FastMoveList[0];
        this.diceHolders = Cast.unsafeCast(new Dice[0]);
    }

    /**
     * Gets a holding object that can be used to store the state of a game.
     * @param depth The depth to find the holding object for.
     * @return A holding object for storing the state of a game.
     */
    private @Nonnull FastGame getGameHolder(int depth) {
        if (depth >= gameHolders.length) {
            int newLength = Math.max(4, gameHolders.length * 2);
            while (depth >= newLength) {
                newLength *= 2;
            }
            int previousLength = gameHolders.length;
            gameHolders = Arrays.copyOf(gameHolders, newLength);
            for (int index = previousLength; index < newLength; ++index) {
                gameHolders[index] = rules.createCompatibleFastGame();
            }
        }
        return gameHolders[depth];
    }

    /**
     * Gets a holding object that can be used to store available moves.
     * @param depth The depth to find the holding object for.
     * @return A holding object for storing available moves.
     */
    private @Nonnull FastMoveList getMoveListHolder(int depth) {
        if (depth >= moveListHolders.length) {
            int newLength = Math.max(4, moveListHolders.length * 2);
            while (depth >= newLength) {
                newLength *= 2;
            }
            int previousLength = moveListHolders.length;
            moveListHolders = Arrays.copyOf(moveListHolders, newLength);
            for (int index = previousLength; index < newLength; ++index) {
                moveListHolders[index] = new FastMoveList();
            }
        }
        return moveListHolders[depth];
    }

    /**
     * Gets a holding object that can be used to store the state of a die,
     * and used to determine the available dice roll probabilities.
     * @param depth The depth to find the holding object for.
     * @return A holding object for the state of a die.
     */
    private @Nonnull Dice<R> getDiceHolder(int depth) {
        if (depth >= diceHolders.length) {
            int newLength = Math.max(4, diceHolders.length * 2);
            while (depth >= newLength) {
                newLength *= 2;
            }
            int previousLength = diceHolders.length;
            diceHolders = Arrays.copyOf(diceHolders, newLength);
            for (int index = previousLength; index < newLength; ++index) {
                diceHolders[index] = rules.getDiceFactory().createDice();
            }
        }
        return diceHolders[depth];
    }

    private float calculateBestMoveUtility(
            @Nonnull FastGame precedingGame,
            @Nonnull FastMoveList availableMoves,
            @Nonnull Dice<R> dice,
            float likelihood,
            int depth
    ) {
        if (!precedingGame.isWaitingForMove())
            throw new IllegalArgumentException("Game is not waiting for a move");

        float maxUtility = Float.NEGATIVE_INFINITY;

        int[] moves = availableMoves.moves;
        int moveCount = availableMoves.moveCount;

        FastGame game = getGameHolder(depth);

        for (int moveIndex = 0; moveIndex < moveCount; ++moveIndex) {
            game.copyFrom(precedingGame);
            game.applyMove(moves[moveIndex]);

            float utility = calculateProbabilityWeightedUtility(
                    game, dice, likelihood, depth + 1
            );
            if (game.isLightTurn != precedingGame.isLightTurn) {
                utility = -utility;
            }
            if (utility > maxUtility) {
                maxUtility = utility;
            }
        }
        return maxUtility;
     }

    private float calculateProbabilityWeightedUtility(
            @Nonnull FastGame precedingGame,
            @Nonnull Dice<R> precedingDice,
            float likelihood,
            int depth
    ) {
        if (precedingGame.isFinished || likelihood < likelihoodThreshold)
            return utilityFunction.scoreGame(precedingGame);
        if (!precedingGame.isWaitingForRoll())
            throw new IllegalArgumentException("Game is not waiting for a roll of the dice");

        float utility = 0.0f;
        float[] probabilities = precedingDice.getRollProbabilities();

        FastGame game = getGameHolder(depth);
        FastMoveList moveList = getMoveListHolder(depth);
        Dice<R> dice = getDiceHolder(depth);

        for (int roll = 0; roll < probabilities.length; ++roll) {
            float prob = probabilities[roll];
            if (prob == 0.0f)
                continue;

            // Update the state of the dice.
            dice.copyFrom(precedingDice);
            dice.recordRoll(roll);

            // Update the state of the game.
            game.copyFrom(precedingGame);
            game.applyRoll(roll, moveList);

            // Recurse!
            float rollLikelihood = prob * likelihood;
            float rollUtility;
            if (!game.isWaitingForMove()) {
                rollUtility = calculateProbabilityWeightedUtility(
                        game, dice, rollLikelihood, depth + 1
                );

            } else {
                rollUtility = calculateBestMoveUtility(
                        game, moveList, dice, rollLikelihood, depth + 1
                );
            }
            if (game.isLightTurn != precedingGame.isLightTurn) {
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

        FastGame gameHolder = getGameHolder(0);
        Dice<R> diceHolder = getDiceHolder(0);
        for (Move<P> move : moves) {
            Game<P, S, R> newGame = game.copy();
            newGame.makeMove(move);
            gameHolder.copyFrom(newGame);
            diceHolder.copyFrom(newGame.getDice());

            float utility = calculateProbabilityWeightedUtility(
                    gameHolder, diceHolder, 1.0f, 1
            );
            if (game.getTurn() != newGame.getTurn()) {
                utility = -utility;
            }

            if (bestMove == null || utility > bestUtility) {
                bestMove = move;
                bestUtility = utility;
            }
        }
        if (bestMove == null)
            throw new IllegalStateException("Best move is unexpectedly null");

        return bestMove;
    }
}
