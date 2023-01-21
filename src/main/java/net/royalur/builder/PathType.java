package net.royalur.builder;

import net.royalur.model.PathPair;
import net.royalur.model.path.*;

import javax.annotation.Nonnull;

/**
 * The type of path to use in a game.
 */
public enum PathType {

    /**
     * The standard path used for Aseb.
     */
    ASEB(AsebPathPair.ID, AsebPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new AsebPathPair();
        }
    },

    /**
     * The path proposed by Bell for the Royal Game of Ur.
     */
    BELL(BellPathPair.ID, BellPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new BellPathPair();
        }
    },

    /**
     * The path proposed by Masters for the Royal Game of Ur.
     */
    MASTERS(MastersPathPair.ID, MastersPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new MastersPathPair();
        }
    },

    /**
     * The path proposed by Murray for the Royal Game of Ur.
     */
    MURRAY(MurrayPathPair.ID, MurrayPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new MurrayPathPair();
        }
    },

    /**
     * The path proposed by Skiriuk for the Royal Game of Ur.
     */
    SKIRIUK(SkiriukPathPair.ID, SkiriukPathPair.class) {
        @Override
        public @Nonnull PathPair create() {
            return new SkiriukPathPair();
        }
    };

    /**
     * The ID of this path.
     */
    public final @Nonnull String id;

    /**
     * The class representing this path.
     */
    public final @Nonnull Class<? extends PathPair> pathClass;

    /**
     * @param id        The ID of this board shape.
     * @param pathClass The class representing this path.
     */
    PathType(@Nonnull String id, @Nonnull Class<? extends PathPair> pathClass) {
        this.id = id;
        this.pathClass = pathClass;
    }

    /**
     * Create an instance of the path.
     * @return The instance of the path.
     */
    public abstract @Nonnull PathPair create();
}
