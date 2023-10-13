package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.name.Name;

import javax.annotation.Nonnull;

/**
 * A game state that is included in the middle of a game to record an action that
 * was taken, but that is not a valid state to be in.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 * @param <A> The type of name given to this action.
 */
public abstract class ActionGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll,
        A extends Name
> extends OngoingGameState<P, S, R> {

    /**
     * The type of action that this state represents.
     */
    private final @Nonnull A actionType;

    /**
     * Instantiates a game state for an ongoing point in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who made an action or that should make an action.
     * @param actionType  The type of action that this state represents.
     */
    public ActionGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull PlayerType turn,
            @Nonnull A actionType
    ) {
        super(board, lightPlayer, darkPlayer, turn);
        this.actionType = actionType;
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    /**
     * Gets the type of action that was taken by the current turn player.
     * @return The type of action that was taken by the current player.
     */
    public @Nonnull A getActionType() {
        return actionType;
    }
}
