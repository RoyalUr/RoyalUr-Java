package net.royalur.rules.state;

import net.royalur.model.Board;
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
public abstract class OngoingGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends GameState<P, S, R> {

    /**
     * The player who made an action or that should make an action.
     */
    private final @Nonnull PlayerType turn;

    /**
     * Instantiates a game state for an ongoing point in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn The player who made an action or that should make an action.
     */
    public OngoingGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull PlayerType turn
    ) {
        super(board, lightPlayer, darkPlayer);
        this.turn = turn;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    /**
     * Gets the player who can make the next interaction with the game.
     * @return The player who can make the next interaction with the game.
     */
    public @Nonnull PlayerType getTurn() {
        return turn;
    }

    /**
     * Gets the player that is waiting whilst the other player makes the
     * next interaction with the game.
     * @return The player who is waiting for the other player to interact
     *         with the game.
     */
    public @Nonnull PlayerType getWaiting() {
        return turn.getOtherPlayer();
    }

    /**
     * Gets the state of the player that we are waiting on to interact with the game.
     * @return The state of the player that we are waiting on to interact with the game.
     */
    public @Nonnull S getTurnPlayer() {
        return getPlayer(getTurn());
    }

    /**
     * Gets the state of the player that is waiting whilst the other player makes the
     * next interaction with the game.
     * @return The state of the player that is waiting for the other player to interact
     *         with the game.
     */
    public @Nonnull S getWaitingPlayer() {
        return getPlayer(getWaiting());
    }
}
