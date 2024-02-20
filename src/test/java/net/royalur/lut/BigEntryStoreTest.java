package net.royalur.lut;

import static org.junit.jupiter.api.Assertions.*;

import net.royalur.lut.buffer.ValueType;
import net.royalur.lut.store.ChunkStore;
import org.junit.jupiter.api.Test;

public class BigEntryStoreTest {

    @Test
    public void testPutGetNegative() {
        ChunkStore map = new ChunkStore(ValueType.UINT32, ValueType.UINT32, 2);
        map.addEntry(-1, 0);
        assertEquals(0, map.getInt(-1));
    }

    @Test
    public void testPutGet() {
        ChunkStore map = new ChunkStore(ValueType.UINT32, ValueType.UINT32, 2);
        assertEquals(0, map.getEntryCount());

        map.addEntry(5, 3);
        assertEquals(1, map.getEntryCount());
        assertEquals(3, map.getInt(5));

        map.addEntry(10, 5);
        map.addEntry(11, 6);
        assertEquals(3, map.getEntryCount());
        assertEquals(3, map.getInt(5));
        assertEquals(5, map.getInt(10));
        assertEquals(6, map.getInt(11));

        map.addEntry(-10, 15);
        assertEquals(4, map.getEntryCount());
        assertEquals(3, map.getInt(5));
        assertEquals(5, map.getInt(10));
        assertEquals(6, map.getInt(11));
        assertEquals(15, map.getInt(-10));
    }

    @Test
    public void testSort() {
        ChunkStore map = new ChunkStore(ValueType.UINT32, ValueType.UINT32, 2);
        assertEquals(0, map.getEntryCount());

        map.addEntry(5, 3);
        map.addEntry(10, 5);
        map.addEntry(11, 6);
        map.sort();

        assertEquals(3, map.getEntryCount());
        assertEquals(3, map.getInt(5));
        assertEquals(5, map.getInt(10));
        assertEquals(6, map.getInt(11));
    }
}
