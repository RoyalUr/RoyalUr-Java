package net.royalur.rules.state;

import net.royalur.model.*;

/**
 * A game state where a player has won the game.
 */
public class WinGameState extends GameState {

    /**
     * The player that won the game.
     */
    private final PlayerType winner;

    /**
     * Instantiates a game state where a player has won the game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param winner The winning player.
     */
    public WinGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            PlayerType winner
    ) {
        super(board, lightPlayer, darkPlayer);
        this.winner = winner;
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    /**
     * Gets the player that won the game.
     * @return The player that won the game.
     */
    public PlayerType getWinner() {
        return this.winner;
    }

    /**
     * Gets the player that lost the game.
     * @return The player that lost the game.
     */
    public PlayerType getLoser() {
        return this.winner.getOtherPlayer();
    }

    /**
     * Gets the state of the player that won the game.
     * @return The state of the player that won the game.
     */
    public PlayerState getWinningPlayer() {
        return getPlayer(getWinner());
    }

    /**
     * Gets the state of the player that lost the game.
     * @return The state of the player that lost the game.
     */
    public PlayerState getLosingPlayer() {
        return getPlayer(getLoser());
    }

    @Override
    public String describe() {
        return "The " + winner.getTextName().toLowerCase() + " player has won!";
    }
}
