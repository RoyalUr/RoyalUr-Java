package net.royalur.model.shape;

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
    public static final Map<String, BoardShapeFactory> BY_ID;

    static {
        Map<String, BoardShapeFactory> byID = new HashMap<>();
        for (BoardType type : values()) {
            byID.put(type.id, type);
        }
        BY_ID = Collections.unmodifiableMap(byID);
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
}
