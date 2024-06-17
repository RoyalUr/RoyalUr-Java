package net.royalur.model;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Stores the metadata of games, such as the date the game was played,
 * the players of the game, and the game rules.
 */
public class GameMetadata {

    /**
     * The format used for parsing and serialising date times to text.
     */
    public static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ISO_DATE;

    /**
     * The key for storing when a game started.
     */
    public static final String START_DATETIME_KEY = "StartTime";

    /**
     * The key for storing when a game was finished.
     */
    public static final String END_DATETIME_KEY = "EndTime";

    /**
     * The key for storing the time control of a game.
     */
    public static final String TIME_CONTROL_KEY = "TimeControl";

    /**
     * Arbitrary metadata about a game.
     */
    private final Map<String, String> metadata;

    /**
     * Instantiates metadata for a game of the Royal Game of Ur.
     * @param metadata The metadata of the game.
     */
    public GameMetadata(Map<String, String> metadata) {
        this.metadata = new LinkedHashMap<>(metadata);
    }

    /**
     * Instantiates an empty metadata for a game.
     */
    public GameMetadata() {
        this(Collections.emptyMap());
    }

    /**
     * Creates a copy of this metadata.
     * @return A copy of this metadata.
     */
    public GameMetadata copy() {
        return new GameMetadata(metadata);
    }

    /**
     * Retrieves a copy of all the metadata stored.
     * @return A copy of all the metadata stored.
     */
    public Map<String, String> getAll() {
        return Map.copyOf(metadata);
    }

    /**
     * Get the metadata value associated with {@code key}, or {@code null}
     * if there is no value associated with the key.
     * @param key The metadata key to retrieve.
     * @return The metadata value associated with {@code key}, or else {@code null}.
     */
    public @Nullable String get(String key) {
        return metadata.get(key);
    }

    /**
     * Removes any metadata value associated with {@code key}.
     * @param key The metadata key to remove.
     */
    public void remove(String key) {
        metadata.remove(key);
    }

    /**
     * Removes all metadata.
     */
    public void clear() {
        metadata.clear();
    }

    /**
     * Add a new metadata value, {@code value}, associated with {@code key}.
     * @param key The metadata key to set the value for.
     * @param value The value to associate with the given key.
     */
    public void put(String key, String value) {
        metadata.put(key, value);
    }

    /**
     * Sets the date and time when this game began.
     * @param datetime The date and time when this game began.
     */
    public void setStartTime(TemporalAccessor datetime) {
        String formatted = DATETIME_FORMATTER.format(datetime);
        put(START_DATETIME_KEY, formatted);
    }

    /**
     * Gets the date and time when this game began, or {@code null}
     * if no end time is included in this game's metadata.
     * @return The date and time when this game began,
     *         or else {@code null}.
     */
    public @Nullable TemporalAccessor getStartTime() {
        String formatted = get(START_DATETIME_KEY);
        if (formatted == null)
            return null;

        return DATETIME_FORMATTER.parse(formatted);
    }

    /**
     * Sets the date and time when this game was finished.
     * @param datetime The date and time when this game was finished.
     */
    public void setEndTime(TemporalAccessor datetime) {
        String formatted = DATETIME_FORMATTER.format(datetime);
        put(END_DATETIME_KEY, formatted);
    }

    /**
     * Gets the date and time when this game was finished, or {@code null}
     * if no end time is included in this game's metadata.
     * @return The date and time when this game was finished,
     *         or else {@code null}.
     */
    public @Nullable TemporalAccessor getEndTime() {
        String formatted = get(END_DATETIME_KEY);
        if (formatted == null)
            return null;

        return DATETIME_FORMATTER.parse(formatted);
    }

    /**
     * Sets the time control used for this game.
     * @param timeControl The time control used for this game.
     */
    public void setTimeControl(TimeControl timeControl) {
        put(TIME_CONTROL_KEY, timeControl.toString());
    }

    /**
     * Gets the time control used for this game, or {@code null} if no
     * time control was included in this game's metadata.
     * @return The time control used for this game, or else {@code null}.
     */
    public @Nullable TimeControl getTimeControl() {
        String text = get(TIME_CONTROL_KEY);
        if (text == null)
            return null;

        return TimeControl.fromString(text);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        GameMetadata other = (GameMetadata) obj;
        return metadata.equals(other.metadata);
    }

    @Override
    public String toString() {
        return metadata.toString();
    }

    /**
     * Initialises metadata for a new game.
     * @param settings The settings used for the game.
     */
    public void initialiseForNewGame(GameSettings settings) {
        setStartTime(ZonedDateTime.now());
    }

    /**
     * Creates and initialises metadata for a new game.
     * @param settings The settings used for the game.
     * @return Metadata for a new game.
     */
    public static GameMetadata createForNewGame(GameSettings settings) {
        GameMetadata metadata = new GameMetadata();
        metadata.initialiseForNewGame(settings);
        return metadata;
    }
}
