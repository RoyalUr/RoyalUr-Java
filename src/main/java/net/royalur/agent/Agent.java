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
public interface Agent<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * Initiates the agent to play their turn in the given game.
     * @param game The game to play a turn in.
     * @param player The player to play the turn as.
     */
    void playTurn(@Nonnull Game<P, S, R> game, @Nonnull Player player);

    /**
     * Completes this game using the two agents to play its moves.
     * @param game The game to complete.
     * @param light The agent to play as the light player.
     * @param dark The agent to play as the dark player.
     * @param <P> The type of pieces that are stored on the board.
     * @param <S> The type of state that is stored for each player.
     * @param <R> The type of rolls that may be made.
     * @return The number of actions that were made by both agents combined. Includes rolls of the dice and moves.
     */
    static <P extends Piece, S extends PlayerState, R extends Roll> int
    playAutonomously(@Nonnull Game<P, S, R> game, @Nonnull Agent<P, S, R> light, @Nonnull Agent<P, S, R> dark) {

        int actions = 0;
        while (!game.isFinished()) {
            if (!game.isPlayable()) {
                throw new IllegalStateException(
                        "Encountered an unplayable state that is not the end of the game: " +
                                game.getCurrentState().getClass().getSimpleName()
                );
            }

            actions += 1;
            S turnPlayer = game.getTurnPlayer();
            switch (turnPlayer.player) {
                case LIGHT -> light.playTurn(game, Player.LIGHT);
                case DARK -> dark.playTurn(game, Player.DARK);
                default -> throw new IllegalStateException("Unknown player " + turnPlayer.player);
            }
        }
        return actions;
    }
}
