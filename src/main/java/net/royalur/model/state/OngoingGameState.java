package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState that is included while the game is still ongoing.
 */
public abstract class OngoingGameState extends GameState {

    /**
     * The player who can make the next interaction with the game.
     */
    public final @Nonnull Player turn;

    /**
     * @param type The type of this game state, representing its purpose.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can make the next move.
     */
    public OngoingGameState(
            @Nonnull GameStateType type,
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player turn) {

        super(type, board, lightPlayer, darkPlayer);
        this.turn = turn;
    }

    /**
     * Retrieves the state of the player that we are waiting on to interact with the game.
     * @return The state of the player that we are waiting on to interact with the game.
     */
    public @Nonnull PlayerState getTurnPlayer() {
        return getPlayer(turn);
    }
}
