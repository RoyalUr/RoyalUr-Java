package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.*;
import net.royalur.model.state.WaitingForMoveGameState;
import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An agent that can autonomously play the Royal Game of Ur. Agents are designed to be created
 * for a specific game, and live for the life of that game. This is intended to allow the agents
 * to store state if needed for their AI.
 * @param <P> The type of pieces that this agent can interact with.
 * @param <S> The type of player state that this agent can interact with.
 * @param <R> The type of rolls that may be made by this agent or their opponent.
 */
public abstract class Agent<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The name of this type of agent.
     */
    public final @Nonnull String name;

    /**
     * The game that this agent is to play in.
     */
    public final @Nonnull Game<P, S, R> game;

    /**
     * The rules that this agent is to play by.
     */
    public final @Nonnull RuleSet<P, S, R> rules;

    /**
     * The player that this agent is playing as.
     */
    public final @Nonnull Player player;

    protected Agent(@Nonnull String name, @Nonnull Game<P, S, R> game, @Nonnull Player player) {
        this.name = name;
        this.game = game;
        this.rules = game.rules;
        this.player = player;
    }

    /**
     * Initiates the agent to play their turn in the game.
     */
    public void playTurn() {
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
     */
    public abstract @Nonnull Move<P> decideMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state, @Nonnull List<Move<P>> moves);
}
