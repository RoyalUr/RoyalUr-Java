package net.royalur.model;

import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class PathPairTest {

    private static final Tile T00 = new Tile(0, 0);
    private static final Tile T01 = new Tile(0, 1);
    private static final Tile T10 = new Tile(1, 0);
    private static final Tile T11 = new Tile(1, 1);
    private static final Tile T20 = new Tile(2, 0);
    private static final Tile T21 = new Tile(2, 1);

    public static class PathPairConstructors {
        public final @Nonnull String name;
        public final @Nonnull Supplier<PathPair> pair;
        public final @Nonnull Supplier<Path> light;
        public final @Nonnull Supplier<Path> dark;

        public PathPairConstructors(
                @Nonnull String name,
                @Nonnull Supplier<PathPair> pair,
                @Nonnull Supplier<Path> light,
                @Nonnull Supplier<Path> dark
        ) {
            this.name = name;
            this.pair = pair;
            this.light = light;
            this.dark = dark;
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }
    }

    public static class PathPairConstructorsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new PathPairConstructors(
                            "Aseb",
                            AsebPathPair::new,
                            AsebPathPair.AsebLightPath::new,
                            AsebPathPair.AsebDarkPath::new
                    )),
                    Arguments.of(new PathPairConstructors(
                            "Bell",
                            BellPathPair::new,
                            BellPathPair.BellLightPath::new,
                            BellPathPair.BellDarkPath::new
                    ))
            );
        }
    }

    @Test
    public void testNew() {
        Path lightPath = new Path("lightPath", Player.LIGHT, List.of(T00), T01, T10);
        Path darkPath = new Path("darkPath", Player.DARK, List.of(T20), T21, T10);

        PathPair pair = new PathPair("pathPair", lightPath, darkPath);
        assertEquals("pathPair", pair.name);
        assertEquals(lightPath, pair.lightPath);
        assertEquals(darkPath, pair.darkPath);

        assertThrows(IllegalArgumentException.class, () -> new PathPair("pathPair", darkPath, lightPath));
        assertThrows(IllegalArgumentException.class, () -> new PathPair("pathPair", lightPath, lightPath));
        assertThrows(IllegalArgumentException.class, () -> new PathPair("pathPair", darkPath, darkPath));
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testNew(PathPairConstructors constructors) {
        Path light = constructors.light.get();
        assertNotNull(light);
        assertEquals(Player.LIGHT, light.player);

        Path dark = constructors.dark.get();
        assertNotNull(dark);
        assertEquals(Player.DARK, dark.player);

        PathPair pair = constructors.pair.get();
        assertNotNull(pair);
    }

    @Test
    public void testHashcode() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(T00), T01, T10);
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(T20), T21, T10);

        PathPair pair1 = new PathPair("pathPair", lightPath1, darkPath1);
        PathPair pair2 = new PathPair("pathPair", lightPath1, darkPath1);
        assertEquals(pair1.hashCode(), pair2.hashCode());

        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(T00, T10), T01, T11);
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(T20, T10), T21, T11);

        PathPair pair3 = new PathPair("path-pair", lightPath2, darkPath2);
        PathPair pair4 = new PathPair("path-pair", lightPath2, darkPath2);
        assertEquals(pair3.hashCode(), pair4.hashCode());
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testHashcode(PathPairConstructors constructors) {
        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(pair1.name, constructors.light.get(), constructors.dark.get());
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }

    @Test
    public void testEquals() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(T00), T01, T10);
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(T20), T21, T10);
        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(T00, T10), T01, T11);
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(T20, T10), T21, T11);

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

        Object notPathPair = new Object();
        assertNotEquals(pair1, notPathPair);
        assertNotEquals(pair3, notPathPair);
        assertNotEquals(pair1, null);
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testEquals(PathPairConstructors constructors) {
        Path genericLightPath = new Path("lightPath", Player.LIGHT, List.of(T00), T01, T10);
        Path genericDarkPath = new Path("darkPath", Player.DARK, List.of(T20), T21, T10);
        PathPair generic = new PathPair("pathPair", genericLightPath, genericDarkPath);

        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(pair1.name, constructors.light.get(), constructors.dark.get());
        assertNotEquals(pair1, pair2);
        assertNotEquals(pair1, generic);
        assertNotEquals(generic, pair2);

        Object notPathPair = new Object();
        assertNotEquals(pair1, notPathPair);
        assertNotEquals(pair2, notPathPair);
    }

    @Test
    public void testIsEquivalent() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(T00), T01, T10);
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(T20), T21, T10);
        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(T00, T10), T01, T11);
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(T20, T10), T21, T11);

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

        Path asebLightPath = new Path(
                "lightPath", Player.LIGHT,
                AsebPathPair.AsebLightPath.TILES,
                AsebPathPair.AsebLightPath.START_TILE,
                AsebPathPair.AsebLightPath.END_TILE
        );
        Path asebDarkPath = new Path(
                "darkPath",
                Player.DARK,
                AsebPathPair.AsebDarkPath.TILES,
                AsebPathPair.AsebDarkPath.START_TILE,
                AsebPathPair.AsebDarkPath.END_TILE
        );
        PathPair aseb3 = new PathPair("pathPair", asebLightPath, asebDarkPath);
        assertTrue(aseb1.isEquivalent(aseb3));
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testIsEquivalent(PathPairConstructors constructors) {
        Path genericLightPath = new Path("lightPath", Player.LIGHT, List.of(T00), T01, T10);
        Path genericDarkPath = new Path("darkPath", Player.DARK, List.of(T20), T21, T10);
        PathPair generic = new PathPair("pathPair", genericLightPath, genericDarkPath);

        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(pair1.name, constructors.light.get(), constructors.dark.get());
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair2.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(generic));
        assertFalse(generic.isEquivalent(pair1));

        Path lightPath = new Path(
                "lightPath", Player.LIGHT,
                pair2.lightPath.tiles,
                pair2.lightPath.startTile,
                pair2.lightPath.endTile
        );
        Path darkPath = new Path(
                "darkPath", Player.DARK,
                pair2.darkPath.tiles,
                pair2.lightPath.startTile,
                pair2.lightPath.endTile
        );
        PathPair pair3 = new PathPair("pathPair", lightPath, darkPath);
        assertTrue(pair3.isEquivalent(pair1));
    }

    @Test
    public void testToString() {
        Path lightPath1 = new Path("lightPath", Player.LIGHT, List.of(T00), T01, T10);
        Path darkPath1 = new Path("darkPath", Player.DARK, List.of(T20), T21, T10);
        Path lightPath2 = new Path("light-path", Player.LIGHT, List.of(T00, T10), T01, T11);
        Path darkPath2 = new Path("dark-path", Player.DARK, List.of(T20, T10), T21, T11);

        PathPair pair1 = new PathPair("pathPair", lightPath1, darkPath1);
        PathPair pair2 = new PathPair("pathPair", lightPath1, darkPath1);
        assertEquals("pathPair (Light: lightPath path, Dark: darkPath path)", pair1.toString());
        assertEquals("pathPair (Light: lightPath path, Dark: darkPath path)", pair2.toString());

        PathPair pair3 = new PathPair("path-pair", lightPath2, darkPath2);
        PathPair pair4 = new PathPair("path-pair", lightPath2, darkPath2);
        assertEquals("path-pair (Light: light-path path, Dark: dark-path path)", pair3.toString());
        assertEquals("path-pair (Light: light-path path, Dark: dark-path path)", pair4.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testToString(PathPairConstructors constructors) {
        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(pair1.name, constructors.light.get(), constructors.dark.get());
        assertEquals(pair1.name, pair1.toString());
        assertEquals(pair1.name, pair2.toString());

        Path lightPath = new Path(
                "lightPath", Player.LIGHT,
                pair2.lightPath.tiles,
                pair2.lightPath.startTile,
                pair2.lightPath.endTile
        );
        Path darkPath = new Path(
                "darkPath", Player.DARK,
                pair2.darkPath.tiles,
                pair2.darkPath.startTile,
                pair2.darkPath.endTile
        );
        PathPair pair3 = new PathPair("pathPair", lightPath, darkPath);
        assertEquals("pathPair (Light: lightPath path, Dark: darkPath path)", pair3.toString());

        PathPair pair4 = new PathPair("test", constructors.light.get(), constructors.dark.get());
        assertEquals("test (of " + pair1.name + " paths)", pair4.toString());
    }
}
