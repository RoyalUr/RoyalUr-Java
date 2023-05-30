package net.royalur.model.shape;

import net.royalur.model.Tile;
import net.royalur.model.path.Path;
import net.royalur.model.path.PathPair;
import net.royalur.notation.name.Name;
import net.royalur.notation.name.Named;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * A board shape that has an associated name.
 * @param <N> The type of the name of the board shape.
 */
public class NamedBoardShape<N extends Name> extends BoardShape implements Named<N> {

    /**
     * The name of this board shape.
     */
    private final @Nonnull N name;

    /**
     * Instantiates a board shape with {@code tiles} representing the tiles on the board.
     * @param name         The name of this board shape.
     * @param tiles        The set of tiles that fall within the bounds of this board shape.
     * @param rosetteTiles The set of tiles that represent rosette tiles in this board shape.
     */
    public NamedBoardShape(@Nonnull N name, @Nonnull Set<Tile> tiles, @Nonnull Set<Tile> rosetteTiles) {
        super(tiles, rosetteTiles);
        this.name = name;
    }

    @Override
    public @Nonnull N getName() {
        return name;
    }

    @Override
    public @Nonnull String getDebugName() {
        return name.getTextName();
    }
}
