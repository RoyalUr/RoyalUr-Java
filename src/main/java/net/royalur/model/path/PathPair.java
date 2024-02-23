package net.royalur.model.path;

import net.royalur.model.PlayerType;
import net.royalur.model.Tile;
import net.royalur.name.Name;
import net.royalur.name.Named;
import net.royalur.name.TextName;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a pair of paths for the light and dark players to
 * move their pieces along in a game of the Royal Game of Ur.
 */
public class PathPair implements Named<Name> {

    /**
     * The name of this path pair.
     */
    private final Name name;

    /**
     * The path that light players take around the board, including
     * the start and end tiles that exist off the board.
     */
    private final List<Tile> lightWithStartEnd;

    /**
     * The path that dark players take around the board, including
     * the start and end tiles that exist off the board.
     */
    private final List<Tile> darkWithStartEnd;

    /**
     * The path that light players take around the board, excluding
     * the start and end tiles that exist off the board.
     */
    private final List<Tile> light;

    /**
     * The path that dark players take around the board, excluding
     * the start and end tiles that exist off the board.
     */
    private final List<Tile> dark;

    /**
     * The start tile of the light player that exists off the board.
     */
    private final Tile lightStart;

    /**
     * The end tile of the light player that exists off the board.
     */
    private final Tile lightEnd;

    /**
     * The start tile of the dark player that exists off the board.
     */
    private final Tile darkStart;

    /**
     * The end tile of the dark player that exists off the board.
     */
    private final Tile darkEnd;

    /**
     * Instantiates a pair of paths.
     * @param name The name of this path.
     * @param lightWithStartEnd The path that light players take around
     *                          the board, including the start and end
     *                          tiles that exist off the board.
     * @param darkWithStartEnd The path that dark players take around
     *                         the board, including the start and end
     *                         tiles that exist off the board.
     */
    public PathPair(
            Name name,
            List<Tile> lightWithStartEnd,
            List<Tile> darkWithStartEnd
    ) {
        this.name = name;
        this.lightWithStartEnd = List.copyOf(lightWithStartEnd);
        this.darkWithStartEnd = List.copyOf(darkWithStartEnd);
        this.light = this.lightWithStartEnd.subList(1, lightWithStartEnd.size() - 1);
        this.dark = this.darkWithStartEnd.subList(1, darkWithStartEnd.size() - 1);
        this.lightStart = lightWithStartEnd.get(0);
        this.lightEnd = lightWithStartEnd.get(lightWithStartEnd.size() - 1);
        this.darkStart = darkWithStartEnd.get(0);
        this.darkEnd = darkWithStartEnd.get(darkWithStartEnd.size() - 1);
    }

    @Override
    public Name getName() {
        return name;
    }

    /**
     * Gets the path that the light player's pieces must take, excluding
     * the start and end tiles that exist off the board.
     * @return The path that the light player's pieces must take
     *         on the board.
     */
    public List<Tile> getLight() {
        return light;
    }

    /**
     * Gets the path that the dark player's pieces must take, excluding
     * the start and end tiles that exist off the board.
     * @return The path that the dark player's pieces must take
     *         on the board.
     */
    public List<Tile> getDark() {
        return dark;
    }

    /**
     * Gets the path of {@code player}, excluding the start and
     * end tiles that exist off the board.
     * @param player The player to get the path for.
     * @return The path for the given player.
     */
    public List<Tile> get(PlayerType player) {
        return switch (player) {
            case LIGHT -> getLight();
            case DARK -> getDark();
        };
    }

    /**
     * Gets the path that the light player's pieces must take, including
     * the start and end tiles that exist off the board.
     * @return The path that the light player's pieces must take
     *         on and off the board.
     */
    public List<Tile> getLightWithStartEnd() {
        return lightWithStartEnd;
    }

    /**
     * Gets the path that the dark player's pieces must take, including
     * the start and end tiles that exist off the board.
     * @return The path that the dark player's pieces must take
     *         on and off the board.
     */
    public List<Tile> getDarkWithStartEnd() {
        return darkWithStartEnd;
    }

    /**
     * Gets the path of {@code player}, including the start and
     * end tiles that exist off the board.
     * @param player The player to get the path for.
     * @return The path for the given player.
     */
    public List<Tile> getWithStartEnd(PlayerType player) {
        return switch (player) {
            case LIGHT -> getLightWithStartEnd();
            case DARK -> getDarkWithStartEnd();
        };
    }

    /**
     * Gets the start tile for the light player that exists off the board.
     * @return The start tile for the light player.
     */
    public Tile getLightStart() {
        return lightStart;
    }

    /**
     * Gets the start tile for the dark player that exists off the board.
     * @return The start tile for the dark player.
     */
    public Tile getDarkStart() {
        return darkStart;
    }

    /**
     * Gets the start tile of {@code player}, which exists off the board.
     * @param player The player to get the start tile for.
     * @return The start tile of the given player.
     */
    public Tile getStart(PlayerType player) {
        return switch (player) {
            case LIGHT -> getLightStart();
            case DARK -> getDarkStart();
        };
    }

    /**
     * Gets the end tile for the light player that exists off the board.
     * @return The end tile for the light player.
     */
    public Tile getLightEnd() {
        return lightEnd;
    }

    /**
     * Gets the end tile for the dark player that exists off the board.
     * @return The end tile for the dark player.
     */
    public Tile getDarkEnd() {
        return darkEnd;
    }

    /**
     * Gets the end tile of {@code player} that exists off the board.
     * @param player The player to get the end tile for.
     * @return The end tile of the given player.
     */
    public Tile getEnd(PlayerType player) {
        return switch (player) {
            case LIGHT -> getLightEnd();
            case DARK -> getDarkEnd();
        };
    }

    /**
     * Determines whether the paths that the light player's pieces must take,
     * and the paths that the dark player's pieces must take, are equivalent
     * between this path pair and {@code other}. The start and end tiles that
     * exist off the board may still differ between the paths.
     * @param other The other pair of paths to check for equivalency.
     * @return Whether the paths that the light and dark player's pieces must take
     *         around the board are equivalent for this path pair and {@code other}.
     */
    public boolean isEquivalent(PathPair other) {
        return getLight().equals(other.getLight())
                && getDark().equals(other.getDark());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        PathPair other = (PathPair) obj;
        return name.equals(other.name)
                && getLightWithStartEnd().equals(other.getLightWithStartEnd())
                && getDarkWithStartEnd().equals(other.getDarkWithStartEnd());
    }

    /**
     * Create a new path pair called {@code name} with
     * paths {@code lightPath} and {@code darkPath}.
     * @param name The name of the path pair.
     * @param lightPathWithStartEnd The path for light pieces, including the
     *                              start and end tiles that exist off the board.
     * @param darkPathWithStartEnd The path for dark pieces, including the
     *                             start and end tiles that exist off the board.
     * @return A new path pair with the given name and paths.
     */
    public static PathPair create(
            String name,
            List<Tile> lightPathWithStartEnd,
            List<Tile> darkPathWithStartEnd
    ) {
        return new PathPair(
                new TextName(name),
                lightPathWithStartEnd,
                darkPathWithStartEnd
        );
    }
}
