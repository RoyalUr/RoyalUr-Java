package net.royalur.rules;

import net.royalur.model.dice.BasicRoll;
import net.royalur.model.dice.Roll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RollTest {

    @Test
    public void testNew() {
        for (int value = 0; value < 10; ++value) {
            Roll roll = new BasicRoll(value);
            assertEquals(value, roll.value());
        }

        assertThrows(IllegalArgumentException.class, () -> new BasicRoll(-1));
        assertThrows(IllegalArgumentException.class, () -> new BasicRoll(-5));
        assertThrows(IllegalArgumentException.class, () -> new BasicRoll(-999));
    }

    @Test
    public void testOf() {
        for (int value = 0; value < 10; ++value) {
            Roll roll = BasicRoll.of(value);
            assertEquals(value, roll.value());
        }

        assertThrows(IllegalArgumentException.class, () -> BasicRoll.of(-1));
        assertThrows(IllegalArgumentException.class, () -> BasicRoll.of(-5));
        assertThrows(IllegalArgumentException.class, () -> BasicRoll.of(-999));
    }

    @Test
    public void testHashCode() {
        for (int value = 0; value < 10; ++value) {
            assertEquals(new BasicRoll(value).hashCode(), BasicRoll.of(value).hashCode());
        }
    }

    @Test
    public void testEquals() {
        for (int value1 = 0; value1 < 10; ++value1) {
            for (int value2 = 0; value2 < 10; ++value2) {
                if (value1 == value2) {
                    assertEquals(new BasicRoll(value1), BasicRoll.of(value2));
                    assertEquals(BasicRoll.of(value1), new BasicRoll(value2));
                } else {
                    assertNotEquals(new BasicRoll(value1), BasicRoll.of(value2));
                    assertNotEquals(BasicRoll.of(value1), new BasicRoll(value2));
                }
            }
        }

        Object notRoll = new Object();
        for (int value = 0; value < 10; ++value) {
            assertNotEquals(new BasicRoll(value), notRoll);
            assertNotEquals(BasicRoll.of(value), notRoll);
            assertNotEquals(new BasicRoll(value), null);
            assertNotEquals(BasicRoll.of(value), null);
        }
    }

    @Test
    public void testToString() {
        assertEquals("0", new BasicRoll(0).toString());
        assertEquals("1", new BasicRoll(1).toString());
        assertEquals("2", new BasicRoll(2).toString());
        assertEquals("3", new BasicRoll(3).toString());
        assertEquals("4", new BasicRoll(4).toString());
    }
}
