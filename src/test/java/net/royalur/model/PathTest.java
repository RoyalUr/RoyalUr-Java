package net.royalur.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    @Test
    public void testNew() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(new Tile(0, 0));

        Path path = new Path("path", Player.LIGHT, tileList);
        assertEquals("path", path.name);
        assertEquals(Player.LIGHT, path.player);
        assertEquals(1, path.length);
        assertEquals(tileList, path.tiles);
        assertNotSame(tileList, path.tiles);

        tileList.add(new Tile(1, 1));
        assertNotEquals(tileList, path.tiles);
        assertEquals(1, path.length);

        path = new Path("", Player.DARK, tileList);
        assertEquals("", path.name);
        assertEquals(Player.DARK, path.player);
        assertEquals(tileList, path.tiles);
        assertNotSame(tileList, path.tiles);
        assertEquals(2, path.length);

        tileList.add(new Tile(1, 2));
        assertNotEquals(tileList, path.tiles);
        assertEquals(2, path.length);

        assertThrows(IllegalArgumentException.class, () -> new Path("path", Player.LIGHT, List.of()));
    }

    @Test
    public void testGet() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(new Tile(0, 0));

        Path path1 = new Path("path", Player.LIGHT, tileList);
        assertEquals(new Tile(0, 0), path1.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> path1.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> path1.get(1));

        tileList.add(new Tile(0, 1));
        tileList.add(new Tile(1, 1));
        tileList.add(new Tile(2, 1));

        Path path2 = new Path("path", Player.LIGHT, tileList);
        assertEquals(new Tile(0, 0), path2.get(0));
        assertEquals(new Tile(0, 1), path2.get(1));
        assertEquals(new Tile(1, 1), path2.get(2));
        assertEquals(new Tile(2, 1), path2.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> path2.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> path2.get(4));
    }

    @Test
    public void testIterator() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(new Tile(0, 0));

        Path path = new Path("path", Player.LIGHT, tileList);
        Iterator<Tile> iter = path.iterator();
        assertTrue(iter.hasNext());
        assertEquals(new Tile(0, 0), iter.next());
        assertFalse(iter.hasNext());

        tileList.add(new Tile(0, 1));
        tileList.add(new Tile(1, 1));
        tileList.add(new Tile(2, 1));
        assertFalse(iter.hasNext());

        path = new Path("path", Player.LIGHT, tileList);
        iter = path.iterator();
        assertTrue(iter.hasNext());
        assertEquals(new Tile(0, 0), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(new Tile(0, 1), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(new Tile(1, 1), iter.next());
        assertTrue(iter.hasNext());
        assertEquals(new Tile(2, 1), iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testHashCode() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(new Tile(0, 0));

        Path path1 = new Path("path", Player.LIGHT, tileList);
        Path path2 = new Path("path", Player.LIGHT, tileList);
        assertEquals(path1.hashCode(), path2.hashCode());

        path1 = new Path("path", Player.DARK, tileList);
        path2 = new Path("path", Player.DARK, tileList);
        assertEquals(path1.hashCode(), path2.hashCode());

        path1 = new Path("", Player.LIGHT, tileList);
        path2 = new Path("", Player.LIGHT, tileList);
        assertEquals(path1.hashCode(), path2.hashCode());

        tileList.add(new Tile(1, 0));
        tileList.add(new Tile(1, 1));

        path1 = new Path("path", Player.LIGHT, tileList);
        path2 = new Path("path", Player.LIGHT, tileList);
        assertEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    public void testIsEquivalent() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(new Tile(0, 0));

        Path path1 = new Path("path", Player.LIGHT, tileList);
        Path path2 = new Path("path", Player.LIGHT, tileList);
        assertTrue(path1.isEquivalent(path2));
        assertTrue(path2.isEquivalent(path1));

        Path path3 = new Path("path", Player.DARK, tileList);
        Path path4 = new Path("path", Player.DARK, tileList);
        assertTrue(path3.isEquivalent(path4));
        assertTrue(path4.isEquivalent(path3));
        assertTrue(path1.isEquivalent(path4));
        assertTrue(path3.isEquivalent(path1));
        assertTrue(path2.isEquivalent(path4));
        assertTrue(path3.isEquivalent(path2));

        Path path5 = new Path("", Player.LIGHT, tileList);
        Path path6 = new Path("", Player.LIGHT, tileList);
        assertTrue(path5.isEquivalent(path6));
        assertTrue(path6.isEquivalent(path5));
        assertTrue(path1.isEquivalent(path5));
        assertTrue(path5.isEquivalent(path1));
        assertTrue(path4.isEquivalent(path6));
        assertTrue(path6.isEquivalent(path4));

        tileList.add(new Tile(1, 0));
        tileList.add(new Tile(1, 1));

        Path path7 = new Path("path", Player.LIGHT, tileList);
        Path path8 = new Path("path", Player.LIGHT, tileList);
        assertTrue(path7.isEquivalent(path8));
        assertTrue(path8.isEquivalent(path7));
        assertFalse(path7.isEquivalent(path1));
        assertFalse(path7.isEquivalent(path3));
        assertFalse(path7.isEquivalent(path5));

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(new Tile(1, 1));
        tileList2.add(new Tile(1, 0));
        tileList2.add(new Tile(0, 0));
        Path path9 = new Path("Path", Player.DARK, tileList2);
        assertTrue(path9.isEquivalent(path9));
        assertFalse(path9.isEquivalent(path1));
        assertFalse(path1.isEquivalent(path9));
        assertFalse(path9.isEquivalent(path7));
        assertFalse(path7.isEquivalent(path9));
    }

    @Test
    public void testEquals() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(new Tile(0, 0));

        Path path1 = new Path("path", Player.LIGHT, tileList);
        Path path2 = new Path("path", Player.LIGHT, tileList);
        assertEquals(path1, path1);
        assertEquals(path1, path2);
        assertEquals(path2, path1);
        assertNotEquals(path1, new Path("Path", Player.LIGHT, tileList));
        assertNotEquals(path1, new Path("", Player.LIGHT, tileList));

        Path path3 = new Path("path", Player.DARK, tileList);
        Path path4 = new Path("path", Player.DARK, tileList);
        assertEquals(path3, path3);
        assertEquals(path3, path4);
        assertEquals(path4, path3);
        assertNotEquals(path3, path1);
        assertNotEquals(path1, path3);
        assertNotEquals(path3, new Path("Path", Player.DARK, tileList));

        Path path5 = new Path("", Player.LIGHT, tileList);
        Path path6 = new Path("", Player.LIGHT, tileList);
        assertEquals(path5, path5);
        assertEquals(path5, path6);
        assertEquals(path6, path5);
        assertNotEquals(path5, path1);
        assertNotEquals(path1, path5);
        assertNotEquals(path5, path3);
        assertNotEquals(path3, path5);
        assertNotEquals(path5, new Path("Path", Player.LIGHT, tileList));

        tileList.add(new Tile(1, 0));
        tileList.add(new Tile(1, 1));

        Path path7 = new Path("path", Player.LIGHT, tileList);
        Path path8 = new Path("path", Player.LIGHT, tileList);
        assertEquals(path7, path7);
        assertEquals(path7, path8);
        assertEquals(path8, path7);
        assertNotEquals(path7, path1);
        assertNotEquals(path1, path7);
        assertNotEquals(path7, path3);
        assertNotEquals(path3, path7);
        assertNotEquals(path7, path5);
        assertNotEquals(path5, path7);
        assertNotEquals(path7, new Path("Path", Player.LIGHT, tileList));

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(new Tile(1, 1));
        tileList2.add(new Tile(1, 0));
        tileList2.add(new Tile(0, 0));
        Path path9 = new Path("Path", Player.DARK, tileList2);
        Path path10 = new Path("path", Player.LIGHT, tileList2);
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
        assertNotEquals(path9, new Path("path", Player.DARK, tileList2));
        assertNotEquals(path10, new Path("Path", Player.LIGHT, tileList2));

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
        tileList.add(new Tile(0, 0));

        Path path = new Path("path", Player.LIGHT, tileList);
        assertEquals("path (Light): A0", path.toString());

        path = new Path("path", Player.DARK, tileList);
        assertEquals("path (Dark): A0", path.toString());

        path = new Path("", Player.LIGHT, tileList);
        assertEquals("Light: A0", path.toString());

        tileList.add(new Tile(1, 0));
        tileList.add(new Tile(1, 1));

        path = new Path("path", Player.LIGHT, tileList);
        assertEquals("path (Light): A0, B0, B1", path.toString());

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(new Tile(1, 1));
        tileList2.add(new Tile(1, 0));
        tileList2.add(new Tile(0, 0));
        path = new Path("Path", Player.DARK, tileList2);
        assertEquals("Path (Dark): B1, B0, A0", path.toString());
    }
}
