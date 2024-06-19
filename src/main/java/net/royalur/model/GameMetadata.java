package net.royalur.model;

import javax.annotation.Nullable;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.*;

/**
 * Stores the metadata of games, such as the date the game was played,
 * the players of the game, and the game rules.
 */
public class GameMetadata {

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
     * Standard metadata keys that are commonly used.
     */
    public static final Set<String> STANDARD_KEYS = Set.of(
            START_DATETIME_KEY, END_DATETIME_KEY, TIME_CONTROL_KEY
    );

    /**
     * Arbitrary metadata about a game.
     */
    private final Map<String, String> metadata;

    private @Nullable ZonedDateTime startTime;
    private @Nullable ZonedDateTime endTime;
    private @Nullable TimeControl timeControl;

    /**
     * Instantiates metadata for a game of the Royal Game of Ur.
     * @param metadata The metadata of the game.
     */
    public GameMetadata(Map<String, String> metadata) {
        // We use a LinkedHashMap to maintain order.
        this.metadata = new LinkedHashMap<>(metadata);

        parseStandardEntries();
    }

    /**
     * Instantiates an empty metadata for a game.
     */
    public GameMetadata() {
        this(Collections.emptyMap());
    }

    public static GameMetadata startingNow() {
        GameMetadata metadata = new GameMetadata();
        metadata.setStartTime(Instant.ofEpochMilli(System.currentTimeMillis()));
        return metadata;
    }

    /**
     * Creates a copy of this metadata.
     * @return A copy of this metadata.
     */
    public GameMetadata copy() {
        return new GameMetadata(metadata);
    }

    private void parseStandardEntries() {
        String startTime = metadata.get(START_DATETIME_KEY);
        this.startTime = (startTime != null ? parseDatetime(startTime) : null);

        String endTime = metadata.get(END_DATETIME_KEY);
        this.endTime = (endTime != null ? parseDatetime(endTime) : null);

        String timeControl = metadata.get(TIME_CONTROL_KEY);
        this.timeControl = (timeControl != null ? TimeControl.fromString(timeControl) : null);
    }

    /**
     * Retrieves a copy of all the metadata stored.
     * @return A copy of all the metadata stored.
     */
    public Map<String, String> getAll() {
        return Map.copyOf(metadata);
    }

    /**
     * Checks whether there is any metadata associated with {@code key}.
     * @param key The metadata key to check.
     * @return Whether there is any metadata associated with {@code key}.
     */
    public boolean has(String key) {
        return metadata.containsKey(key);
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
        if (STANDARD_KEYS.contains(key)) {
            parseStandardEntries();
        }
    }

    /**
     * Removes all metadata.
     */
    public void clear() {
        metadata.clear();
        parseStandardEntries();
    }

    /**
     * Add a new metadata value, {@code value}, associated with {@code key}.
     * @param key The metadata key to set the value for.
     * @param value The value to associate with the given key.
     */
    public void put(String key, String value) {
        metadata.put(key, value);
        if (STANDARD_KEYS.contains(key)) {
            parseStandardEntries();
        }
    }

    /**
     * Checks whether this metadata contains the date and time when this game began.
     * @return Whether this metadata contains the date and time when this game began.
     */
    public boolean hasStartTime() {
        return startTime != null;
    }

    /**
     * Gets the date and time when this game began, or {@code null}
     * if no end time is included in this game's metadata.
     * @return The date and time when this game began,
     *         or else {@code null}.
     */
    public @Nullable ZonedDateTime getStartTime() {
        return startTime;
    }

    /**
     * Sets the date and time when this game began.
     * @param datetime The date and time when this game began.
     */
    public void setStartTime(TemporalAccessor datetime) {
        put(START_DATETIME_KEY, formatDatetime(datetime));
    }

    /**
     * Checks whether this metadata contains the date and time when this game was finished.
     * @return Whether this metadata contains the date and time when this game was finished.
     */
    public boolean hasEndTime() {
        return endTime != null;
    }

    /**
     * Gets the date and time when this game was finished, or {@code null}
     * if no end time is included in this game's metadata.
     * @return The date and time when this game was finished,
     *         or else {@code null}.
     */
    public @Nullable ZonedDateTime getEndTime() {
        return endTime;
    }

    /**
     * Sets the date and time when this game was finished.
     * @param datetime The date and time when this game was finished.
     */
    public void setEndTime(TemporalAccessor datetime) {
        put(END_DATETIME_KEY, formatDatetime(datetime));
    }

    /**
     * Checks whether this metadata contains the time control used for the game.
     * @return Whether this metadata contains the time control used for the game.
     */
    public boolean hasTimeControl() {
        return timeControl != null;
    }

    /**
     * Gets the time control used for this game, or {@code null} if no
     * time control was included in this game's metadata.
     * @return The time control used for this game, or else {@code null}.
     */
    public @Nullable TimeControl getTimeControl() {
        return timeControl;
    }

    /**
     * Sets the time control used for this game.
     * @param timeControl The time control used for this game.
     */
    public void setTimeControl(TimeControl timeControl) {
        put(TIME_CONTROL_KEY, timeControl.toString());
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
     * ISO date format, with some allowances.
     */
    private static final DateTimeFormatter DATETIME_FORMATTER;

    static {
        DATETIME_FORMATTER = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .append(DateTimeFormatter.ISO_LOCAL_DATE)
                .optionalStart()
                .appendLiteral('T')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .optionalStart()
                .appendOffsetId()
                .optionalStart()
                .appendLiteral('[')
                .parseCaseSensitive()
                .appendZoneRegionId()
                .appendLiteral(']')
                .toFormatter(Locale.US);
    }

    public static ZonedDateTime parseDatetime(String datetime) {
        TemporalAccessor result = DATETIME_FORMATTER.parseBest(
                datetime,
                ZonedDateTime::from,
                LocalDateTime::from,
                LocalDate::from
        );
        if (result instanceof ZonedDateTime zonedResult)
            return zonedResult;
        if (result instanceof LocalDateTime localResult)
            return localResult.atZone(ZoneOffset.UTC);
        if (result instanceof LocalDate localResult)
            return localResult.atStartOfDay(ZoneOffset.UTC);

        throw new IllegalArgumentException("Unable to parse date: " + datetime);
    }

    public static String formatDatetime(TemporalAccessor datetime) {
        if (datetime instanceof Instant instant) {
            datetime = instant.atZone(ZoneOffset.UTC);
        } else if (datetime instanceof LocalDateTime localTime) {
            datetime = localTime.atZone(ZoneOffset.UTC);
        } else if (datetime instanceof LocalDate localTime) {
            datetime = localTime.atStartOfDay(ZoneOffset.UTC);
        }
        return DATETIME_FORMATTER.format(datetime);
    }
}
