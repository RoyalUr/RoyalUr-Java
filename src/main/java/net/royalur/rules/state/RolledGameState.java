package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import java.util.List;

/**
 * A game state that represents a roll that was made in a game.
 */
public class RolledGameState extends ActionGameState {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    private final Roll roll;

    /**
     * The moves that are available from this position using the given roll.
     */
    private final List<Move> availableMoves;

    /**
     * Instantiates a game state that represents a roll that was made in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     * @param roll        The value of the dice that was rolled that can be
     *                    used as the number of places to move a piece.
     * @param availableMoves The moves that are available from this position
     *                       using the given roll.
     */
    public RolledGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            PlayerType turn,
            Roll roll,
            List<Move> availableMoves
    ) {
        super(board, lightPlayer, darkPlayer, turn);
        this.roll = roll;
        this.availableMoves = List.copyOf(availableMoves);
    }

    /**
     * Gets the roll that the player made.
     * @return The roll that the player made.
     */
    public Roll getRoll() {
        return roll;
    }

    /**
     * Gets the moves that are available from this position
     * using the given roll.
     * @return The moves that are available from
     *         this position using the given roll.
     */
    public List<Move> getAvailableMoves() {
        return this.availableMoves;
    }

    @Override
    public String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("The ")
                .append(getTurn().getName().toLowerCase())
                .append(" player rolled ")
                .append(roll.value());

        if (availableMoves.isEmpty()) {
            if (roll.value() == 0) {
                builder.append(", and had no moves");
            } else {
                builder.append(", and all moves were blocked");
            }
        }
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof RolledGameState other))
            return false;

        return super.equals(other) && roll.equals(other.roll);
    }
}
