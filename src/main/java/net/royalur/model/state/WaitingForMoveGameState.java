package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState where we are waiting for a player to make a move.
 */
public class WaitingForMoveGameState extends PlayableGameState {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    public final @Nonnull Roll roll;

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     * @param roll        The value of the dice that was rolled that can be
     *                    used as the number of places to move a piece.
     */
    public WaitingForMoveGameState(
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player turn,
            @Nonnull Roll roll) {

        super(GameStateType.WAITING_FOR_MOVE, board, lightPlayer, darkPlayer, turn);
        this.roll = roll;
    }

    @Override
    public @Nonnull String describe() {
        return "Waiting for the player " + getTurnPlayer().name + " to make a move with their roll of " + roll + ".";
    }
}
