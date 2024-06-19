package net.royalur.rules.state;

import net.royalur.model.*;

/**
 * A game state where we are waiting for interactions from a player.
 */
public abstract class PlayableGameState extends OngoingGameState {

    /**
     * Instantiates a game state for an ongoing point in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param timeSinceGameStartMs The time this state was created.
     * @param turn        The player who made an action or that should make an action.
     */
    public PlayableGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            long timeSinceGameStartMs,
            PlayerType turn
    ) {
        super(board, lightPlayer, darkPlayer, timeSinceGameStartMs, turn);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof PlayableGameState other))
            return false;

        return super.equals(other);
    }
}
