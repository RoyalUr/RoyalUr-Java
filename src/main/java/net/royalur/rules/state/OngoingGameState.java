package net.royalur.rules.state;

import net.royalur.model.Board;
import net.royalur.model.PlayerType;
import net.royalur.model.PlayerState;

/**
 * A game state from within a game where a winner has not yet been determined.
 */
public abstract class OngoingGameState extends GameState {

    /**
     * The player who made an action or that should make an action.
     */
    private final PlayerType turn;

    /**
     * Instantiates a game state for an ongoing point in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn The player who made an action or that should make an action.
     */
    public OngoingGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            PlayerType turn
    ) {
        super(board, lightPlayer, darkPlayer);
        this.turn = turn;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    /**
     * Gets the player who can make the next interaction with the game.
     * @return The player who can make the next interaction with the game.
     */
    public PlayerType getTurn() {
        return turn;
    }

    /**
     * Gets the player that is waiting whilst the other player makes the
     * next interaction with the game.
     * @return The player who is waiting for the other player to interact
     *         with the game.
     */
    public PlayerType getWaiting() {
        return turn.getOtherPlayer();
    }

    /**
     * Gets the state of the player that we are waiting on to interact with the game.
     * @return The state of the player that we are waiting on to interact with the game.
     */
    public PlayerState getTurnPlayer() {
        return getPlayerState(getTurn());
    }

    /**
     * Gets the state of the player that is waiting whilst the other player makes the
     * next interaction with the game.
     * @return The state of the player that is waiting for the other player to interact
     *         with the game.
     */
    public PlayerState getWaitingPlayer() {
        return getPlayerState(getWaiting());
    }
}
