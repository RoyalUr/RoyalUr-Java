package net.royalur.model;

import net.royalur.model.path.Path;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    private static final Tile T11 = new Tile(1, 1);
    private static final Tile T12 = new Tile(1, 2);
    private static final Tile T21 = new Tile(2, 1);
    private static final Tile T22 = new Tile(2, 2);
    private static final Tile T32 = new Tile(3, 2);
    private static final Tile T23 = new Tile(2, 3);

    @Test
    public void testNew() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T11);

        Path path = new Path(tileList, T12, T21);
        assertEquals(1, path.length);
        assertEquals(tileList, path.tiles);
        assertNotSame(tileList, path.tiles);
        assertEquals(T12, path.startTile);
        assertEquals(T21, path.endTile);

        tileList.add(T22);
        assertNotEquals(tileList, path.tiles);
        assertEquals(1, path.length);

        path = new Path(tileList, T12, T32);
        assertEquals(2, path.length);
        assertEquals(tileList, path.tiles);
        assertNotSame(tileList, path.tiles);
        assertEquals(T12, path.startTile);
        assertEquals(T32, path.endTile);

        tileList.add(T23);
        assertNotEquals(tileList, path.tiles);
        assertEquals(2, path.length);

        assertThrows(IllegalArgumentException.class, () -> new Path(List.of(), T12, T32));
    }

    @Test
    public void testGet() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T11);

        Path path1 = new Path(tileList, T12, T21);
        assertEquals(T11, path1.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> path1.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> path1.get(1));

        tileList.add(T12);
        tileList.add(T22);
        tileList.add(T32);

        Path path2 = new Path(tileList, T12, T21);
        assertEquals(T11, path2.get(0));
        assertEquals(T12, path2.get(1));
        assertEquals(T22, path2.get(2));
        assertEquals(T32, path2.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> path2.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> path2.get(4));
    }

    @Test
    public void testIterator() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T11);

        Path path = new Path(tileList, T12, T21);
        Iterator<Tile> iter = path.iterator();
        assertTrue(iter.hasNext());
        assertEquals(T11, iter.next());
        assertFalse(iter.hasNext());

        tileList.add(T12);
        tileList.add(T22);
        tileList.add(T32);
        assertFalse(iter.hasNext());

        path = new Path(tileList, T12, T21);
        iter = path.iterator();
        assertTrue(iter.hasNext());
        assertEquals(T11, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(T12, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(T22, iter.next());
        assertTrue(iter.hasNext());
        assertEquals(T32, iter.next());
        assertFalse(iter.hasNext());
    }

    @Test
    public void testHashCode() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T11);

        Path path1 = new Path(tileList, T12, T21);
        Path path2 = new Path(tileList, T12, T21);
        assertEquals(path1.hashCode(), path2.hashCode());

        path1 = new Path(tileList, T12, T22);
        path2 = new Path(tileList, T12, T22);
        assertEquals(path1.hashCode(), path2.hashCode());

        path1 = new Path(tileList, T32, T21);
        path2 = new Path(tileList, T32, T21);
        assertEquals(path1.hashCode(), path2.hashCode());

        tileList.add(T21);
        tileList.add(T22);

        path1 = new Path(tileList, T32, T22);
        path2 = new Path(tileList, T32, T22);
        assertEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    public void testIsEquivalent() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T11);

        Path path1 = new Path(tileList, T12, T21);
        Path path2 = new Path(tileList, T12, T21);
        assertTrue(path1.isEquivalent(path2));
        assertTrue(path2.isEquivalent(path1));

        Path path3 = new Path(tileList, T12, T22);
        Path path4 = new Path(tileList, T12, T22);
        assertTrue(path3.isEquivalent(path4));
        assertTrue(path4.isEquivalent(path3));
        assertTrue(path1.isEquivalent(path4));
        assertTrue(path3.isEquivalent(path1));
        assertTrue(path2.isEquivalent(path4));
        assertTrue(path3.isEquivalent(path2));

        Path path5 = new Path(tileList, T32, T21);
        Path path6 = new Path(tileList, T32, T21);
        assertTrue(path5.isEquivalent(path6));
        assertTrue(path6.isEquivalent(path5));
        assertTrue(path1.isEquivalent(path5));
        assertTrue(path5.isEquivalent(path1));
        assertTrue(path4.isEquivalent(path6));
        assertTrue(path6.isEquivalent(path4));

        tileList.add(T21);
        tileList.add(T22);

        Path path7 = new Path(tileList, T32, T22);
        Path path8 = new Path(tileList, T32, T22);
        assertTrue(path7.isEquivalent(path8));
        assertTrue(path8.isEquivalent(path7));
        assertFalse(path7.isEquivalent(path1));
        assertFalse(path7.isEquivalent(path3));
        assertFalse(path7.isEquivalent(path5));

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(T22);
        tileList2.add(T21);
        tileList2.add(T11);
        Path path9 = new Path(tileList2, T32, T22);
        assertTrue(path9.isEquivalent(path9));
        assertFalse(path9.isEquivalent(path1));
        assertFalse(path1.isEquivalent(path9));
        assertFalse(path9.isEquivalent(path7));
        assertFalse(path7.isEquivalent(path9));
    }

    @Test
    public void testEquals() {
        List<Tile> tileList = new ArrayList<>();
        tileList.add(T11);

        Path path1 = new Path(tileList, T12, T21);
        Path path2 = new Path(tileList, T12, T21);
        assertEquals(path1, path2);
        assertEquals(path2, path1);
        assertEquals(path1, new Path(tileList, T12, T21));
        assertEquals(path1, new Path(tileList, T12, T21));
        assertNotEquals(path1, new Path(tileList, T12, T22));
        assertNotEquals(path1, new Path(tileList, T32, T21));
        assertNotEquals(path1, new Path(tileList, T32, T22));

        Path path3 = new Path(tileList, T32, T21);
        Path path4 = new Path(tileList, T32, T21);
        assertEquals(path3, path4);
        assertEquals(path4, path3);
        assertNotEquals(path3, path1);
        assertNotEquals(path1, path3);
        assertEquals(path3, new Path(tileList, T32, T21));
        assertNotEquals(path3, new Path(tileList, T32, T22));
        assertNotEquals(path3, new Path(tileList, T12, T21));

        Path path5 = new Path(tileList, T12, T21);
        Path path6 = new Path(tileList, T12, T21);
        assertEquals(path5, path6);
        assertEquals(path6, path5);
        assertEquals(path5, path1);
        assertEquals(path1, path5);
        assertNotEquals(path5, path3);
        assertNotEquals(path3, path5);
        assertEquals(path5, new Path(tileList, T12, T21));
        assertNotEquals(path5, new Path(tileList, T32, T21));
        assertNotEquals(path5, new Path(tileList, T12, T22));

        tileList.add(T21);
        tileList.add(T22);

        Path path7 = new Path(tileList, T12, T23);
        Path path8 = new Path(tileList, T12, T23);
        assertEquals(path7, path8);
        assertEquals(path8, path7);
        assertNotEquals(path7, path1);
        assertNotEquals(path1, path7);
        assertNotEquals(path7, path3);
        assertNotEquals(path3, path7);
        assertNotEquals(path7, path5);
        assertNotEquals(path5, path7);
        assertEquals(path7, new Path(tileList, T12, T23));
        assertNotEquals(path7, new Path(tileList, T32, T23));
        assertNotEquals(path7, new Path(tileList, T12, T32));

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(T22);
        tileList2.add(T21);
        tileList2.add(T11);
        Path path9 = new Path(tileList2, T32, T23);
        Path path10 = new Path(tileList2, T12, T23);
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
        assertEquals(path9, new Path(tileList2, T32, T23));
        assertEquals(path10, new Path(tileList2, T12, T23));

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
        tileList.add(T11);

        Path path = new Path(tileList, T12, T21);
        assertEquals("A1", path.toString());

        path = new Path(tileList, T32, T21);
        assertEquals("A1", path.toString());

        path = new Path(tileList, T12, T22);
        assertEquals("A1", path.toString());

        tileList.add(T21);
        tileList.add(T22);

        path = new Path(tileList, T12, T21);
        assertEquals("A1, B1, B2", path.toString());

        List<Tile> tileList2 = new ArrayList<>();
        tileList2.add(T22);
        tileList2.add(T21);
        tileList2.add(T11);
        path = new Path(tileList2, T32, T23);
        assertEquals("B2, B1, A1", path.toString());
    }
}
