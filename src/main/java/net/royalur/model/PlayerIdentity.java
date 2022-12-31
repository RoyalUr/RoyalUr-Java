package net.royalur.model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Identifying information about a player.
 */
public abstract class PlayerIdentity {

    /**
     * Returns whether this identity is anonymous.
     * @return Whether this identity is anonymous.
     */
    public abstract boolean isAnonymous();

    /**
     * Retrieves whether this identity is named.
     * @return Whether this identity is named.
     */
    public abstract boolean hasName();

    /**
     * Retrieves the name of this identity.
     * @return The name of this identity.
     * @throws UnsupportedOperationException If this identity does not have a name.
     */
    public abstract @Nonnull String getName();

    /**
     * Retrieves a name to display for this player.
     * @return A name to display for this player.
     */
    public abstract @Nonnull String getDisplayName();

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("hashCode has not been implemented for " + getClass());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        throw new UnsupportedOperationException("equals has not been implemented for " + getClass());
    }

    @Override
    public @Nonnull String toString() {
        if (hasName())
            return getName();

        return "<" + getDisplayName() + ">";
    }
}
