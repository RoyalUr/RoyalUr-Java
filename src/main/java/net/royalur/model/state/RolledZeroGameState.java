package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState that represents a roll that resulted in no available moves due to a roll with a value of zero.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of the roll that had the value of zero.
 */
public class RolledZeroGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends InfoGameState<P, S, R> {

    /**
     * The roll of zero that was made.
     */
    public final @Nonnull R roll;

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     */
    public RolledZeroGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn,
            @Nonnull R roll) {

        super(board, lightPlayer, darkPlayer, turn);

        if (roll.value != 0) {
            throw new IllegalArgumentException(
                    "The roll that was made does not have a value of zero. It has a value of " + roll.value
            );
        }
        this.roll = roll;
    }

    @Override
    public @Nonnull String describe() {
        return "The player " + getTurnPlayer().name + " rolled 0, so no pieces can be moved.";
    }
}
