package net.royalur.model.path;

import net.royalur.model.*;

import java.util.List;

/**
 * The standard paths that are used for Aseb.
 * <p>
 * Citation: W. Crist, A.E. Dunn-Vaturi, and A. de Voogt,
 * Ancient Egyptians at Play: Board Games Across Borders,
 * Bloomsbury Egyptology, Bloomsbury Academic, London, 2016.
 */
public class AsebPathPair extends PathPair {

    /**
     * The path of the light player's pieces.
     */
    public static final List<Tile> LIGHT_PATH = List.copyOf(Tile.createPath(
            1, 5,
            1, 1,
            2, 1,
            2, 12,
            1, 12
    ));

    /**
     * The path of the dark player's pieces.
     */
    public static final List<Tile> DARK_PATH = List.copyOf(Tile.createPath(
            3, 5,
            3, 1,
            2, 1,
            2, 12,
            3, 12
    ));

    /**
     * Instantiates the standard paths for the light and dark player in Aseb.
     */
    public AsebPathPair() {
        super(PathType.ASEB, LIGHT_PATH, DARK_PATH);
    }
}
