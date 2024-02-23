package net.royalur.model.path;

import net.royalur.name.Name;
import net.royalur.name.NameMap;
import net.royalur.name.UniqueNameMap;

/**
 * The type of path to use in a game.
 */
public enum PathType implements Name, PathPairFactory {

    /**
     * The path proposed by Bell for the Royal Game of Ur.
     */
    BELL(1, "Bell") {
        @Override
        public PathPair createPathPair() {
            return new BellPathPair();
        }
    },

    /**
     * The standard path used for Aseb.
     */
    ASEB(2, "Aseb") {
        @Override
        public PathPair createPathPair() {
            return new AsebPathPair();
        }
    },

    /**
     * The path proposed by Masters for the Royal Game of Ur.
     */
    MASTERS(3, "Masters") {
        @Override
        public PathPair createPathPair() {
            return new MastersPathPair();
        }
    },

    /**
     * The path proposed by Murray for the Royal Game of Ur.
     */
    MURRAY(4, "Murray") {
        @Override
        public PathPair createPathPair() {
            return new MurrayPathPair();
        }
    },

    /**
     * The path proposed by Skiriuk for the Royal Game of Ur.
     */
    SKIRIUK(5, "Skiriuk") {
        @Override
        public PathPair createPathPair() {
            return new SkiriukPathPair();
        }
    };

    /**
     * A store to be used to parse path pairs.
     */
    public static final NameMap<PathType, PathPairFactory> FACTORIES;
    static {
        NameMap<PathType, PathPairFactory> factories = new UniqueNameMap<>();
        for (PathType type : values()) {
            factories.put(type, type);
        }
        FACTORIES = factories.unmodifiableCopy();
    }

    /**
     * A constant numerical ID representing the path.
     * This ID will never change.
     */
    private final int id;

    /**
     * The name given to this path.
     */
    private final String name;

    /**
     * Instantiates a type of path.
     * @param id   A fixed numerical identifier to represent this path.
     * @param name The name given to this path.
     */
    PathType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Name getName() {
        return this;
    }

    @Override
    public String getTextName() {
        return name;
    }

    @Override
    public boolean hasID() {
        return true;
    }

    @Override
    public int getID() {
        return id;
    }

    /**
     * Create an instance of the paths.
     * @return The instance of the paths.
     */
    public abstract PathPair createPathPair();
}
