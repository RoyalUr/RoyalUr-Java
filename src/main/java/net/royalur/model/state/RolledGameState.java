package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A game state that represents a roll that was made in a game.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of roll that was made in this game state.
 */
public class RolledGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends ActionGameState<P, S, R> {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    public final @Nonnull R roll;

    /**
     * Instantiates a game state that represents a roll that was made in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     * @param roll        The value of the dice that was rolled that can be
     *                    used as the number of places to move a piece.
     */
    public RolledGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn,
            @Nonnull R roll) {

        super(board, lightPlayer, darkPlayer, turn);

        this.roll = roll;
    }

    @Override
    public @Nonnull String describe() {
        return "The player " + getTurnPlayer().name + " rolled " + roll +".";
    }
}
