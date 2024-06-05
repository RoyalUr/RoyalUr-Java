package net.royalur.model.shape;

/**
 * A factory that creates a board shape.
 */
public interface BoardShapeFactory {

    /**
     * Create an instance of the board shape.
     * @return The instance of the board shape.
     */
    BoardShape createBoardShape();
}
