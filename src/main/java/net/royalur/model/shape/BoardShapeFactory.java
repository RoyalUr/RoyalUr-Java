package net.royalur.model.shape;

import net.royalur.model.path.PathPair;

import javax.annotation.Nonnull;

/**
 * A factory that creates a board shape.
 */
public interface BoardShapeFactory {

    /**
     * Create an instance of the board shape.
     * @return The instance of the board shape.
     */
    @Nonnull BoardShape create();
}
