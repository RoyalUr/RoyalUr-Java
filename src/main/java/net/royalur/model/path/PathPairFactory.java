package net.royalur.model.path;

import net.royalur.name.Name;
import net.royalur.name.Named;

/**
 * A factory that creates a pair of paths.
 */
public interface PathPairFactory extends Named<Name> {

    /**
     * Create an instance of the paths.
     * @return The instance of the paths.
     */
    PathPair createPathPair();
}
