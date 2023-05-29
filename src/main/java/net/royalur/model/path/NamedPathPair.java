package net.royalur.model.path;

import javax.annotation.Nonnull;

/**
 * A path pair that is named.
 */
public interface NamedPathPair extends PathPair {

    /**
     * Gets the name given to this path pair.
     * @return The name given to this path pair.
     */
    public @Nonnull String getName();
}
