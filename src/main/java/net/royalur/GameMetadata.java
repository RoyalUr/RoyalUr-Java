package net.royalur;

import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;
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
    public static final @Nonnull DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ISO_DATE;

    /**
     * The key for storing when a game started.
     */
    public static final @Nonnull String START_DATETIME_KEY = "StartTime";

    /**
     * The key for storing when a game was finished.
     */
    public static final @Nonnull String END_DATETIME_KEY = "EndTime";

    /**
     * Arbitrary metadata about this game.
     */
    private final @Nonnull Map<String, String> metadata;

    /**
     * Instantiates metadata for a game of the Royal Game of Ur.
     * @param metadata The metadata of the game.
     */
    public GameMetadata(@Nonnull Map<String, String> metadata) {
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
    public @Nonnull GameMetadata copy() {
        return new GameMetadata(metadata);
    }

    /**
     * Retrieves a copy of all the metadata stored.
     * @return A copy of all the metadata stored.
     */
    public @Nonnull Map<String, String> getAll() {
        return Map.copyOf(metadata);
    }

    /**
     * Get the metadata value associated with {@code key}, or {@code null}
     * if there is no value associated with the key.
     * @param key The metadata key to retrieve.
     * @return The metadata value associated with {@code key}, or else {@code null}.
     */
    public @Nullable String get(@Nonnull String key) {
        return metadata.get(key);
    }

    /**
     * Removes any metadata value associated with {@code key}.
     * @param key The metadata key to remove.
     */
    public void remove(@Nonnull String key) {
        metadata.remove(key);
    }

    /**
     * Add a new metadata value, {@code value}, associated with {@code key}.
     * @param key The metadata key to set the value for.
     * @param value The value to associate with the given key.
     */
    public void put(@Nonnull String key, @Nonnull String value) {
        metadata.put(key, value);
    }

    /**
     * Sets the date and time when this game began.
     * @param datetime The date and time when this game began.
     */
    public void setStartTime(@Nonnull TemporalAccessor datetime) {
        String formatted = DATETIME_FORMATTER.format(datetime);
        put(START_DATETIME_KEY, formatted);
    }

    /**
     * Gets the date and time when this game began,
     * or {@code null} if no end time is included in this
     * game's metadata.
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
    public void setEndTime(@Nonnull TemporalAccessor datetime) {
        String formatted = DATETIME_FORMATTER.format(datetime);
        put(END_DATETIME_KEY, formatted);
    }

    /**
     * Gets the date and time when this game was finished,
     * or {@code null} if no end time is included in this
     * game's metadata.
     * @return The date and time when this game was finished,
     *         or else {@code null}.
     */
    public @Nullable TemporalAccessor getEndTime() {
        String formatted = get(END_DATETIME_KEY);
        if (formatted == null)
            return null;

        return DATETIME_FORMATTER.parse(formatted);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null || !getClass().equals(obj.getClass()))
            return false;

        GameMetadata other = (GameMetadata) obj;
        return metadata.equals(other.metadata);
    }

    @Override
    public @Nonnull String toString() {
        return metadata.toString();
    }

    /**
     * Initialises metadata for a new game.
     * @param rules The rules used for the game.
     */
    public void initialiseForNewGame(@Nonnull RuleSet<?, ?, ?> rules) {
        setStartTime(ZonedDateTime.now());
    }

    /**
     * Creates and initialises metadata for a new game.
     * @param rules The rules used for the game.
     * @return Metadata for a new game.
     */
    public static @Nonnull GameMetadata createForNewGame(
            @Nonnull RuleSet<?, ?, ?> rules
    ) {
        GameMetadata metadata = new GameMetadata();
        metadata.initialiseForNewGame(rules);
        return metadata;
    }
}
