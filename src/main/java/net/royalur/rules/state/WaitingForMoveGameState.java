package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import java.util.List;

/**
 * A game state where the game is waiting for a player to make a move.
 */
public class WaitingForMoveGameState extends PlayableGameState {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    private final Roll roll;

    /**
     * The moves that are available to be made from this position.
     */
    private final List<Move> availableMoves;

    /**
     * Instantiates a game state where the game is waiting for a player to make a move.
     * @param board          The state of the pieces on the board.
     * @param lightPlayer    The state of the light player.
     * @param darkPlayer     The state of the dark player.
     * @param turn           The player who can make the next move.
     * @param roll           The value of the dice that was rolled that can be
     *                       used as the number of places to move a piece.
     * @param availableMoves The moves that are available to be made from this position.
     */
    public WaitingForMoveGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            PlayerType turn,
            Roll roll,
            List<Move> availableMoves
    ) {
        super(board, lightPlayer, darkPlayer, turn);
        if (availableMoves.isEmpty())
            throw new IllegalArgumentException("There must be available moves for a waiting for move state");

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
     * Gets the moves that are available to be made from this position.
     * @return The moves that are available to be made from this position.
     */
    public List<Move> getAvailableMoves() {
        return availableMoves;
    }

    @Override
    public String describe() {
        return "Waiting for the " + getTurn().getName().toLowerCase()
                + " player to make a move with their roll of " + roll.value();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof WaitingForMoveGameState other))
            return false;

        return super.equals(other)
                && roll.equals(other.roll)
                && availableMoves.equals(other.availableMoves);
    }
}
