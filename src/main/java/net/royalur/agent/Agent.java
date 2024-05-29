package net.royalur.agent;

import net.royalur.Game;
import net.royalur.model.PlayerState;

/**
 * An agent that can autonomously play the Royal Game of Ur. Agents are designed
 * to be used on any game, and should not hold game-specific state.
 */
public interface Agent {

    /**
     * Initiates the agent to play their turn in the given game.
     * @param game The game to play a turn in.
     */
    void playTurn(Game game);

    /**
     * Completes this game using the two agents to play its moves.
     * @param game The game to complete.
     * @param light The agent to play as the light player.
     * @param dark The agent to play as the dark player.
     * @return The number of actions that were made by both agents combined.
     *         Includes rolls of the dice and moves.
     */
    static int playAutonomously(Game game, Agent light, Agent dark) {
        int actions = 0;
        while (!game.isFinished()) {
            if (!game.isPlayable()) {
                throw new IllegalStateException(
                        "Encountered an unplayable state that is not the end of the game: "
                                + game.getState().getClass().getSimpleName()
                );
            }

            actions += 1;
            PlayerState turnPlayer = game.getTurnPlayer();
            switch (turnPlayer.getPlayer()) {
                case LIGHT -> light.playTurn(game);
                case DARK -> dark.playTurn(game);
            }
        }
        return actions;
    }
}
