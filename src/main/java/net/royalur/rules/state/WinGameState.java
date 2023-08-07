package net.royalur.rules.state;

import net.royalur.model.*;

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
> extends AbstractGameState<P, S, R> {

    /**
     * The player that won the game.
     */
    private final @Nonnull PlayerType winner;

    /**
     * The player that lost the game.
     */
    private final @Nonnull PlayerType loser;

    /**
     * Instantiates a game state where a player has won the game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param winner The winning player.
     */
    public WinGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull PlayerType winner
    ) {
        super(board, lightPlayer, darkPlayer);
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
    public S getWinner() {
        return getPlayer(winner);
    }

    /**
     * Retrieves the state of the player that lost the game.
     * @return The state of the player that lost the game.
     */
    public S getLoser() {
        return getPlayer(loser);
    }

    @Override
    public @Nonnull String describe() {
        return "The " + winner.getTextName().toLowerCase() + " player has won!";
    }
}
