package net.royalur.model.identity;

import net.royalur.model.PlayerIdentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A player that is anonymous.
 */
public class AnonymousPlayer extends PlayerIdentity {

    /**
     * The name used for the anonymous player.
     */
    public static final @Nonnull String DISPLAY_NAME = "Anonymous";

    @Override
    public boolean isAnonymous() {
        return true;
    }

    @Override
    public boolean hasName() {
        return false;
    }

    @Override
    public @Nonnull String getName() {
        throw new UnsupportedOperationException("Anonymous players do not have a name");
    }

    @Override
    public @Nonnull String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public int hashCode() {
        return 8175577;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj != null && obj.getClass().equals(getClass());
    }
}
