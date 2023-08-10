package net.royalur.model.path;

import net.royalur.model.PlayerType;
import net.royalur.model.Tile;
import net.royalur.name.Name;
import net.royalur.name.Named;
import net.royalur.name.TextName;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pair of paths for the light and dark players to
 * move their pieces along in a game of the Royal Game of Ur.
 */
public class PathPair implements Named<Name> {

    /**
     * The name of this path pair.
     */
    private final @Nonnull Name name;

    /**
     * The path that light players take around the board, including
     * the start and end tiles that exist off the board.
     */
    private final @Nonnull List<Tile> lightWithStartEnd;

    /**
     * The path that dark players take around the board, including
     * the start and end tiles that exist off the board.
     */
    private final @Nonnull List<Tile> darkWithStartEnd;

    /**
     * The path that light players take around the board, excluding
     * the start and end tiles that exist off the board.
     */
    private final @Nonnull List<Tile> light;

    /**
     * The path that dark players take around the board, excluding
     * the start and end tiles that exist off the board.
     */
    private final @Nonnull List<Tile> dark;

    /**
     * The start tile of the light player that exists off the board.
     */
    private final @Nonnull Tile lightStart;

    /**
     * The end tile of the light player that exists off the board.
     */
    private final @Nonnull Tile lightEnd;

    /**
     * The start tile of the dark player that exists off the board.
     */
    private final @Nonnull Tile darkStart;

    /**
     * The end tile of the dark player that exists off the board.
     */
    private final @Nonnull Tile darkEnd;

    /**
     * Instantiates a pair of paths.
     * @param name The name of this path.
     * @param lightWithStartEnd The path that light players take around the board,
     *                          including the start and end tiles that exist off the board.
     * @param darkWithStartEnd The path that dark players take around the board,
     *                         including the start and end tiles that exist off the board.
     */
    public PathPair(
            @Nonnull Name name,
            @Nonnull List<Tile> lightWithStartEnd,
            @Nonnull List<Tile> darkWithStartEnd
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
    public @Nonnull Name getName() {
        return name;
    }

    /**
     * The path that the light player's pieces must take, excluding
     * the start and end tiles that exist off the board.
     */
    public @Nonnull List<Tile> getLight() {
        return light;
    }

    /**
     * The path that the dark player's pieces must take, excluding
     * the start and end tiles that exist off the board.
     */
    public @Nonnull List<Tile> getDark() {
        return dark;
    }

    /**
     * Retrieves the path for the player {@code player}, excluding
     * the start and end tiles that exist off the board.
     * @param player The player to get the path for.
     * @return The path for the given player.
     */
    public @Nonnull List<Tile> get(@Nonnull PlayerType player) {
        return switch (player) {
            case LIGHT -> getLight();
            case DARK -> getDark();
        };
    }

    /**
     * The path that the light player's pieces must take, including
     * the start and end tiles that exist off the board.
     */
    public @Nonnull List<Tile> getLightWithStartEnd() {
        return lightWithStartEnd;
    }

    /**
     * The path that the dark player's pieces must take, including
     * the start and end tiles that exist off the board.
     */
    public @Nonnull List<Tile> getDarkWithStartEnd() {
        return darkWithStartEnd;
    }

    /**
     * Retrieves the path for the player {@code player}, including
     * the start and end tiles that exist off the board.
     * @param player The player to get the path for.
     * @return The path for the given player.
     */
    public @Nonnull List<Tile> getWithStartEnd(@Nonnull PlayerType player) {
        return switch (player) {
            case LIGHT -> getLightWithStartEnd();
            case DARK -> getDarkWithStartEnd();
        };
    }

    /**
     * Retrieves the start tile for the light player that exists off the board.
     * @return The start tile for the light player.
     */
    public @Nonnull Tile getLightStart() {
        return lightStart;
    }

    /**
     * Retrieves the start tile for the dark player that exists off the board.
     * @return The start tile for the dark player.
     */
    public @Nonnull Tile getDarkStart() {
        return darkStart;
    }

    /**
     * Retrieves the start tile that exists off the board
     * for the player {@code player}.
     * @param player The player to get the start tile for.
     * @return The start tile of the given player.
     */
    public @Nonnull Tile getStart(@Nonnull PlayerType player) {
        return switch (player) {
            case LIGHT -> getLightStart();
            case DARK -> getDarkStart();
        };
    }

    /**
     * Retrieves the end tile for the light player that exists off the board.
     * @return The end tile for the light player.
     */
    public @Nonnull Tile getLightEnd() {
        return lightEnd;
    }

    /**
     * Retrieves the end tile for the dark player that exists off the board.
     * @return The end tile for the dark player.
     */
    public @Nonnull Tile getDarkEnd() {
        return darkEnd;
    }

    /**
     * Retrieves the end tile that exists off the board
     * for the player {@code player}.
     * @param player The player to get the end tile for.
     * @return The end tile of the given player.
     */
    public @Nonnull Tile getEnd(@Nonnull PlayerType player) {
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
    public boolean isEquivalent(@Nonnull PathPair other) {
        return getLight().equals(other.getLight())
                && getDark().equals(other.getDark());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        PathPair other = (PathPair) obj;
        return getLightWithStartEnd().equals(other.getLightWithStartEnd())
                && getDarkWithStartEnd().equals(other.getDarkWithStartEnd());
    }

    /**
     * Create a new path pair with the name {@code name} and the
     * paths {@code lightPath} and {@code darkPath}.
     * @param name The name of the path pair.
     * @param lightPath The path for light pieces.
     * @param darkPath The path for dark pieces.
     * @return A new path pair with the given name and paths.
     */
    public static @Nonnull PathPair create(
            @Nonnull String name,
            @Nonnull List<Tile> lightPath,
            @Nonnull List<Tile> darkPath
    ) {
        return new PathPair(new TextName(name), lightPath, darkPath);
    }

    /**
     * Constructs a path from waypoints on the board.
     * @param coordinates The waypoint coordinates. Must be an even list
     *                    ordered following x0, y0, x1, y1, x2, y2, etc.
     * @return A list of tiles following the path between the given waypoints.
     */
    public static @Nonnull List<Tile> createPath(int... coordinates) {
        if (coordinates.length == 0)
            throw new IllegalArgumentException("No coordinates provided");
        if (coordinates.length % 2 != 0)
            throw new IllegalArgumentException("Expected an even number of coordinates");

        Tile current = null;
        List<Tile> path = new ArrayList<>();

        for (int index = 0; index < coordinates.length; index += 2) {
            int x = coordinates[index];
            int y = coordinates[index + 1];
            Tile next = new Tile(x, y);
            if (current != null) {
                while (!current.equals(next)) {
                    current = current.stepTowards(next);
                    path.add(current);
                }
            } else {
                path.add(next);
            }
            current = next;
        }
        return path;
    }
}
