package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState where a player has won.
 */
public class WinGameState extends GameState {

    /**
     * The player that won the game.
     */
    public final @Nonnull Player winner;

    /**
     * The player that lost the game.
     */
    public final @Nonnull Player loser;

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param winner The winning player.
     */
    public WinGameState(
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player winner) {

        super(GameStateType.WIN, board, lightPlayer, darkPlayer);
        this.winner = winner;
        this.loser = winner.getOtherPlayer();
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    /**
     * Retrieves the state of the player that won the game.
     * @return The state of the player that won the game.
     */
    public PlayerState getWinner() {
        return getPlayer(winner);
    }

    /**
     * Retrieves the state of the player that lost the game.
     * @return The state of the player that lost the game.
     */
    public PlayerState getLoser() {
        return getPlayer(loser);
    }

    @Override
    public @Nonnull String describe() {
        return "The player " + getWinner().name + " has won!";
    }
}
