package net.royalur.model.path;

import net.royalur.notation.name.Name;
import net.royalur.notation.name.NameMap;

import javax.annotation.Nonnull;

/**
 * The type of path to use in a game.
 */
public enum PathType implements Name, PathPairFactory {

    /**
     * The standard path used for Aseb.
     */
    ASEB("Aseb", AsebPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new AsebPathPair();
        }
    },

    /**
     * The path proposed by Bell for the Royal Game of Ur.
     */
    BELL("Bell", BellPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new BellPathPair();
        }
    },

    /**
     * The path proposed by Masters for the Royal Game of Ur.
     */
    MASTERS("Masters", MastersPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new MastersPathPair();
        }
    },

    /**
     * The path proposed by Murray for the Royal Game of Ur.
     */
    MURRAY("Murray", MurrayPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new MurrayPathPair();
        }
    },

    /**
     * The path proposed by Skiriuk for the Royal Game of Ur.
     */
    SKIRIUK("Skiriuk", SkiriukPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new SkiriukPathPair();
        }
    };

    /**
     * A store to be used to parse path pairs.
     */
    public static final @Nonnull NameMap<PathType, PathPairFactory> FACTORIES;
    static {
        NameMap<PathType, PathPairFactory> factories = NameMap.create();
        for (PathType type : values()) {
            factories.put(type, type);
        }
        FACTORIES = factories.unmodifiableCopy();
    }

    /**
     * The name given to this path.
     */
    public final @Nonnull String name;

    /**
     * The class representing this path.
     */
    public final @Nonnull Class<? extends PathPair> pathClass;

    /**
     * @param name      The name given to this path.
     * @param pathClass The class representing this path.
     */
    PathType(@Nonnull String name, @Nonnull Class<? extends PathPair> pathClass) {
        this.name = name;
        this.pathClass = pathClass;
    }

    @Override
    public @Nonnull String getTextName() {
        return name;
    }

    /**
     * Create an instance of the paths.
     * @return The instance of the paths.
     */
    public abstract @Nonnull PathPair create();
}
