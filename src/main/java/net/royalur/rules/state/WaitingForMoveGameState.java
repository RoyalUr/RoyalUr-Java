package net.royalur.rules.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A game state where the game is waiting for a player to make a move.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of the roll that was made to get into this game state.
 */
public class WaitingForMoveGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends PlayableGameState<P, S, R> {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    public final @Nonnull R roll;

    /**
     * Instantiates a game state where the game is waiting for a player to make a move.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     * @param roll        The value of the dice that was rolled that can be
     *                    used as the number of places to move a piece.
     */
    public WaitingForMoveGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn,
            @Nonnull R roll) {

        super(GameStateType.WAITING_FOR_MOVE, board, lightPlayer, darkPlayer, turn);
        if (roll.value <= 0) {
            throw new IllegalArgumentException(
                    "The waiting for move game state must have a roll of at least 1, not " + roll.value
            );
        }
        this.roll = roll;
    }

    @Override
    public @Nonnull String describe() {
        return "Waiting for the " + turn.lowerName + "player to make a move with their roll of " + roll + ".";
    }
}
