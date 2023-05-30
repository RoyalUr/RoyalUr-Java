package net.royalur.model.path;

import javax.annotation.Nonnull;

/**
 * A factory that creates a pair of paths.
 */
public interface PathPairFactory {

    /**
     * Create an instance of the paths.
     * @return The instance of the paths.
     */
    @Nonnull PathPair create();
}
