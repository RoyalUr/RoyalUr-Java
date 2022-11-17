package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    @Test
    public void testNew() {
        Tile tile = new Tile(0, 0);
        assertEquals(0, tile.x);
        assertEquals(0, tile.y);

        tile = new Tile(2, 1);
        assertEquals(2, tile.x);
        assertEquals(1, tile.y);

        tile = new Tile(25, 999);
        assertEquals(25, tile.x);
        assertEquals(999, tile.y);

        for (int x = 0; x < 26; ++x) {
            for (int y = 0; y < 50; ++y) {
                tile = new Tile(x, y);
                assertEquals(x, tile.x);
                assertEquals(y, tile.y);
            }
        }

        assertThrows(IllegalArgumentException.class, () -> new Tile(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tile(0, -1));
        assertThrows(IllegalArgumentException.class, () -> new Tile(26, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tile(-1, -1));
        assertThrows(IllegalArgumentException.class, () -> new Tile(-5, 5));
        assertThrows(IllegalArgumentException.class, () -> new Tile(5, -5));
        assertThrows(IllegalArgumentException.class, () -> new Tile(-5, -4));
        assertThrows(IllegalArgumentException.class, () -> new Tile(35, 4));
        assertThrows(IllegalArgumentException.class, () -> new Tile(35, -9));
    }

    @Test
    public void testIsValid() {
        assertTrue(Tile.isValid(0, 0));
        assertTrue(Tile.isValid(1, 0));
        assertTrue(Tile.isValid(0, 1));
        assertTrue(Tile.isValid(1, 1));
        assertTrue(Tile.isValid(25, 999));
        assertTrue(Tile.isValid(12, 12));
        assertTrue(Tile.isValid(11, 13));

        assertFalse(Tile.isValid(-1, 0));
        assertFalse(Tile.isValid(1, -1));
        assertFalse(Tile.isValid(-1, 1));
        assertFalse(Tile.isValid(1, -1));
        assertFalse(Tile.isValid(-1, 999));
        assertFalse(Tile.isValid(12, -1));
        assertFalse(Tile.isValid(-1, 13));

        assertFalse(Tile.isValid(26, 0));
        assertFalse(Tile.isValid(1, -1));
        assertFalse(Tile.isValid(29, 1));
        assertFalse(Tile.isValid(1, -1));
        assertFalse(Tile.isValid(34, 999));
        assertFalse(Tile.isValid(12, -1));
        assertFalse(Tile.isValid(26, 13));

        for (int x = 0; x < 26; ++x) {
            assertFalse(Tile.isValid(x, -1));
            assertFalse(Tile.isValid(x, -5));
            for (int y = 0; y < 50; ++y) {
                assertTrue(Tile.isValid(x, y));
            }
        }
    }

    @Test
    public void testEquals() {
        Tile zeroZero = new Tile(0, 0);
        Tile oneZero = new Tile(1, 0);
        Tile zeroOne = new Tile(0, 1);
        Tile oneOne = new Tile(1, 1);

        assertEquals(zeroZero, zeroZero);
        assertNotEquals(zeroZero, oneZero);
        assertNotEquals(zeroZero, zeroOne);
        assertNotEquals(zeroZero, oneOne);

        assertNotEquals(oneZero, zeroZero);
        assertEquals(oneZero, oneZero);
        assertNotEquals(oneZero, zeroOne);
        assertNotEquals(oneZero, oneOne);

        assertNotEquals(zeroOne, zeroZero);
        assertNotEquals(zeroOne, oneZero);
        assertEquals(zeroOne, zeroOne);
        assertNotEquals(zeroOne, oneOne);

        assertNotEquals(oneOne, zeroZero);
        assertNotEquals(oneOne, oneZero);
        assertNotEquals(oneOne, zeroOne);
        assertEquals(oneOne, oneOne);

        assertEquals(zeroZero, new Tile(0, 0));
        assertEquals(oneZero, new Tile(1, 0));
        assertEquals(zeroOne, new Tile(0, 1));
        assertEquals(oneOne, new Tile(1, 1));

        Object notTile = new Object();
        assertNotEquals(zeroZero, notTile);
        assertNotEquals(oneZero, notTile);
        assertNotEquals(zeroOne, notTile);
        assertNotEquals(oneOne, notTile);
        assertNotEquals(zeroZero, null);
    }

    @Test
    public void testToString() {
        assertEquals("A0", new Tile(0, 0).toString());
        assertEquals("A1", new Tile(0, 1).toString());
        assertEquals("B0", new Tile(1, 0).toString());
        assertEquals("B1", new Tile(1, 1).toString());
        assertEquals("C7", new Tile(2, 7).toString());
        assertEquals("D15", new Tile(3, 15).toString());
        assertEquals("F99", new Tile(5, 99).toString());
        assertEquals("K789", new Tile(10, 789).toString());
    }

    @Test
    public void testFromString() {
        assertEquals(new Tile(0, 0), Tile.fromString("A0"));
        assertEquals(new Tile(0, 1), Tile.fromString("A1"));
        assertEquals(new Tile(1, 0), Tile.fromString("B0"));
        assertEquals(new Tile(1, 1), Tile.fromString("B1"));
        assertEquals(new Tile(2, 7), Tile.fromString("C7"));
        assertEquals(new Tile(3, 15), Tile.fromString("D15"));
        assertEquals(new Tile(5, 99), Tile.fromString("F99"));
        assertEquals(new Tile(10, 789), Tile.fromString("K789"));

        assertEquals(new Tile(0, 0), Tile.fromString("a0"));
        assertEquals(new Tile(0, 1), Tile.fromString("a1"));
        assertEquals(new Tile(1, 0), Tile.fromString("b0"));
        assertEquals(new Tile(1, 1), Tile.fromString("b1"));
        assertEquals(new Tile(2, 7), Tile.fromString("c7"));
        assertEquals(new Tile(3, 15), Tile.fromString("d15"));
        assertEquals(new Tile(5, 99), Tile.fromString("f99"));
        assertEquals(new Tile(10, 789), Tile.fromString("k789"));

        assertThrows(IllegalArgumentException.class, () -> Tile.fromString("A"));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromString("B"));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromString("F"));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromString("1"));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromString("7"));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromString("99"));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromString(";9"));
        assertThrows(NumberFormatException.class, () -> Tile.fromString("AA"));
        assertThrows(NumberFormatException.class, () -> Tile.fromString("AB9"));
    }

    @Test
    public void testToFromString() {
        for (int x = 0; x < 26; ++x) {
            for (int y = 0; y < 50; ++y) {
                Tile tile = new Tile(x, y);
                assertEquals(tile, Tile.fromString(tile.toString()));
            }
        }
    }
}
