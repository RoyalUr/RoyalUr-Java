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
public interface ActionGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll,
        A extends Name
> extends OngoingGameState<P, S, R> {

    /**
     * Gets the type of action that was taken by the current turn player.
     * @return The type of action that was taken by the current player.
     */
    @Nonnull A getActionType();
}
