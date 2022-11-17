package net.royalur.model.state;

import net.royalur.model.Board;
import net.royalur.model.GameStateType;
import net.royalur.model.Player;
import net.royalur.model.PlayerState;

import javax.annotation.Nonnull;

/**
 * A GameState where we are waiting for a player to roll the dice.
 */
public class WaitingForRollGameState extends PlayableGameState {

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     */
    public WaitingForRollGameState(
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player turn) {

        super(GameStateType.WAITING_FOR_ROLL, board, lightPlayer, darkPlayer, turn);
    }

    @Override
    public @Nonnull String describe() {
        return "Waiting for the player " + getTurnPlayer().name + " to roll the dice.";
    }
}
