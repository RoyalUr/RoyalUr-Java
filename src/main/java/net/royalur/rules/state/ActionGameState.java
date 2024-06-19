package net.royalur.rules.state;

import net.royalur.model.*;

/**
 * A game state that is included in the middle of a game to record an action that
 * was taken, but that is not a valid state to be in.
 */
public abstract class ActionGameState extends OngoingGameState {

    /**
     * Instantiates a game state for an ongoing point in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param timeSinceGameStartMs The time this state was created.
     * @param turn        The player who made an action or that should make an action.
     */
    public ActionGameState(
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
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ActionGameState other))
            return false;

        return super.equals(other);
    }
}
