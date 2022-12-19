package net.royalur.model;

import net.royalur.model.state.ActionGameState;
import net.royalur.model.state.WaitingForMoveGameState;
import net.royalur.model.state.WaitingForRollGameState;
import net.royalur.model.state.WinGameState;

import javax.annotation.Nonnull;

/**
 * Represents the purpose of each game state.
 */
public enum GameStateType {

    /**
     * A state that is included just for information,
     * but which cannot be played from.
     */
    INFO(ActionGameState.class),

    /**
     * A state where a player is yet to make a move.
     */
    WAITING_FOR_MOVE(WaitingForMoveGameState.class),

    /**
     * A state where a player is yet to roll the dice.
     */
    WAITING_FOR_ROLL(WaitingForRollGameState.class),

    /**
     * A state where a player has won.
     */
    WIN(WinGameState.class),

    /**
     * Reserved for custom game states.
     */
    CUSTOM(GameState.class);

    public final @Nonnull Class<? extends GameState<?, ?, ?>> baseClass;

    @SuppressWarnings("unchecked")
    GameStateType(@Nonnull Class<?> baseClass) {
        if (!GameState.class.isAssignableFrom(baseClass))
            throw new IllegalArgumentException("The baseClass must be a subclass of " + GameState.class);

        this.baseClass = (Class<? extends GameState<?, ?, ?>>) baseClass;
    }
}
