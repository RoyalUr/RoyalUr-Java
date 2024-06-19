package net.royalur.rules;

import net.royalur.model.GameMetadata;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;

/**
 * Provides timing information for recording the history of a game.
 */
public interface TimeProvider {

    /**
     * Checks whether this time provider produces values other than zero.
     * @return Whether this time provider produces values other than zero.
     */
    boolean isTimed();

    /**
     * Gets the start time of the game in milliseconds since the epoch.
     * If this provider is untimed, {@code 0} will be returned instead.
     * @return The start time of the game in milliseconds since the epoch.
     */
    long getGameStartEpochMs();

    /**
     * Gets the start time of the game, or {@code null} if the start time is not recorded.
     * If this provider is untimed, {@code 0} will be returned instead.
     * @return The start time of the game, or else {@code null}.
     */
    default @Nullable ZonedDateTime getGameStartTime() {
        long gameStartEpochMs = getGameStartEpochMs();
        if (gameStartEpochMs <= 0)
            return null;

        return Instant.ofEpochMilli(gameStartEpochMs).atZone(ZoneOffset.UTC);
    }

    /**
     * Gets the number of milliseconds elapsed since the start of the game.
     * @return The number of milliseconds elapsed since the start of the game.
     */
    long getTimeSinceGameStartMs();

    /**
     * Creates a new time provider where the game starts now.
     * @return A new time provider where the game starts now.
     */
    static TimeProvider createStartingNow() {
        return new Timed(System.currentTimeMillis());
    }

    /**
     * Creates a new time provider where the game starts at the given time.
     * @return A new time provider where the game starts at the given time.
     */
    static TimeProvider createStartingAtEpochMs(long epochMs) {
        return new Timed(epochMs);
    }

    /**
     * Creates a new time provider where the game is not timed.
     * @return A new time provider where the game is not timed.
     */
    static TimeProvider createUntimed() {
        return new Untimed();
    }

    /**
     * Creates a new time provider that takes the game start time from
     * the given metadata.
     * @param metadata The metadata to source the game start time from.
     * @return A new time provider that takes the game start time from {@code metadata}.
     */
    static TimeProvider fromMetadata(GameMetadata metadata) {
        TemporalAccessor startTime = metadata.getStartTime();
        if (startTime != null)
            return createStartingAtEpochMs(Instant.from(startTime).toEpochMilli());

        return createUntimed();
    }

    class Timed implements TimeProvider {
        private final long gameStartEpochMs;

        public Timed(long gameStartEpochMs) {
            this.gameStartEpochMs = gameStartEpochMs;
        }

        @Override
        public boolean isTimed() {
            return true;
        }

        @Override
        public long getGameStartEpochMs() {
            return gameStartEpochMs;
        }

        @Override
        public long getTimeSinceGameStartMs() {
            return System.currentTimeMillis() - gameStartEpochMs;
        }
    }

    class Untimed implements TimeProvider {

        @Override
        public boolean isTimed() {
            return false;
        }

        @Override
        public long getGameStartEpochMs() {
            return 0;
        }

        @Override
        public long getTimeSinceGameStartMs() {
            return 0;
        }
    }
}
