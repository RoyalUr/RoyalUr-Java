package net.royalur.rules;

import net.royalur.model.Roll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RollTest {

    @Test
    public void testNew() {
        for (int value = 0; value < 10; ++value) {
            Roll roll = new Roll(value);
            assertEquals(value, roll.value);
        }

        assertThrows(IllegalArgumentException.class, () -> new Roll(-1));
        assertThrows(IllegalArgumentException.class, () -> new Roll(-5));
        assertThrows(IllegalArgumentException.class, () -> new Roll(-999));
    }

    @Test
    public void testOf() {
        for (int value = 0; value < 10; ++value) {
            Roll roll = Roll.of(value);
            assertEquals(value, roll.value);
        }

        assertThrows(IllegalArgumentException.class, () -> Roll.of(-1));
        assertThrows(IllegalArgumentException.class, () -> Roll.of(-5));
        assertThrows(IllegalArgumentException.class, () -> Roll.of(-999));
    }

    @Test
    public void testHashCode() {
        for (int value = 0; value < 10; ++value) {
            assertEquals(new Roll(value).hashCode(), Roll.of(value).hashCode());
        }
    }

    @Test
    public void testEquals() {
        for (int value1 = 0; value1 < 10; ++value1) {
            for (int value2 = 0; value2 < 10; ++value2) {
                if (value1 == value2) {
                    assertEquals(new Roll(value1), Roll.of(value2));
                    assertEquals(Roll.of(value1), new Roll(value2));
                } else {
                    assertNotEquals(new Roll(value1), Roll.of(value2));
                    assertNotEquals(Roll.of(value1), new Roll(value2));
                }
            }
        }

        Object notRoll = new Object();
        for (int value = 0; value < 10; ++value) {
            assertNotEquals(new Roll(value), notRoll);
            assertNotEquals(Roll.of(value), notRoll);
            assertNotEquals(new Roll(value), null);
            assertNotEquals(Roll.of(value), null);
        }
    }

    @Test
    public void testToString() {
        assertEquals("0", new Roll(0).toString());
        assertEquals("1", new Roll(1).toString());
        assertEquals("2", new Roll(2).toString());
        assertEquals("3", new Roll(3).toString());
        assertEquals("4", new Roll(4).toString());
    }
}
