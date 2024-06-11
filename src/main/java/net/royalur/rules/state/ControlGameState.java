package net.royalur.rules.state;

import net.royalur.model.Board;
import net.royalur.model.PlayerState;
import net.royalur.model.PlayerType;

import javax.annotation.Nullable;

/**
 * A game state that records a control action that was made.
 */
public abstract class ControlGameState extends GameState {

    /**
     * The player that performed the control action.
     */
    private final @Nullable PlayerType player;

    /**
     * Instantiates a game state for a control action.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param player      The player that performed the control action.
     */
    public ControlGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            @Nullable PlayerType player
    ) {
        super(board, lightPlayer, darkPlayer);
        this.player = player;
    }

    /**
     * Instantiates a game state for a control action.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     */
    public ControlGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer
    ) {
        this(board, lightPlayer, darkPlayer, null);
    }

    @Override
    public @Nullable PlayerType getSubject() {
        return player;
    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    /**
     * Gets whether this control state has an associated player.
     * @return Whether this control state has an associated player.
     */
    public boolean hasPlayer() {
        return player != null;
    }

    /**
     * Gets the player that performed the control action.
     * @return The player that performed the control action.
     */
    public PlayerType getPlayer() {
        if (player == null)
            throw new IllegalStateException("This control state does not have an associated player");
        return player;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof ControlGameState other))
            return false;

        return super.equals(other) && player == other.player;
    }
}
