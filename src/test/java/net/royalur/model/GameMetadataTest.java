package net.royalur.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class GameMetadataTest {

    private static void assertEquivalent(long epochMs, ZonedDateTime dateTime) {
        assertEquals(epochMs, Instant.from(dateTime).toEpochMilli());
    }

    @Test
    public void testDatetimeParsing() {
        long t2023_06_19_13_27_02_UTC = 1687181222000L;
        long t2023_06_19_00_00_00_UTC = 1687132800000L;
        assertEquivalent(t2023_06_19_13_27_02_UTC, GameMetadata.parseDatetime("2023-06-19T14:27:02+01:00"));
        assertEquivalent(t2023_06_19_13_27_02_UTC, GameMetadata.parseDatetime("2023-06-19T13:27:02"));
        assertEquivalent(t2023_06_19_00_00_00_UTC, GameMetadata.parseDatetime("2023-06-19"));
    }

    @Test
    public void testDatetimeFormatting() {
        Instant t2023_06_19_13_27_02_UTC = Instant.ofEpochMilli(1687181222000L);
        assertEquals(
                "2023-06-19T13:27:02Z",
                GameMetadata.formatDatetime(t2023_06_19_13_27_02_UTC)
        );
    }
}
