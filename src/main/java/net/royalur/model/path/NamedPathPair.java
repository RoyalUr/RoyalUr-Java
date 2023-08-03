package net.royalur.model.path;

import net.royalur.model.Tile;
import net.royalur.name.Name;
import net.royalur.name.Named;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A path pair that has an associated name.
 * @param <N> The type of the name of the path pair.
 */
public class NamedPathPair<N extends Name> extends PathPair implements Named<N> {

    /**
     * The name of this path pair.
     */
    private final @Nonnull N name;

    /**
     * Instantiates a pair of paths.
     * @param name  The name of this path.
     * @param light The path that light players take around the board.
     * @param dark  The path that dark players take around the board.
     */
    public NamedPathPair(@Nonnull N name, @Nonnull List<Tile> light, @Nonnull List<Tile> dark) {
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
