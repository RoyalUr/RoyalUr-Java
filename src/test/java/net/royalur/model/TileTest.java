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
        assertEquals(2, tile.x);
        assertEquals(1, tile.y);

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
    }
}
