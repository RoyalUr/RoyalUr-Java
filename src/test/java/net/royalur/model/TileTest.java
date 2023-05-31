package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {

    @Test
    public void testNew() {
        Tile tile = new Tile(1, 1);
        assertEquals(0, tile.getXIndex());
        assertEquals(0, tile.getYIndex());
        assertEquals(1, tile.getX());
        assertEquals(1, tile.getY());

        tile = new Tile(3, 2);
        assertEquals(2, tile.getXIndex());
        assertEquals(1, tile.getYIndex());
        assertEquals(3, tile.getX());
        assertEquals(2, tile.getY());

        tile = new Tile(26, 1000);
        assertEquals(25, tile.getXIndex());
        assertEquals(999, tile.getYIndex());
        assertEquals(26, tile.getX());
        assertEquals(1000, tile.getY());

        tile = new Tile(3, 0);
        assertEquals(2, tile.getXIndex());
        assertEquals(-1, tile.getYIndex());
        assertEquals(3, tile.getX());
        assertEquals(0, tile.getY());

        for (int x = 1; x <= 26; ++x) {
            for (int y = 1; y <= 50; ++y) {
                tile = new Tile(x, y);
                assertEquals(x, tile.getX());
                assertEquals(y, tile.getY());
            }
        }

        assertThrows(IllegalArgumentException.class, () -> new Tile(0, 1));
        assertThrows(IllegalArgumentException.class, () -> new Tile(27, 1));
        assertThrows(IllegalArgumentException.class, () -> new Tile(0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Tile(-4, 6));
        assertThrows(IllegalArgumentException.class, () -> new Tile(-4, -3));
        assertThrows(IllegalArgumentException.class, () -> new Tile(36, 5));
        assertThrows(IllegalArgumentException.class, () -> new Tile(5, -1));
    }

    @Test
    public void testFromIndices() {
        Tile tile = Tile.fromIndices(0, 0);
        assertEquals(0, tile.getXIndex());
        assertEquals(0, tile.getYIndex());
        assertEquals(1, tile.getX());
        assertEquals(1, tile.getY());

        tile = Tile.fromIndices(2, 1);
        assertEquals(2, tile.getXIndex());
        assertEquals(1, tile.getYIndex());
        assertEquals(3, tile.getX());
        assertEquals(2, tile.getY());

        tile = Tile.fromIndices(25, 999);
        assertEquals(25, tile.getXIndex());
        assertEquals(999, tile.getYIndex());
        assertEquals(26, tile.getX());
        assertEquals(1000, tile.getY());

        tile = Tile.fromIndices(2, -1);
        assertEquals(2, tile.getXIndex());
        assertEquals(-1, tile.getYIndex());
        assertEquals(3, tile.getX());
        assertEquals(0, tile.getY());

        for (int ix = 0; ix < 26; ++ix) {
            for (int iy = 0; iy < 50; ++iy) {
                tile = Tile.fromIndices(ix, iy);
                assertEquals(ix, tile.getXIndex());
                assertEquals(iy, tile.getYIndex());
            }
        }

        assertThrows(IllegalArgumentException.class, () -> Tile.fromIndices(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromIndices(26, 0));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromIndices(-1, -1));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromIndices(-5, 5));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromIndices(-5, -4));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromIndices(35, 4));
        assertThrows(IllegalArgumentException.class, () -> Tile.fromIndices(4, -2));
    }

    @Test
    public void testIsValid() {
        assertTrue(Tile.isValidIndices(0, 0));
        assertTrue(Tile.isValidIndices(1, 0));
        assertTrue(Tile.isValidIndices(0, 1));
        assertTrue(Tile.isValidIndices(1, 1));
        assertTrue(Tile.isValidIndices(25, 999));
        assertTrue(Tile.isValidIndices(12, 12));
        assertTrue(Tile.isValidIndices(11, 13));

        assertFalse(Tile.isValidIndices(-1, 0));
        assertFalse(Tile.isValidIndices(1, -1));
        assertFalse(Tile.isValidIndices(-1, 1));
        assertFalse(Tile.isValidIndices(1, -1));
        assertFalse(Tile.isValidIndices(-1, 999));
        assertFalse(Tile.isValidIndices(12, -1));
        assertFalse(Tile.isValidIndices(-1, 13));

        assertFalse(Tile.isValidIndices(26, 0));
        assertFalse(Tile.isValidIndices(1, -1));
        assertFalse(Tile.isValidIndices(29, 1));
        assertFalse(Tile.isValidIndices(1, -1));
        assertFalse(Tile.isValidIndices(34, 999));
        assertFalse(Tile.isValidIndices(12, -1));
        assertFalse(Tile.isValidIndices(26, 13));

        for (int ix = 0; ix < 26; ++ix) {
            assertFalse(Tile.isValidIndices(ix, -1));
            assertFalse(Tile.isValidIndices(ix, -5));
            for (int iy = 0; iy < 50; ++iy) {
                assertTrue(Tile.isValidIndices(ix, iy));
            }
        }
    }

    @Test
    public void testEquals() {
        Tile oneOne = new Tile(1, 1);
        Tile twoOne = new Tile(2, 1);
        Tile oneTwo = new Tile(1, 2);
        Tile twoTwo = new Tile(2, 2);

        assertEquals(oneOne, oneOne);
        assertNotEquals(oneOne, twoOne);
        assertNotEquals(oneOne, oneTwo);
        assertNotEquals(oneOne, twoTwo);

        assertNotEquals(twoOne, oneOne);
        assertEquals(twoOne, twoOne);
        assertNotEquals(twoOne, oneTwo);
        assertNotEquals(twoOne, twoTwo);

        assertNotEquals(oneTwo, oneOne);
        assertNotEquals(oneTwo, twoOne);
        assertEquals(oneTwo, oneTwo);
        assertNotEquals(oneTwo, twoTwo);

        assertNotEquals(twoTwo, oneOne);
        assertNotEquals(twoTwo, twoOne);
        assertNotEquals(twoTwo, oneTwo);
        assertEquals(twoTwo, twoTwo);

        assertEquals(oneOne, new Tile(1, 1));
        assertEquals(twoOne, new Tile(2, 1));
        assertEquals(oneTwo, new Tile(1, 2));
        assertEquals(twoTwo, new Tile(2, 2));

        Object notTile = new Object();
        assertNotEquals(oneOne, notTile);
        assertNotEquals(twoOne, notTile);
        assertNotEquals(oneTwo, notTile);
        assertNotEquals(twoTwo, notTile);
        assertNotEquals(oneOne, null);
    }

    @Test
    public void testToString() {
        assertEquals("A0", new Tile(1, 0).toString());
        assertEquals("A1", new Tile(1, 1).toString());
        assertEquals("B0", new Tile(2, 0).toString());
        assertEquals("B1", new Tile(2, 1).toString());
        assertEquals("C7", new Tile(3, 7).toString());
        assertEquals("D15", new Tile(4, 15).toString());
        assertEquals("F99", new Tile(6, 99).toString());
        assertEquals("K789", new Tile(11, 789).toString());
    }

    @Test
    public void testFromString() {
        assertEquals(new Tile(1, 0), Tile.fromString("A0"));
        assertEquals(new Tile(1, 1), Tile.fromString("A1"));
        assertEquals(new Tile(2, 0), Tile.fromString("B0"));
        assertEquals(new Tile(2, 1), Tile.fromString("B1"));
        assertEquals(new Tile(3, 7), Tile.fromString("C7"));
        assertEquals(new Tile(4, 15), Tile.fromString("D15"));
        assertEquals(new Tile(6, 99), Tile.fromString("F99"));
        assertEquals(new Tile(11, 789), Tile.fromString("K789"));

        assertEquals(new Tile(1, 0), Tile.fromString("a0"));
        assertEquals(new Tile(1, 1), Tile.fromString("a1"));
        assertEquals(new Tile(2, 0), Tile.fromString("b0"));
        assertEquals(new Tile(2, 1), Tile.fromString("b1"));
        assertEquals(new Tile(3, 7), Tile.fromString("c7"));
        assertEquals(new Tile(4, 15), Tile.fromString("d15"));
        assertEquals(new Tile(6, 99), Tile.fromString("f99"));
        assertEquals(new Tile(11, 789), Tile.fromString("k789"));

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
        for (int x = 1; x <= 26; ++x) {
            for (int y = 0; y <= 50; ++y) {
                Tile tile = new Tile(x, y);
                assertEquals(tile, Tile.fromString(tile.toString()));
            }
        }
    }
}
