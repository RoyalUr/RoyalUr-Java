package net.royalur.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    private static final Tile T00 = new Tile(0, 0);
    private static final Tile T01 = new Tile(0, 1);
    private static final Tile T10 = new Tile(1, 0);
    private static final Tile T11 = new Tile(1, 1);
    private static final Tile T21 = new Tile(2, 1);
    private static final Tile T12 = new Tile(1, 2);

    @Test
    public void testNew() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T00);

        Path path = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals(Player.LIGHT, path.player);
        assertEquals(1, path.length);
        assertEquals(tileList, path.tiles);
        assertNotSame(tileList, path.tiles);
        assertEquals(T01, path.startTile);
        assertEquals(T10, path.endTile);

        tileList.add(T11);
        assertNotEquals(tileList, path.tiles);
        assertEquals(1, path.length);

        path = new Path(Player.DARK, tileList, T01, T21);
        assertEquals(Player.DARK, path.player);
        assertEquals(2, path.length);
        assertEquals(tileList, path.tiles);
        assertNotSame(tileList, path.tiles);
        assertEquals(T01, path.startTile);
        assertEquals(T21, path.endTile);

        tileList.add(T12);
        assertNotEquals(tileList, path.tiles);
        assertEquals(2, path.length);

        assertThrows(IllegalArgumentException.class, () -> new Path(Player.LIGHT, List.of(), T01, T21));
    }

    @Test
    public void testGet() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T00);

        Path path1 = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals(T00, path1.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> path1.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> path1.get(1));

        tileList.add(T01);
        tileList.add(T11);
        tileList.add(T21);

        Path path2 = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals(T00, path2.get(0));
        assertEquals(T01, path2.get(1));
        assertEquals(T11, path2.get(2));
        assertEquals(T21, path2.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> path2.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> path2.get(4));
    }

    @Test
    public void testIterator() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T00);

        Path path = new Path(Player.LIGHT, tileList, T01, T10);
        Iterator<Tile> iter = path.iterator();
        assertTrue(iter.hasNext());
        assertEquals(T00, iter.next());
        assertFalse(iter.hasNext());

        tileList.add(T01);
        tileList.add(T11);
        tileList.add(T21);
        assertFalse(iter.hasNext());

        path = new Path(Player.LIGHT, tileList, T01, T10);
        iter = path.iterator();
        assertTrue(iter.hasNext());
        assertEquals(T00, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(T01, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(T11, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(T21, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testHashCode() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T00);

        Path path1 = new Path(Player.LIGHT, tileList, T01, T10);
        Path path2 = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals(path1.hashCode(), path2.hashCode());

        path1 = new Path(Player.DARK, tileList, T01, T11);
        path2 = new Path(Player.DARK, tileList, T01, T11);
        assertEquals(path1.hashCode(), path2.hashCode());

        path1 = new Path(Player.LIGHT, tileList, T21, T10);
        path2 = new Path(Player.LIGHT, tileList, T21, T10);
        assertEquals(path1.hashCode(), path2.hashCode());

        tileList.add(T10);
        tileList.add(T11);

        path1 = new Path(Player.LIGHT, tileList, T21, T11);
        path2 = new Path(Player.LIGHT, tileList, T21, T11);
        assertEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    public void testIsEquivalent() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T00);

        Path path1 = new Path(Player.LIGHT, tileList, T01, T10);
        Path path2 = new Path(Player.LIGHT, tileList, T01, T10);
        assertTrue(path1.isEquivalent(path2));
        assertTrue(path2.isEquivalent(path1));

        Path path3 = new Path(Player.DARK, tileList, T01, T11);
        Path path4 = new Path(Player.DARK, tileList, T01, T11);
        assertTrue(path3.isEquivalent(path4));
        assertTrue(path4.isEquivalent(path3));
        assertTrue(path1.isEquivalent(path4));
        assertTrue(path3.isEquivalent(path1));
        assertTrue(path2.isEquivalent(path4));
        assertTrue(path3.isEquivalent(path2));

        Path path5 = new Path(Player.LIGHT, tileList, T21, T10);
        Path path6 = new Path(Player.LIGHT, tileList, T21, T10);
        assertTrue(path5.isEquivalent(path6));
        assertTrue(path6.isEquivalent(path5));
        assertTrue(path1.isEquivalent(path5));
        assertTrue(path5.isEquivalent(path1));
        assertTrue(path4.isEquivalent(path6));
        assertTrue(path6.isEquivalent(path4));

        tileList.add(T10);
        tileList.add(T11);

        Path path7 = new Path(Player.LIGHT, tileList, T21, T11);
        Path path8 = new Path(Player.LIGHT, tileList, T21, T11);
        assertTrue(path7.isEquivalent(path8));
        assertTrue(path8.isEquivalent(path7));
        assertFalse(path7.isEquivalent(path1));
        assertFalse(path7.isEquivalent(path3));
        assertFalse(path7.isEquivalent(path5));

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(T11);
        tileList2.add(T10);
        tileList2.add(T00);
        Path path9 = new Path(Player.DARK, tileList2, T21, T11);
        assertTrue(path9.isEquivalent(path9));
        assertFalse(path9.isEquivalent(path1));
        assertFalse(path1.isEquivalent(path9));
        assertFalse(path9.isEquivalent(path7));
        assertFalse(path7.isEquivalent(path9));
    }

    @Test
    public void testEquals() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T00);

        Path path1 = new Path(Player.LIGHT, tileList, T01, T10);
        Path path2 = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals(path1, path1);
        assertEquals(path1, path2);
        assertEquals(path2, path1);
        assertEquals(path1, new Path(Player.LIGHT, tileList, T01, T10));
        assertEquals(path1, new Path(Player.LIGHT, tileList, T01, T10));
        assertNotEquals(path1, new Path(Player.LIGHT, tileList, T01, T11));
        assertNotEquals(path1, new Path(Player.LIGHT, tileList, T21, T10));
        assertNotEquals(path1, new Path(Player.LIGHT, tileList, T21, T11));

        Path path3 = new Path(Player.DARK, tileList, T21, T10);
        Path path4 = new Path(Player.DARK, tileList, T21, T10);
        assertEquals(path3, path3);
        assertEquals(path3, path4);
        assertEquals(path4, path3);
        assertNotEquals(path3, path1);
        assertNotEquals(path1, path3);
        assertEquals(path3, new Path(Player.DARK, tileList, T21, T10));
        assertNotEquals(path3, new Path(Player.DARK, tileList, T21, T11));
        assertNotEquals(path3, new Path(Player.DARK, tileList, T01, T10));

        Path path5 = new Path(Player.LIGHT, tileList, T01, T10);
        Path path6 = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals(path5, path5);
        assertEquals(path5, path6);
        assertEquals(path6, path5);
        assertEquals(path5, path1);
        assertEquals(path1, path5);
        assertNotEquals(path5, path3);
        assertNotEquals(path3, path5);
        assertEquals(path5, new Path(Player.LIGHT, tileList, T01, T10));
        assertNotEquals(path5, new Path(Player.LIGHT, tileList, T21, T10));
        assertNotEquals(path5, new Path(Player.LIGHT, tileList, T01, T11));

        tileList.add(T10);
        tileList.add(T11);

        Path path7 = new Path(Player.LIGHT, tileList, T01, T12);
        Path path8 = new Path(Player.LIGHT, tileList, T01, T12);
        assertEquals(path7, path7);
        assertEquals(path7, path8);
        assertEquals(path8, path7);
        assertNotEquals(path7, path1);
        assertNotEquals(path1, path7);
        assertNotEquals(path7, path3);
        assertNotEquals(path3, path7);
        assertNotEquals(path7, path5);
        assertNotEquals(path5, path7);
        assertEquals(path7, new Path(Player.LIGHT, tileList, T01, T12));
        assertNotEquals(path7, new Path(Player.LIGHT, tileList, T21, T12));
        assertNotEquals(path7, new Path(Player.LIGHT, tileList, T01, T21));

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(T11);
        tileList2.add(T10);
        tileList2.add(T00);
        Path path9 = new Path(Player.DARK, tileList2, T21, T12);
        Path path10 = new Path(Player.LIGHT, tileList2, T01, T12);
        assertEquals(path9, path9);
        assertNotEquals(path9, path1);
        assertNotEquals(path1, path9);
        assertNotEquals(path9, path3);
        assertNotEquals(path3, path9);
        assertNotEquals(path9, path5);
        assertNotEquals(path5, path9);
        assertNotEquals(path9, path7);
        assertNotEquals(path7, path9);
        assertNotEquals(path10, path7);
        assertNotEquals(path7, path10);
        assertEquals(path9, new Path(Player.DARK, tileList2, T21, T12));
        assertEquals(path10, new Path(Player.LIGHT, tileList2, T01, T12));

        Object notPath = new Object();
        assertNotEquals(path1, notPath);
        assertNotEquals(path7, notPath);
        assertNotEquals(path9, notPath);
        assertNotEquals(path10, notPath);
        assertNotEquals(path1, null);
    }

    @Test
    public void testToString() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T00);

        Path path = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals("A0", path.toString());

        path = new Path(Player.DARK, tileList, T21, T10);
        assertEquals("A0", path.toString());

        path = new Path(Player.LIGHT, tileList, T01, T11);
        assertEquals("A0", path.toString());

        tileList.add(T10);
        tileList.add(T11);

        path = new Path(Player.LIGHT, tileList, T01, T10);
        assertEquals("A0, B0, B1", path.toString());

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(T11);
        tileList2.add(T10);
        tileList2.add(T00);
        path = new Path(Player.DARK, tileList2, T21, T12);
        assertEquals("B1, B0, A0", path.toString());
    }
}
