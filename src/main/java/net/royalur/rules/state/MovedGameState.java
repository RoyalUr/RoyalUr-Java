package net.royalur.rules.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A game state that represents a roll that resulted in no available moves due to a roll with a value of zero.
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
     * Instantiates a game state representing a move that was made in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who made the move.
     * @param roll        The roll of the dice that was used to move the piece.
     * @param move        The move that was made on the board.
     */
    public MovedGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn,
            @Nonnull R roll,
            @Nonnull Move<P> move) {

        super(ActionType.MOVE, board, lightPlayer, darkPlayer, turn);

        this.roll = roll;
        this.move = move;
    }

    @Override
    public @Nonnull String describe() {
        return "The " + turn.lowerName + " player rolled " + roll + ", " +
                "and moved their " + move.getSource() + " piece to " + move.getDestination() + ".";
    }
}
