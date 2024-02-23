package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;

/**
 * A game state that is included in the middle of a game to record an action that
 * was taken, but that is not a valid state to be in.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public abstract class ActionGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends OngoingGameState<P, S, R> {

    /**
     * Instantiates a game state for an ongoing point in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who made an action or that should make an action.
     */
    public ActionGameState(
            Board<P> board,
            S lightPlayer,
            S darkPlayer,
            PlayerType turn
    ) {
        super(board, lightPlayer, darkPlayer, turn);
    }

    @Override
    public boolean isPlayable() {
        return false;
    }
}
