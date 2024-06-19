package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

/**
 * A game state that represents a move of a piece on the board.
 */
public class MovedGameState extends ActionGameState {

    /**
     * The roll of the dice that was used for the move.
     */
    private final Roll roll;

    /**
     * The move that was made.
     */
    private final Move move;

    /**
     * Instantiates a game state representing a move that was made in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param timeSinceGameStartMs The time this state was created.
     * @param turn        The player who made the move.
     * @param roll        The roll of the dice that was used to move the piece.
     * @param move        The move that was made on the board.
     */
    public MovedGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll,
            Move move
    ) {
        super(board, lightPlayer, darkPlayer, timeSinceGameStartMs, turn);
        this.roll = roll;
        this.move = move;
    }

    /**
     * Gets the roll of the dice that was used for the move.
     * @return The roll of the dice that was used for the move.
     */
    public Roll getRoll() {
        return roll;
    }

    /**
     * Gets the move that was made.
     * @return The move that was made.
     */
    public Move getMove() {
        return move;
    }

    @Override
    public String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("The ");
        builder.append(getTurn().getName().toLowerCase());
        builder.append(" player ");

        boolean introducing = move.isIntroduction();
        boolean scoring = move.isScore();

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof MovedGameState other))
            return false;

        return super.equals(other)
                && roll.equals(other.roll)
                && move.equals(other.move);
    }
}
