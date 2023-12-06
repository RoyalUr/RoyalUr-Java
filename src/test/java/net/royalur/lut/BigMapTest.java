package net.royalur.lut;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BigMapTest {

    @Test
    public void testPutGetNegative() {
        BigMap map = new BigMap(BigMap.INT, BigMap.INT, 2);
        map.put(-1, 0);
        for (BigMap.Entry entry : map) {
            System.out.println(entry.key + " or " + ((int) entry.key) + ": " + entry.value);
        }
        assertEquals(0, map.getInt(-1));
    }

    @Test
    public void testPutGet() {
        BigMap map = new BigMap(BigMap.INT, BigMap.INT, 2);
        assertEquals(0, map.getEntryCount());

        map.put(5, 3);
        assertEquals(1, map.getEntryCount());
        assertEquals(3, map.getInt(5));

        map.put(10, 5);
        map.put(11, 6);
        assertEquals(3, map.getEntryCount());
        assertEquals(3, map.getInt(5));
        assertEquals(5, map.getInt(10));
        assertEquals(6, map.getInt(11));

        map.put(-10, 15);
        assertEquals(4, map.getEntryCount());
        assertEquals(3, map.getInt(5));
        assertEquals(5, map.getInt(10));
        assertEquals(6, map.getInt(11));
        assertEquals(15, map.getInt(-10));
    }

    @Test
    public void testSort() {
        BigMap map = new BigMap(BigMap.INT, BigMap.INT, 2);
        assertEquals(0, map.getEntryCount());

        map.put(5, 3);
        map.put(10, 5);
        map.put(11, 6);
        map.sort();

        assertEquals(3, map.getEntryCount());
        assertEquals(3, map.getInt(5));
        assertEquals(5, map.getInt(10));
        assertEquals(6, map.getInt(11));
    }
}
