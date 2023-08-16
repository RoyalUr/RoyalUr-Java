package net.royalur.rules.state;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;

/**
 * A game state from within a game where a winner has not yet been determined.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public interface OngoingGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends GameState<P, S, R> {

    /**
     * Gets the player who can make the next interaction with the game.
     * @return The player who can make the next interaction with the game.
     */
    @Nonnull
    PlayerType getTurn();

    /**
     * Retrieves the state of the player that we are waiting on to interact with the game.
     * @return The state of the player that we are waiting on to interact with the game.
     */
    default @Nonnull S getTurnPlayer() {
        return getPlayer(getTurn());
    }

    /**
     * Retrieves the state of the player that is waiting for their own turn.
     * @return The state of the player that is waiting for their own turn.
     */
    default @Nonnull S getWaitingPlayer() {
        return getPlayer(getTurn().getOtherPlayer());
    }
}
