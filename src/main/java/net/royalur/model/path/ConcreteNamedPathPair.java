package net.royalur.model.path;

import javax.annotation.Nonnull;

/**
 * A path with a known name, light, and dark path.
 */
public class ConcreteNamedPathPair extends ConcretePathPair implements NamedPathPair {

    /**
     * The name associated with this path pair.
     */
    private final @Nonnull String name;

    /**
     * Instantiates the standard paths for the light and dark player in Aseb.
     */
    public ConcreteNamedPathPair(@Nonnull String name, @Nonnull Path light, @Nonnull Path dark) {
        super(light, dark);
        this.name = name;
    }

    @Override
    public @Nonnull String getName() {
        return name;
    }
}
