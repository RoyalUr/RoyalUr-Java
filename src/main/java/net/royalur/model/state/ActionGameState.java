package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A game state that is included in the middle of a game to record an action that
 * was taken, but that is not a valid state to be in.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public abstract class ActionGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends OngoingGameState<P, S, R> {

    /**
     * The type of action that was made.
     */
    public final @Nonnull ActionType actionType;

    /**
     * Instantiates a game state that represents an action that was taken.
     * @param actionType  The type of action that was made.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     */
    public ActionGameState(
            @Nonnull ActionType actionType,
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn) {

        super(GameStateType.ACTION, board, lightPlayer, darkPlayer, turn);

        this.actionType = actionType;
    }

    @Override
    public boolean isPlayable() {
        return false;
    }
}
