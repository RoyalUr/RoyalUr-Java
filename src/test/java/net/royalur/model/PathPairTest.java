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

    private static final Tile T11 = new Tile(1, 1);
    private static final Tile T12 = new Tile(1, 2);
    private static final Tile T21 = new Tile(2, 1);
    private static final Tile T22 = new Tile(2, 2);
    private static final Tile T31 = new Tile(3, 1);
    private static final Tile T32 = new Tile(3, 2);

    private static class IdentifiedPathPair extends PathPair {
        private final @Nonnull String identifier;

        public IdentifiedPathPair(@Nonnull String identifier, @Nonnull Path lightPath, @Nonnull Path darkPath) {
            super(lightPath, darkPath);
            this.identifier = identifier;
        }

        @Override
        public @Nonnull String getIdentifier() {
            return identifier;
        }
    }

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
        Path lightPath = new Path(Player.LIGHT, List.of(T11), T12, T21);
        Path darkPath = new Path(Player.DARK, List.of(T31), T32, T21);

        PathPair pair = new PathPair(lightPath, darkPath);
        assertEquals(lightPath, pair.lightPath);
        assertEquals(darkPath, pair.darkPath);

        assertThrows(IllegalArgumentException.class, () -> new PathPair(darkPath, lightPath));
        assertThrows(IllegalArgumentException.class, () -> new PathPair(lightPath, lightPath));
        assertThrows(IllegalArgumentException.class, () -> new PathPair(darkPath, darkPath));
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
        Path lightPath1 = new Path(Player.LIGHT, List.of(T11), T12, T21);
        Path darkPath1 = new Path(Player.DARK, List.of(T31), T32, T21);

        PathPair pair1 = new PathPair(lightPath1, darkPath1);
        PathPair pair2 = new PathPair(lightPath1, darkPath1);
        assertEquals(pair1.hashCode(), pair2.hashCode());

        Path lightPath2 = new Path(Player.LIGHT, List.of(T11, T21), T12, T22);
        Path darkPath2 = new Path(Player.DARK, List.of(T31, T21), T32, T22);

        PathPair pair3 = new PathPair(lightPath2, darkPath2);
        PathPair pair4 = new PathPair(lightPath2, darkPath2);
        assertEquals(pair3.hashCode(), pair4.hashCode());
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testHashcode(PathPairConstructors constructors) {
        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(constructors.light.get(), constructors.dark.get());
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }

    @Test
    public void testEquals() {
        Path lightPath1 = new Path(Player.LIGHT, List.of(T11), T12, T21);
        Path darkPath1 = new Path(Player.DARK, List.of(T31), T32, T21);
        Path lightPath2 = new Path(Player.LIGHT, List.of(T11, T21), T12, T22);
        Path darkPath2 = new Path(Player.DARK, List.of(T31, T21), T32, T22);

        PathPair pair1 = new PathPair(lightPath1, darkPath1);
        PathPair pair2 = new PathPair(lightPath1, darkPath1);
        assertEquals(pair1, pair2);
        assertNotEquals(pair1, new IdentifiedPathPair("abc", lightPath1, darkPath1));
        assertNotEquals(pair1, new PathPair(lightPath2, darkPath1));
        assertNotEquals(pair1, new PathPair(lightPath1, darkPath2));
        assertNotEquals(pair1, new PathPair(lightPath2, darkPath2));

        PathPair pair3 = new PathPair(lightPath2, darkPath2);
        PathPair pair4 = new PathPair(lightPath2, darkPath2);
        assertEquals(pair3, pair4);
        assertNotEquals(pair1, pair3);
        assertNotEquals(pair3, pair1);
        assertNotEquals(pair3, new IdentifiedPathPair("def", lightPath2, darkPath2));
        assertNotEquals(pair3, new PathPair(lightPath2, darkPath1));
        assertNotEquals(pair3, new PathPair(lightPath1, darkPath2));
        assertNotEquals(pair3, new PathPair(lightPath1, darkPath1));

        Object notPathPair = new Object();
        assertNotEquals(pair1, notPathPair);
        assertNotEquals(pair3, notPathPair);
        assertNotEquals(pair1, null);
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testEquals(PathPairConstructors constructors) {
        Path genericLightPath = new Path(Player.LIGHT, List.of(T11), T12, T21);
        Path genericDarkPath = new Path(Player.DARK, List.of(T31), T32, T21);
        PathPair generic = new PathPair(genericLightPath, genericDarkPath);

        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(constructors.light.get(), constructors.dark.get());
        PathPair pair3 = new IdentifiedPathPair(constructors.name, constructors.light.get(), constructors.dark.get());
        assertNotEquals(pair1, pair2);
        assertNotEquals(pair1, generic);
        assertNotEquals(generic, pair2);
        assertNotEquals(pair2, pair3);

        Object notPathPair = new Object();
        assertNotEquals(pair1, notPathPair);
        assertNotEquals(pair2, notPathPair);
    }

    @Test
    public void testIsEquivalent() {
        Path lightPath1 = new Path(Player.LIGHT, List.of(T11), T12, T21);
        Path darkPath1 = new Path(Player.DARK, List.of(T31), T32, T21);
        Path lightPath2 = new Path(Player.LIGHT, List.of(T11, T21), T12, T22);
        Path darkPath2 = new Path(Player.DARK, List.of(T31, T21), T32, T22);

        PathPair pair1 = new PathPair(lightPath1, darkPath1);
        PathPair pair2 = new IdentifiedPathPair("2", lightPath1, darkPath1);
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair1.isEquivalent(new PathPair(lightPath1, darkPath1)));
        assertFalse(pair1.isEquivalent(new PathPair(lightPath2, darkPath1)));
        assertFalse(pair1.isEquivalent(new IdentifiedPathPair("12", lightPath1, darkPath2)));
        assertFalse(pair1.isEquivalent(new IdentifiedPathPair("22", lightPath2, darkPath2)));

        PathPair pair3 = new PathPair(lightPath2, darkPath2);
        PathPair pair4 = new PathPair(lightPath2, darkPath2);
        assertTrue(pair3.isEquivalent(pair4));
        assertFalse(pair1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(pair1));
        assertTrue(pair3.isEquivalent(new PathPair(lightPath2, darkPath2)));
        assertFalse(pair3.isEquivalent(new PathPair(lightPath2, darkPath1)));
        assertFalse(pair3.isEquivalent(new IdentifiedPathPair("12", lightPath1, darkPath2)));
        assertFalse(pair3.isEquivalent(new IdentifiedPathPair("11", lightPath1, darkPath1)));

        PathPair aseb1 = new AsebPathPair();
        PathPair aseb2 = new PathPair(new AsebPathPair.AsebLightPath(), new AsebPathPair.AsebDarkPath());
        assertTrue(aseb1.isEquivalent(aseb2));
        assertTrue(aseb2.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(aseb1));

        Path asebLightPath = new Path(
                Player.LIGHT,
                AsebPathPair.AsebLightPath.TILES,
                AsebPathPair.AsebLightPath.START_TILE,
                AsebPathPair.AsebLightPath.END_TILE
        );
        Path asebDarkPath = new Path(
                Player.DARK,
                AsebPathPair.AsebDarkPath.TILES,
                AsebPathPair.AsebDarkPath.START_TILE,
                AsebPathPair.AsebDarkPath.END_TILE
        );
        PathPair aseb3 = new PathPair(asebLightPath, asebDarkPath);
        assertTrue(aseb1.isEquivalent(aseb3));
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testIsEquivalent(PathPairConstructors constructors) {
        Path genericLightPath = new Path(Player.LIGHT, List.of(T11), T12, T21);
        Path genericDarkPath = new Path(Player.DARK, List.of(T31), T32, T21);
        PathPair generic = new PathPair(genericLightPath, genericDarkPath);

        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(constructors.light.get(), constructors.dark.get());
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair2.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(generic));
        assertFalse(generic.isEquivalent(pair1));

        Path lightPath = new Path(
                Player.LIGHT,
                pair2.lightPath.tiles,
                pair2.lightPath.startTile,
                pair2.lightPath.endTile
        );
        Path darkPath = new Path(
                Player.DARK,
                pair2.darkPath.tiles,
                pair2.lightPath.startTile,
                pair2.lightPath.endTile
        );
        PathPair pair3 = new PathPair(lightPath, darkPath);
        assertTrue(pair3.isEquivalent(pair1));
    }

    @Test
    public void testToString() {
        Path lightPath1 = new Path(Player.LIGHT, List.of(T11), T12, T21);
        Path darkPath1 = new Path(Player.DARK, List.of(T31), T32, T21);
        Path lightPath2 = new Path(Player.LIGHT, List.of(T11, T21), T12, T22);
        Path darkPath2 = new Path(Player.DARK, List.of(T31, T21), T32, T22);

        PathPair pair1 = new PathPair(lightPath1, darkPath1);
        PathPair pair2 = new IdentifiedPathPair("Pair2", lightPath1, darkPath1);
        assertEquals("Unknown Path", pair1.toString());
        assertEquals("Pair2 Path", pair2.toString());

        PathPair pair3 = new PathPair(lightPath2, darkPath2);
        PathPair pair4 = new IdentifiedPathPair("Four", lightPath2, darkPath2);
        assertEquals("Unknown Path", pair3.toString());
        assertEquals("Four Path", pair4.toString());
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testToString(PathPairConstructors constructors) {
        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(constructors.light.get(), constructors.dark.get());
        assertEquals(constructors.name + " Path", pair1.toString());
        assertEquals("Unknown Path", pair2.toString());

        Path lightPath = new Path(
                Player.LIGHT,
                pair2.lightPath.tiles,
                pair2.lightPath.startTile,
                pair2.lightPath.endTile
        );
        Path darkPath = new Path(
                Player.DARK,
                pair2.darkPath.tiles,
                pair2.darkPath.startTile,
                pair2.darkPath.endTile
        );
        PathPair pair3 = new IdentifiedPathPair("Path Pair", lightPath, darkPath);
        assertEquals("Path Pair Path", pair3.toString());

        PathPair pair4 = new IdentifiedPathPair("Test", constructors.light.get(), constructors.dark.get());
        assertEquals("Test Path", pair4.toString());
    }
}
