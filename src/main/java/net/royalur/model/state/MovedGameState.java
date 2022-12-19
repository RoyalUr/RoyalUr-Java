package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState that represents a roll that resulted in no available moves due to a roll with a value of zero.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of the roll that had the value of zero.
 */
public class MovedGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends ActionGameState<P, S, R> {

    /**
     * The roll of the dice that was used for the move.
     */
    public final @Nonnull R roll;

    /**
     * The move that was made.
     */
    public final @Nonnull Move<P> move;

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     */
    public MovedGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn,
            @Nonnull R roll,
            @Nonnull Move<P> move) {

        super(board, lightPlayer, darkPlayer, turn);

        this.roll = roll;
        this.move = move;
    }

    @Override
    public @Nonnull String describe() {
        return "The player " + getTurnPlayer().name + " rolled " + roll + ", " +
                "and moved their " + move.getSource() + " piece to " + move.getDestination() + ".";
    }
}
