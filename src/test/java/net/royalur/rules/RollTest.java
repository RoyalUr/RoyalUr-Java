package net.royalur.rules;

import net.royalur.model.dice.SimpleRoll;
import net.royalur.model.dice.Roll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RollTest {

    @Test
    public void testNew() {
        for (int value = 0; value < 10; ++value) {
            Roll roll = new SimpleRoll(value);
            assertEquals(value, roll.value());
        }

        assertThrows(IllegalArgumentException.class, () -> new SimpleRoll(-1));
        assertThrows(IllegalArgumentException.class, () -> new SimpleRoll(-5));
        assertThrows(IllegalArgumentException.class, () -> new SimpleRoll(-999));
    }

    @Test
    public void testOf() {
        for (int value = 0; value < 10; ++value) {
            Roll roll = SimpleRoll.of(value);
            assertEquals(value, roll.value());
        }

        assertThrows(IllegalArgumentException.class, () -> SimpleRoll.of(-1));
        assertThrows(IllegalArgumentException.class, () -> SimpleRoll.of(-5));
        assertThrows(IllegalArgumentException.class, () -> SimpleRoll.of(-999));
    }

    @Test
    public void testHashCode() {
        for (int value = 0; value < 10; ++value) {
            assertEquals(new SimpleRoll(value).hashCode(), SimpleRoll.of(value).hashCode());
        }
    }

    @Test
    public void testEquals() {
        for (int value1 = 0; value1 < 10; ++value1) {
            for (int value2 = 0; value2 < 10; ++value2) {
                if (value1 == value2) {
                    assertEquals(new SimpleRoll(value1), SimpleRoll.of(value2));
                    assertEquals(SimpleRoll.of(value1), new SimpleRoll(value2));
                } else {
                    assertNotEquals(new SimpleRoll(value1), SimpleRoll.of(value2));
                    assertNotEquals(SimpleRoll.of(value1), new SimpleRoll(value2));
                }
            }
        }

        Object notRoll = new Object();
        for (int value = 0; value < 10; ++value) {
            assertNotEquals(new SimpleRoll(value), notRoll);
            assertNotEquals(SimpleRoll.of(value), notRoll);
            assertNotEquals(new SimpleRoll(value), null);
            assertNotEquals(SimpleRoll.of(value), null);
        }
    }

    @Test
    public void testToString() {
        assertEquals("0", new SimpleRoll(0).toString());
        assertEquals("1", new SimpleRoll(1).toString());
        assertEquals("2", new SimpleRoll(2).toString());
        assertEquals("3", new SimpleRoll(3).toString());
        assertEquals("4", new SimpleRoll(4).toString());
    }
}
