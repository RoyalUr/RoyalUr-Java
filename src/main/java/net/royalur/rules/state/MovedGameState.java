package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;

/**
 * A game state that represents a move of a piece on the board.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of the roll that was made.
 */
public class MovedGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends ActionGameState<P, S, R> {

    /**
     * The roll of the dice that was used for the move.
     */
    private final R roll;

    /**
     * The move that was made.
     */
    private final Move<P> move;

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
            Board<P> board,
            S lightPlayer,
            S darkPlayer,
            PlayerType turn,
            R roll,
            Move<P> move
    ) {
        super(board, lightPlayer, darkPlayer, turn);
        this.roll = roll;
        this.move = move;
    }

    /**
     * Gets the roll of the dice that was used for the move.
     * @return The roll of the dice that was used for the move.
     */
    public R getRoll() {
        return roll;
    }

    /**
     * Gets the move that was made.
     * @return The move that was made.
     */
    public Move<P> getMove() {
        return move;
    }

    @Override
    public String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("The ");
        builder.append(getTurn().getTextName().toLowerCase());
        builder.append(" player ");

        boolean introducing = move.isIntroducingPiece();
        boolean scoring = move.isScoringPiece();

        if (introducing && scoring) {
            builder.append("scored a newly introduced piece.");
        } else if (scoring) {
            builder.append("scored their ")
                    .append(move.getSource());
        } else if (introducing) {
            builder.append("introduced a piece to ")
                    .append(move.getDest());
        } else {
            builder.append("moved their ")
                    .append(move.getSource())
                    .append(" piece to ")
                    .append(move.isCapture() ? "capture " : "")
                    .append(move.getDest());
        }
        return builder.toString();
    }
}
