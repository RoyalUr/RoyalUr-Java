package net.royalur.builder;

import net.royalur.model.BoardShape;
import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.StandardBoardShape;

import javax.annotation.Nonnull;

/**
 * The type of board to use in a game.
 */
public enum BoardType {

    /**
     * The standard board shape.
     */
    STANDARD(StandardBoardShape.ID, StandardBoardShape.class) {
        @Override
        public @Nonnull BoardShape create() {
            return new StandardBoardShape();
        }
    },

    /**
     * The Aseb board shape.
     */
    ASEB(AsebBoardShape.ID, AsebBoardShape.class) {
        @Override
        public @Nonnull BoardShape create() {
            return new AsebBoardShape();
        }
    };

    /**
     * The ID of this board shape.
     */
    public final @Nonnull String id;

    /**
     * The class representing this board shape.
     */
    public final @Nonnull Class<? extends BoardShape> shapeClass;

    /**
     * @param id         The ID of this board shape.
     * @param shapeClass The class representing this board shape.
     */
    BoardType(@Nonnull String id, @Nonnull Class<? extends BoardShape> shapeClass) {
        this.id = id;
        this.shapeClass = shapeClass;
    }

    /**
     * Create an instance of the board shape.
     * @return The instance of the board shape.
     */
    public abstract @Nonnull BoardShape create();
}
