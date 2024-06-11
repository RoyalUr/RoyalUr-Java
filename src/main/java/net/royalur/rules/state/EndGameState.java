package net.royalur.rules.state;

import net.royalur.model.Board;
import net.royalur.model.PlayerState;
import net.royalur.model.PlayerType;

import javax.annotation.Nullable;

/**
 * A game state where a player has won the game.
 */
public class EndGameState extends GameState {

    /**
     * The player that won the game.
     */
    private final @Nullable PlayerType winner;

    /**
     * Instantiates a game state where a player has won the game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param winner      The winning player, or {@code null} if neither player won.
     */
    public EndGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            @Nullable PlayerType winner
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
     * Gets whether the game ended with a winner.
     * @return Whether the game ended with a winner.
     */
    public boolean hasWinner() {
        return winner != null;
    }

    /**
     * Gets whether the game ended with a loser.
     * @return Whether the game ended with a loser.
     */
    public boolean hasLoser() {
        return winner != null;
    }

    /**
     * Gets the player that won the game.
     * @return The player that won the game.
     */
    public PlayerType getWinner() {
        if (winner == null)
            throw new IllegalStateException("This game ended without a winner");
        return winner;
    }

    /**
     * Gets the player that lost the game.
     * @return The player that lost the game.
     */
    public PlayerType getLoser() {
        if (winner == null)
            throw new IllegalStateException("This game ended without a loser");
        return winner.getOtherPlayer();
    }

    /**
     * Gets the state of the player that won the game.
     * @return The state of the player that won the game.
     */
    public PlayerState getWinningPlayer() {
        return getPlayerState(getWinner());
    }

    /**
     * Gets the state of the player that lost the game.
     * @return The state of the player that lost the game.
     */
    public PlayerState getLosingPlayer() {
        return getPlayerState(getLoser());
    }

    @Override
    public @Nullable PlayerType getSubject() {
        return winner;
    }

    @Override
    public String describe() {
        if (winner == null)
            return "The game ended without a winner";

        return "The " + winner.getName().toLowerCase() + " player has won!";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof EndGameState other))
            return false;

        return super.equals(other) && winner == other.winner;
    }
}
