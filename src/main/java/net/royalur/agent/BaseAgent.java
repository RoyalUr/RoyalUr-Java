package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An agent that can autonomously play the Royal Game of Ur. Agents are designed to be used
 * on any game, and should not hold game-specific state.
 * @param <P> The type of pieces that this agent can interact with.
 * @param <S> The type of player state that this agent can interact with.
 * @param <R> The type of rolls that may be made by this agent or their opponent.
 */
public abstract class BaseAgent<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> implements Agent<P, S, R> {

    /**
     * Determines the move to be executed from the current state of the game.
     * @param game The game to find the best move in.
     * @param availableMoves The list of available moves to be chosen from.
     * @return The move that the agent chose to play.
     */
    public abstract Move<P> decideMove(
            Game<P, S, R> game,
            List<Move<P>> availableMoves
    );

    /**
     * Initiates the agent to play their turn in the given game.
     * @param game The game to play a turn in.
     */
    @Override
    public final void playTurn(Game<P, S, R> game) {
        if (game.isFinished())
            throw new IllegalStateException("The game has already been completed");
        if (!game.isPlayable())
            throw new IllegalStateException("The game is not in a playable state");

        // Just roll the dice, there's not usually any decisions to be made here.
        if (game.isWaitingForRoll()) {
            game.rollDice();
            return;
        }

        // Decide the move to be made.
        if (game.isWaitingForMove()) {
            List<Move<P>> moves = game.findAvailableMoves();
            Move<P> chosenMove = decideMove(game, moves);
            game.makeMove(chosenMove);
            return;
        }
        throw new IllegalStateException("The game is in an unexpected state");
    }
}
