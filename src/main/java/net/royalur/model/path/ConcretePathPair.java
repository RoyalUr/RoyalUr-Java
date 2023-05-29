package net.royalur.model.path;

import javax.annotation.Nonnull;

/**
 * A path with a known name, light, and dark path.
 */
public class ConcretePathPair implements PathPair {

    /**
     * The path that light players take around the board.
     */
    private final @Nonnull Path light;

    /**
     * The path that dark players take around the board.
     */
    private final @Nonnull Path dark;

    /**
     * Instantiates the standard paths for the light and dark player in Aseb.
     */
    public ConcretePathPair(@Nonnull Path light, @Nonnull Path dark) {
        this.light = light;
        this.dark = dark;
    }

    @Override
    public @Nonnull Path getLight() {
        return light;
    }

    @Override
    public @Nonnull Path getDark() {
        return dark;
    }
}
