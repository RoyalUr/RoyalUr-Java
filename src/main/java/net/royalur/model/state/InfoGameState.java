package net.royalur.model.state;

import net.royalur.model.Board;
import net.royalur.model.GameStateType;
import net.royalur.model.Player;
import net.royalur.model.PlayerState;

import javax.annotation.Nonnull;

/**
 * A GameState that is included in the middle of a game to add information,
 * but that is not a valid state to be in.
 */
public abstract class InfoGameState extends OngoingGameState {

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     */
    public InfoGameState(
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player turn) {

        super(GameStateType.INFO, board, lightPlayer, darkPlayer, turn);
    }

    @Override
    public boolean isPlayable() {
        return false;
    }
}
