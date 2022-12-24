package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.state.WaitingForMoveGameState;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An agent that can autonomously play the Royal Game of Ur. Agents are designed to be used
 * on any game, and should not hold game-specific state.
 * @param <P> The type of pieces that this agent can interact with.
 * @param <S> The type of player state that this agent can interact with.
 * @param <R> The type of rolls that may be made by this agent or their opponent.
 */
public abstract class Agent<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * Instantiates an agent to autonomously play the Royal Game of Ur.
     */
    public Agent() {}

    /**
     * Gets an identifier that can be used to uniquely identify this type of agent.
     * @return An identifier that can be used to uniquely identify this type of agent.
     */
    public @Nonnull String getIdentifier() {
        throw new UnsupportedOperationException("This agent does not have an identifier (" + getClass() + ")");
    }

    /**
     * Initiates the agent to play their turn in the given game.
     * @param game The game to play a turn in.
     * @param player The player to play the turn as.
     */
    public void playTurn(@Nonnull Game<P, S, R> game, @Nonnull Player player) {
        if (game.isFinished())
            throw new IllegalStateException("The game has already been completed");
        if (!game.isPlayable())
            throw new IllegalStateException("The game is not in a playable state");
        if (game.getTurnPlayer().player != player)
            throw new IllegalStateException("It is not currently the agent's turn in the game");

        // Just roll the dice, there's not usually any decisions to be made here.
        if (game.isWaitingForRoll()) {
            game.rollDice();
            return;
        }

        // Decide the move to be made.
        if (game.isWaitingForMove()) {
            WaitingForMoveGameState<P, S, R> state = game.getCurrentWaitingForMoveState();
            List<Move<P>> moves = game.findAvailableMoves();
            Move<P> chosenMove = decideMove(state, moves);
            game.makeMove(chosenMove);
            return;
        }
        throw new IllegalStateException("The game is in an unexpected state");
    }

    /**
     * Determines the move to be executed from the current state of the game.
     * @param state The current state of the game.
     * @param moves The list of available moves to be chosen from.
     * @return The move that the agent chose to play.
     */
    public abstract @Nonnull Move<P> decideMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state,
            @Nonnull List<Move<P>> moves
    );
}
