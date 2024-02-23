package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;

/**
 * A game state where a player has won the game.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public class WinGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends GameState<P, S, R> {

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
            Board<P> board,
            S lightPlayer,
            S darkPlayer,
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
    public S getWinningPlayer() {
        return getPlayer(getWinner());
    }

    /**
     * Gets the state of the player that lost the game.
     * @return The state of the player that lost the game.
     */
    public S getLosingPlayer() {
        return getPlayer(getLoser());
    }

    @Override
    public String describe() {
        return "The " + winner.getTextName().toLowerCase() + " player has won!";
    }
}
