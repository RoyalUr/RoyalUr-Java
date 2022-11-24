package net.royalur.model;

import net.royalur.model.path.AsebPathPair;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PathPairTest {

    @Test
    public void testNew() {
        Path lightPath = new Path("lightPath", Player.LIGHT, List.of(new Tile(0, 0)));
        Path darkPath = new Path("darkPath", Player.DARK, List.of(new Tile(2, 0)));

        PathPair pair = new PathPair("pathPair", lightPath, darkPath);
        assertEquals("pathPair", pair.name);
        assertEquals(lightPath, pair.lightPath);
        assertEquals(darkPath, pair.darkPath);

        assertThrows(IllegalArgumentException.class, () -> new PathPair("pathPair", darkPath, lightPath));
        assertThrows(IllegalArgumentException.class, () -> new PathPair("pathPair", lightPath, lightPath));
        assertThrows(IllegalArgumentException.class, () -> new PathPair("pathPair", darkPath, darkPath));
    }

    @Test
    public void testHashcode() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(new Tile(0, 0)));
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(new Tile(2, 0)));

        PathPair pair1 = new PathPair("pathPair", lightPath1, darkPath1);
        PathPair pair2 = new PathPair("pathPair", lightPath1, darkPath1);
        assertEquals(pair1.hashCode(), pair2.hashCode());

        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(new Tile(0, 0), new Tile(1, 0)));
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(new Tile(2, 0), new Tile(1, 0)));

        PathPair pair3 = new PathPair("path-pair", lightPath2, darkPath2);
        PathPair pair4 = new PathPair("path-pair", lightPath2, darkPath2);
        assertEquals(pair3.hashCode(), pair4.hashCode());

        PathPair aseb1 = new AsebPathPair();
        PathPair aseb2 = new PathPair("Aseb", new AsebPathPair.AsebLightPath(), new AsebPathPair.AsebDarkPath());
        assertEquals(aseb1.hashCode(), aseb2.hashCode());
    }

    @Test
    public void testEquals() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(new Tile(0, 0)));
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(new Tile(2, 0)));
        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(new Tile(0, 0), new Tile(1, 0)));
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(new Tile(2, 0), new Tile(1, 0)));

        PathPair pair1 = new PathPair("pathPair", lightPath1, darkPath1);
        PathPair pair2 = new PathPair("pathPair", lightPath1, darkPath1);
        assertEquals(pair1, pair2);
        assertNotEquals(pair1, new PathPair("path-pair", lightPath1, darkPath1));
        assertNotEquals(pair1, new PathPair("pathPair", lightPath2, darkPath1));
        assertNotEquals(pair1, new PathPair("pathPair", lightPath1, darkPath2));
        assertNotEquals(pair1, new PathPair("pathPair", lightPath2, darkPath2));

        PathPair pair3 = new PathPair("path-pair", lightPath2, darkPath2);
        PathPair pair4 = new PathPair("path-pair", lightPath2, darkPath2);
        assertEquals(pair3, pair4);
        assertNotEquals(pair1, pair3);
        assertNotEquals(pair3, pair1);
        assertNotEquals(pair3, new PathPair("pathPair", lightPath2, darkPath2));
        assertNotEquals(pair3, new PathPair("path-pair", lightPath2, darkPath1));
        assertNotEquals(pair3, new PathPair("path-pair", lightPath1, darkPath2));
        assertNotEquals(pair3, new PathPair("path-pair", lightPath1, darkPath1));

        PathPair aseb1 = new AsebPathPair();
        PathPair aseb2 = new PathPair("Aseb", new AsebPathPair.AsebLightPath(), new AsebPathPair.AsebDarkPath());
        assertNotEquals(aseb1, aseb2);
        assertNotEquals(aseb1, pair1);
        assertNotEquals(pair1, aseb1);
        assertNotEquals(aseb1, pair3);
        assertNotEquals(pair3, aseb1);

        Object notPathPair = new Object();
        assertNotEquals(pair1, notPathPair);
        assertNotEquals(pair3, notPathPair);
        assertNotEquals(aseb1, notPathPair);
        assertNotEquals(aseb2, notPathPair);
        assertNotEquals(pair1, null);
    }

    @Test
    public void testIsEquivalent() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(new Tile(0, 0)));
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(new Tile(2, 0)));
        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(new Tile(0, 0), new Tile(1, 0)));
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(new Tile(2, 0), new Tile(1, 0)));

        PathPair pair1 = new PathPair("pathPair", lightPath1, darkPath1);
        PathPair pair2 = new PathPair("pathPair", lightPath1, darkPath1);
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair1.isEquivalent(new PathPair("path-pair", lightPath1, darkPath1)));
        assertFalse(pair1.isEquivalent(new PathPair("pathPair", lightPath2, darkPath1)));
        assertFalse(pair1.isEquivalent(new PathPair("pathPair", lightPath1, darkPath2)));
        assertFalse(pair1.isEquivalent(new PathPair("pathPair", lightPath2, darkPath2)));

        PathPair pair3 = new PathPair("path-pair", lightPath2, darkPath2);
        PathPair pair4 = new PathPair("path-pair", lightPath2, darkPath2);
        assertTrue(pair3.isEquivalent(pair4));
        assertFalse(pair1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(pair1));
        assertTrue(pair3.isEquivalent(new PathPair("pathPair", lightPath2, darkPath2)));
        assertFalse(pair3.isEquivalent(new PathPair("path-pair", lightPath2, darkPath1)));
        assertFalse(pair3.isEquivalent(new PathPair("path-pair", lightPath1, darkPath2)));
        assertFalse(pair3.isEquivalent(new PathPair("path-pair", lightPath1, darkPath1)));

        PathPair aseb1 = new AsebPathPair();
        PathPair aseb2 = new PathPair("Aseb", new AsebPathPair.AsebLightPath(), new AsebPathPair.AsebDarkPath());
        assertTrue(aseb1.isEquivalent(aseb2));
        assertTrue(aseb2.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(aseb1));

        Path asebLightPath = new Path("lightPath", Player.LIGHT, AsebPathPair.AsebLightPath.TILES);
        Path asebDarkPath = new Path("darkPath", Player.DARK, AsebPathPair.AsebDarkPath.TILES);
        PathPair aseb3 = new PathPair("pathPair", asebLightPath, asebDarkPath);
        assertTrue(aseb1.isEquivalent(aseb3));
    }

    @Test
    public void testToString() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(new Tile(0, 0)));
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(new Tile(2, 0)));
        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(new Tile(0, 0), new Tile(1, 0)));
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(new Tile(2, 0), new Tile(1, 0)));

        PathPair pair1 = new PathPair("pathPair", lightPath1, darkPath1);
        PathPair pair2 = new PathPair("pathPair", lightPath1, darkPath1);
        assertEquals("pathPair (Light: lightPath path, Dark: darkPath path)", pair1.toString());
        assertEquals("pathPair (Light: lightPath path, Dark: darkPath path)", pair2.toString());

        PathPair pair3 = new PathPair("path-pair", lightPath2, darkPath2);
        PathPair pair4 = new PathPair("path-pair", lightPath2, darkPath2);
        assertEquals("path-pair (Light: light-path path, Dark: dark-path path)", pair3.toString());
        assertEquals("path-pair (Light: light-path path, Dark: dark-path path)", pair4.toString());

        PathPair aseb1 = new AsebPathPair();
        PathPair aseb2 = new PathPair("Aseb", new AsebPathPair.AsebLightPath(), new AsebPathPair.AsebDarkPath());
        assertEquals("Aseb", aseb1.toString());
        assertEquals("Aseb", aseb2.toString());

        Path asebLightPath = new Path("lightPath", Player.LIGHT, AsebPathPair.AsebLightPath.TILES);
        Path asebDarkPath = new Path("darkPath", Player.DARK, AsebPathPair.AsebDarkPath.TILES);
        PathPair aseb3 = new PathPair("pathPair", asebLightPath, asebDarkPath);
        assertEquals("pathPair (Light: lightPath path, Dark: darkPath path)", aseb3.toString());

        PathPair aseb4 = new PathPair("test", new AsebPathPair.AsebLightPath(), new AsebPathPair.AsebDarkPath());
        assertEquals("test (of Aseb paths)", aseb4.toString());
    }
}
