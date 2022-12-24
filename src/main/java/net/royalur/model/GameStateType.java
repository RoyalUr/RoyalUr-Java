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
     * A state that is included to record the historical
     * actions that have been taken in a game.
     */
    ACTION(ActionGameState.class),

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
     * Reserved for custom game states that may be
     * used by custom rule sets.
     */
    CUSTOM(GameState.class);

    /**
     * The base class of states of this type.
     */
    public final @Nonnull Class<? extends GameState<?, ?, ?>> baseClass;

    /**
     * Instantiates a new type of game state.
     * @param baseClass The base class of states of this type.
     */
    @SuppressWarnings("unchecked")
    GameStateType(@Nonnull Class<? extends GameState> baseClass) {
        this.baseClass = (Class<? extends GameState<?, ?, ?>>) baseClass;
    }
}
