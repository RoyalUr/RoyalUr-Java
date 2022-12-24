package net.royalur;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class RoyalUrAPITest {

    private static final Pattern SEMVER_PATTERN = Pattern.compile(
            "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)" +
            "(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)" +
            "(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))" +
            "?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$"
    );

    /**
     * Ensures compliance with a semantic versioning format.
     * See: <a href="https://semver.org/">https://semver.org</a>
     */
    @ParameterizedTest
    @ValueSource(strings = RoyalUrAPI.VERSION)
    public void testVersion(String version) {
        assertTrue(SEMVER_PATTERN.matcher(version).matches());
    }

    /**
     * Test for a test!
     */
    @Test
    public void testVersionTest() {
        testVersion("0.0.0");
        testVersion("0.0.1");
        testVersion("0.1.0");
        testVersion("1.0.0");
        testVersion("1.1.1");
        testVersion("99.99.99");
        testVersion("1.2.3");
        testVersion("1.2.3-a");
        testVersion("1.2.3-beta.11+sha.0nsfgkjkjsdf");
        testVersion("1.2.3-beta.1-1.ab-c+sha.0nsfgkjkjs-df");
        testVersion("1.0.0-alpha.1");
        testVersion("1.0.0-alpha.beta");
        testVersion("1.0.0-alpha");
        testVersion("1.0.0-beta");
        testVersion("1.0.0-beta.11");
        testVersion("1.0.0-beta.2");
        testVersion("1.0.0-rc.1");
        testVersion("1.0.0-alpha.12.ab-c");
        testVersion("1.0.0-alpha.12.x-yz");
        testVersion("1.0.0+ksadhjgksdhgksdhgfj");
        testVersion("1.0.0+sdgfsdgsdhsdfgdsfgf");
        testVersion("1.2.3-Beta.4+SHA123456789");
        testVersion("3.2.3-Beta.4+SHA123456789");
        testVersion("1.4.3-Beta.4+SHA123456789");
        testVersion("1.2.5-Beta.4+SHA123456789");
        testVersion("1.2.3+SHA123456789");
        testVersion("1.2.3-Beta.4-test+sha12345-6789");

        assertThrows(AssertionError.class, () -> testVersion("1.2.3-"));
        assertThrows(AssertionError.class, () -> testVersion("1.0.0+"));
        assertThrows(AssertionError.class, () -> testVersion("1.2.3.4-beta.11+sha.0nsfgkjkjsdf"));
        assertThrows(AssertionError.class, () -> testVersion("1.2-beta.11+sha.0nsfgkjkjsdf"));
        assertThrows(AssertionError.class, () -> testVersion("1-beta.11+sha.0nsfgkjkjsdf"));
        assertThrows(AssertionError.class, () -> testVersion("1-beta.11+sha.0nsfgkjkjsdf"));
    }
}
