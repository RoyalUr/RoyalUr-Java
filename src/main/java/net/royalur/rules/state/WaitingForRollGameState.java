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
     * @param timeSinceGameStartMs The time this state was created.
     * @param turn        The player who can roll the dice.
     */
    public WaitingForRollGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            long timeSinceGameStartMs,
            PlayerType turn
    ) {
        super(board, lightPlayer, darkPlayer, timeSinceGameStartMs, turn);
    }

    @Override
    public String describe() {
        return "Waiting for the " + getTurn().getName().toLowerCase()
                + " player to roll the dice";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof WaitingForRollGameState other))
            return false;

        return super.equals(other);
    }
}
