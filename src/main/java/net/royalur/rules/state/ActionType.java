package net.royalur.rules.state;

import net.royalur.model.GameState;

import javax.annotation.Nonnull;

public enum ActionType {

    /**
     * A move of a piece.
     */
    MOVE(MovedGameState.class),

    /**
     * A roll of the dice.
     */
    ROLL(RolledGameState.class),

    /**
     * Reserved for custom actions that may be
     * used by custom rule sets.
     */
    CUSTOM(ActionGameState.class);

    /**
     * The base class of states of this type.
     */
    public final @Nonnull Class<? extends GameState<?, ?, ?>> baseClass;

    /**
     * Instantiates a new type of game state.
     * @param baseClass The base class of states of this type.
     */
    @SuppressWarnings("unchecked")
    ActionType(@Nonnull Class<? extends GameState> baseClass) {
        this.baseClass = (Class<? extends GameState<?, ?, ?>>) baseClass;
    }
}
