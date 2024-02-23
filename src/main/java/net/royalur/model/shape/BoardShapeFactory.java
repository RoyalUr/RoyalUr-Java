package net.royalur.model.shape;

import net.royalur.name.Name;
import net.royalur.name.Named;

import javax.annotation.Nonnull;

/**
 * A factory that creates a board shape.
 */
public interface BoardShapeFactory extends Named<Name> {

    /**
     * Create an instance of the board shape.
     * @return The instance of the board shape.
     */
    BoardShape createBoardShape();
}
