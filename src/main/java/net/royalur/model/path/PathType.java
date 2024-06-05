package net.royalur.model.path;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The type of path to use in a game.
 */
public enum PathType implements PathPairFactory {

    /**
     * The path proposed by Bell for the Royal Game of Ur.
     */
    BELL("bell", "Bell") {
        @Override
        public PathPair createPathPair() {
            return new BellPathPair();
        }
    },

    /**
     * The standard path used for Aseb.
     */
    ASEB("aseb", "Aseb") {
        @Override
        public PathPair createPathPair() {
            return new AsebPathPair();
        }
    },

    /**
     * The path proposed by Masters for the Royal Game of Ur.
     */
    MASTERS("masters", "Masters") {
        @Override
        public PathPair createPathPair() {
            return new MastersPathPair();
        }
    },

    /**
     * The path proposed by Murray for the Royal Game of Ur.
     */
    MURRAY("murray", "Murray") {
        @Override
        public PathPair createPathPair() {
            return new MurrayPathPair();
        }
    },

    /**
     * The path proposed by Skiriuk for the Royal Game of Ur.
     */
    SKIRIUK("skiriuk", "Skiriuk") {
        @Override
        public PathPair createPathPair() {
            return new SkiriukPathPair();
        }
    };

    /**
     * A store to be used to parse path pairs.
     */
    public static final Map<String, PathPairFactory> BY_ID;

    static {
        Map<String, PathPairFactory> byID = new HashMap<>();
        for (PathType type : values()) {
            byID.put(type.id, type);
        }
        BY_ID = Collections.unmodifiableMap(byID);
    }

    /**
     * An ID to refer to this path type.
     */
    private final String id;

    /**
     * The name given to this path.
     */
    private final String name;

    /**
     * Instantiates a type of path.
     * @param id   A fixed numerical identifier to represent this path.
     * @param name The name given to this path.
     */
    PathType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the ID that refers to this path type.
     * @return The ID that refers to this path type.
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the name of this path type.
     * @return The name of this path type.
     */
    public String getName() {
        return name;
    }

    /**
     * Create an instance of the paths.
     * @return The instance of the paths.
     */
    public abstract PathPair createPathPair();
}
