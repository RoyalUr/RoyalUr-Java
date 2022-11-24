package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState that is included in the middle of a game to add information,
 * but that is not a valid state to be in.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public abstract class InfoGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends OngoingGameState<P, S, R> {

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     */
    public InfoGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn) {

        super(GameStateType.INFO, board, lightPlayer, darkPlayer, turn);
    }

    @Override
    public boolean isPlayable() {
        return false;
    }
}
