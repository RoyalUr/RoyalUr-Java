package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState where we are waiting for interactions from a player.
 */
public abstract class PlayableGameState extends OngoingGameState {

    /**
     * @param type The type of this game state, representing its purpose.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     */
    public PlayableGameState(
            @Nonnull GameStateType type,
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player turn) {

        super(type, board, lightPlayer, darkPlayer, turn);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }
}
