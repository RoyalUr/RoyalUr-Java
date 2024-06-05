package net.royalur.model.shape;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The type of board to use in a game.
 */
public enum BoardType implements BoardShapeFactory {

    /**
     * The standard board shape.
     */
    STANDARD("standard", "Standard") {
        @Override
        public BoardShape createBoardShape() {
            return new StandardBoardShape();
        }
    },

    /**
     * The Aseb board shape.
     */
    ASEB("aseb", "Aseb") {
        @Override
        public BoardShape createBoardShape() {
            return new AsebBoardShape();
        }
    };

    /**
     * A store to be used to parse board shapes.
     */
    public static final Map<String, BoardShapeFactory> PARSING_MAP;

    static {
        Map<String, BoardShapeFactory> parsingMap = new HashMap<>();

        // Old names given to board types that we changed.
        parsingMap.put("Standard", BoardType.STANDARD);
        parsingMap.put("Aseb", BoardType.ASEB);

        for (BoardType type : values()) {
            parsingMap.put(type.id, type);
        }
        PARSING_MAP = Collections.unmodifiableMap(parsingMap);
    }

    /**
     * An ID representing this board shape.
     */
    private final String id;

    /**
     * The name of this board shape.
     */
    private final String name;

    /**
     * Instantiates a type of board.
     * @param id   A constant ID representing this board shape.
     * @param name The name of this board shape.
     */
    BoardType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Gets the ID that refers to this board type.
     * @return The ID that refers to this board type.
     */
    public String getID() {
        return id;
    }

    /**
     * Gets the name of this board type.
     * @return The name of this board type.
     */
    public String getName() {
        return name;
    }

    /**
     * Create an instance of the board shape.
     * @return The instance of the board shape.
     */
    public abstract BoardShape createBoardShape();

    /**
     * Get the board type with an ID of {@param id}.
     * @param id The ID of the board type.
     * @return The board type with the given ID.
     */
    public static BoardType getByID(String id) {
        for (BoardType boardType : values()) {
            if (boardType.id.equals(id))
                return boardType;
        }
        throw new IllegalArgumentException("Unknown board type " + id);
    }

    /**
     * Get the board type with an ID of {@param id}, or else {@code null}.
     * @param id The ID of the board type to look for.
     * @return The board type with the given ID, or null.
     */
    public static @Nullable BoardType getByIDOrNull(@Nullable String id) {
        if (id == null)
            return null;

        for (BoardType boardType : values()) {
            if (boardType.id.equals(id))
                return boardType;
        }
        return null;
    }
}
