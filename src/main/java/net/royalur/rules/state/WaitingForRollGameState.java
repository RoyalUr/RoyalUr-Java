package net.royalur.rules.state;

import net.royalur.model.*;

/**
 * A game state where the game is waiting for a player to roll the dice.
 */
public class WaitingForRollGameState extends PlayableGameState {

    /**
     * Instantiates a game state where the game is waiting for a player to roll the dice.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     */
    public WaitingForRollGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            PlayerType turn
    ) {
        super(board, lightPlayer, darkPlayer, turn);
    }

    @Override
    public String describe() {
        return "Waiting for the " + getTurn().getTextName().toLowerCase() +
                " player to roll the dice";
    }
}
