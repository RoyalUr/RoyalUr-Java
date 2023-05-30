package net.royalur.rules.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A game state that is included while the game is still ongoing.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public abstract class OngoingGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends AbstractGameState<P, S, R> {

    /**
     * The player who can make the next interaction with the game.
     */
    public final @Nonnull Player turn;

    /**
     * Instantiates a game state that is included while the game is still ongoing.
     * @param type The type of this game state, representing its purpose.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     */
    public OngoingGameState(
            @Nonnull GameStateType type,
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn) {

        super(type, board, lightPlayer, darkPlayer);
        this.turn = turn;
    }

    /**
     * Retrieves the state of the player that we are waiting on to interact with the game.
     * @return The state of the player that we are waiting on to interact with the game.
     */
    public @Nonnull S getTurnPlayer() {
        return getPlayer(turn);
    }

    /**
     * Retrieves the state of the player that is waiting for their own turn.
     * @return The state of the player that is waiting for their own turn.
     */
    public @Nonnull S getWaitingPlayer() {
        return getPlayer(turn.getOtherPlayer());
    }
}
