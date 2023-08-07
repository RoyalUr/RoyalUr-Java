package net.royalur.rules.state;

import net.royalur.name.Name;

import javax.annotation.Nonnull;

/**
 * The type of action that occurred in a state,
 * or that is expected in a state.
 */
public enum ActionType implements Name {

    /**
     * A move of a piece.
     */
    MOVE("Move"),

    /**
     * A roll of the dice.
     */
    ROLL("Roll");

    /**
     * The name of this type of action.
     */
    private final @Nonnull String name;

    /**
     * Instantiates a new type of action type.
     * @param name The name of this type.
     */
    ActionType(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public @Nonnull String getTextName() {
        return name;
    }

    @Override
    public boolean hasID() {
        return false;
    }

    @Override
    public int getID() {
        throw new UnsupportedOperationException("No associated ID");
    }
}