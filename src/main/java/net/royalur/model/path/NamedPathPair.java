package net.royalur.model.path;

import net.royalur.notation.name.Name;
import net.royalur.notation.name.Named;

import javax.annotation.Nonnull;

/**
 * A path pair that has an associated name.
 * @param <N> The type of the name of the path pair.
 */
public class NamedPathPair<N extends Name> extends PathPair implements Named<N> {

    private final @Nonnull N name;

    /**
     * Instantiates a pair of paths.
     * @param name  The name of this path.
     * @param light The path that light players take around the board.
     * @param dark  The path that dark players take around the board.
     */
    public NamedPathPair(@Nonnull N name, @Nonnull Path light, @Nonnull Path dark) {
        super(light, dark);
        this.name = name;
    }

    @Override
    public @Nonnull N getName() {
        return name;
    }

    @Override
    public @Nonnull String getDebugName() {
        return name.getTextName();
    }
}
