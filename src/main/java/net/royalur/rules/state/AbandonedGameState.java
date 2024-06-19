package net.royalur.rules.state;

import net.royalur.model.AbandonReason;
import net.royalur.model.Board;
import net.royalur.model.PlayerState;
import net.royalur.model.PlayerType;

import javax.annotation.Nullable;

/**
 * A game state that represents a game being abandoned.
 */
public class AbandonedGameState extends ControlGameState {

    /**
     * The reason that the game was abandoned.
     */
    private final AbandonReason reason;

    /**
     * Instantiates a game state representing a game being abandoned.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param timeSinceGameStartMs The time this state was created.
     * @param reason      The reason that the game was abandoned.
     * @param player      The player that abandoned the game, or {@code null}
     *                    if the game was not abandoned by a specific player.
     */
    public AbandonedGameState(
            Board board,
            PlayerState lightPlayer,
            PlayerState darkPlayer,
            long timeSinceGameStartMs,
            AbandonReason reason,
            @Nullable PlayerType player
    ) {
        super(board, lightPlayer, darkPlayer, timeSinceGameStartMs, player);
        if (reason.requiresPlayer() && player == null)
            throw new IllegalArgumentException(reason.getName() + " abandonment requires a player");

        this.reason = reason;
    }

    /**
     * Gets the reason that the game was abandoned.
     * @return The reason that the game was abandoned.
     */
    public AbandonReason getReason() {
        return reason;
    }

    @Override
    public String describe() {
        return switch (reason) {
            case PLAYER_LEFT -> "The " + getPlayer().getName().toLowerCase() + " player left the game";
            case EXTERNAL -> "An external event ended the game";
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof AbandonedGameState other))
            return false;

        return super.equals(other) && reason == other.reason;
    }
}
