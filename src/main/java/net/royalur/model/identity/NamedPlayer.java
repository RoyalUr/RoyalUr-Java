package net.royalur.model.identity;

import net.royalur.model.PlayerIdentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A player that has a name.
 */
public class NamedPlayer extends PlayerIdentity {

    /**
     * The name of the player.
     */
    public final @Nonnull String name;

    /**
     * Instantiates a named player identity.
     * @param name The name of the player.
     */
    public NamedPlayer(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public boolean hasName() {
        return true;
    }

    @Override
    public @Nonnull String getName() {
        return name;
    }

    @Override
    public @Nonnull String getDisplayName() {
        return name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !obj.getClass().equals(getClass()))
            return false;

        NamedPlayer other = (NamedPlayer) obj;
        return name.equals(other.name);
    }
}
