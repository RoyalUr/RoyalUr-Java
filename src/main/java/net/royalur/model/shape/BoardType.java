package net.royalur.model.shape;

import net.royalur.model.path.PathType;
import net.royalur.notation.name.Name;
import net.royalur.notation.name.NameMap;

import javax.annotation.Nonnull;

/**
 * The type of board to use in a game.
 */
public enum BoardType implements Name, BoardShapeFactory {

    /**
     * The standard board shape.
     */
    STANDARD("Standard", StandardBoardShape.class) {
        @Override
        public @Nonnull BoardShape create() {
            return new StandardBoardShape();
        }
    },

    /**
     * The Aseb board shape.
     */
    ASEB("Aseb", AsebBoardShape.class) {
        @Override
        public @Nonnull BoardShape create() {
            return new AsebBoardShape();
        }
    };

    /**
     * A store to be used to parse board shapes.
     */
    public static final @Nonnull NameMap<BoardType, BoardShapeFactory> FACTORIES;
    static {
        NameMap<BoardType, BoardShapeFactory> factories = NameMap.create();
        for (BoardType type : values()) {
            factories.put(type, type);
        }
        FACTORIES = factories.unmodifiableCopy();
    }

    /**
     * The name of this board shape.
     */
    public final @Nonnull String name;

    /**
     * The class representing this board shape.
     */
    public final @Nonnull Class<? extends BoardShape> shapeClass;

    /**
     * @param name       The name of this board shape.
     * @param shapeClass The class representing this board shape.
     */
    BoardType(@Nonnull String name, @Nonnull Class<? extends BoardShape> shapeClass) {
        this.name = name;
        this.shapeClass = shapeClass;
    }

    @Override
    public @Nonnull String getTextName() {
        return name;
    }
}
