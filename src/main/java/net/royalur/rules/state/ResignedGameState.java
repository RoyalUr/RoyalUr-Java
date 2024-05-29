package net.royalur.rules.state;

import net.royalur.model.Board;
import net.royalur.model.PlayerState;
import net.royalur.model.PlayerType;

/**
 * A game state that represents a player resigning from a game.
 */
public class ResignedGameState extends ControlGameState {

    /**
     * Instantiates a game state representing a player resigning from a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param player      The player who resigned the game.
     */
    public ResignedGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            PlayerType player
    ) {
        super(board, lightPlayer, darkPlayer, player);
    }

    @Override
    public String describe() {
        return "The " + getPlayer().getTextName().toLowerCase() + " player resigned";
    }
}
