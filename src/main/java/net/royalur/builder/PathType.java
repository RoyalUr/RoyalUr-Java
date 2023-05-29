package net.royalur.builder;

import net.royalur.model.path.PathPair;
import net.royalur.model.path.*;

import javax.annotation.Nonnull;

/**
 * The type of path to use in a game.
 */
public enum PathType {

    /**
     * The standard path used for Aseb.
     */
    ASEB(AsebPathPair.NAME, AsebPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new AsebPathPair();
        }
    },

    /**
     * The path proposed by Bell for the Royal Game of Ur.
     */
    BELL(BellPathPair.NAME, BellPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new BellPathPair();
        }
    },

    /**
     * The path proposed by Masters for the Royal Game of Ur.
     */
    MASTERS(MastersPathPair.NAME, MastersPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new MastersPathPair();
        }
    },

    /**
     * The path proposed by Murray for the Royal Game of Ur.
     */
    MURRAY(MurrayPathPair.NAME, MurrayPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new MurrayPathPair();
        }
    },

    /**
     * The path proposed by Skiriuk for the Royal Game of Ur.
     */
    SKIRIUK(SkiriukPathPair.NAME, SkiriukPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new SkiriukPathPair();
        }
    };

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

    /**
     * Create an instance of the path.
     * @return The instance of the path.
     */
    public abstract @Nonnull PathPair create();
}
