package net.royalur.model;

import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.path.Path;
import net.royalur.model.path.PathPair;
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
        Path lightPath = new Path(List.of(T11), T12, T21);
        Path darkPath = new Path(List.of(T31), T32, T21);

        PathPair pair = PathPair.create(lightPath, darkPath);
        assertEquals(lightPath, pair.getLight());
        assertEquals(darkPath, pair.getDark());
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testNew(PathPairConstructors constructors) {
        Path light = constructors.light.get();
        assertNotNull(light);

        Path dark = constructors.dark.get();
        assertNotNull(dark);

        PathPair pair = constructors.pair.get();
        assertNotNull(pair);
    }

    @Test
    public void testIsEquivalent() {
        Path lightPath1 = new Path(List.of(T11), T12, T21);
        Path darkPath1 = new Path(List.of(T31), T32, T21);
        Path lightPath2 = new Path(List.of(T11, T21), T12, T22);
        Path darkPath2 = new Path(List.of(T31, T21), T32, T22);

        PathPair pair1 = PathPair.create(lightPath1, darkPath1);
        PathPair pair2 = PathPair.create("2", lightPath1, darkPath1);
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair1.isEquivalent(PathPair.create(lightPath1, darkPath1)));
        assertFalse(pair1.isEquivalent(PathPair.create(lightPath2, darkPath1)));
        assertFalse(pair1.isEquivalent(PathPair.create("12", lightPath1, darkPath2)));
        assertFalse(pair1.isEquivalent(PathPair.create("22", lightPath2, darkPath2)));

        PathPair pair3 = PathPair.create(lightPath2, darkPath2);
        PathPair pair4 = PathPair.create(lightPath2, darkPath2);
        assertTrue(pair3.isEquivalent(pair4));
        assertFalse(pair1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(pair1));
        assertTrue(pair3.isEquivalent(PathPair.create(lightPath2, darkPath2)));
        assertFalse(pair3.isEquivalent(PathPair.create(lightPath2, darkPath1)));
        assertFalse(pair3.isEquivalent(PathPair.create("12", lightPath1, darkPath2)));
        assertFalse(pair3.isEquivalent(PathPair.create("11", lightPath1, darkPath1)));

        PathPair aseb1 = new AsebPathPair();
        PathPair aseb2 = PathPair.create(new AsebPathPair.AsebLightPath(), new AsebPathPair.AsebDarkPath());
        assertTrue(aseb1.isEquivalent(aseb2));
        assertTrue(aseb2.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(aseb1));

        Path asebLightPath = new Path(
                AsebPathPair.AsebLightPath.TILES,
                AsebPathPair.AsebLightPath.START_TILE,
                AsebPathPair.AsebLightPath.END_TILE
        );
        Path asebDarkPath = new Path(
                AsebPathPair.AsebDarkPath.TILES,
                AsebPathPair.AsebDarkPath.START_TILE,
                AsebPathPair.AsebDarkPath.END_TILE
        );
        PathPair aseb3 = PathPair.create(asebLightPath, asebDarkPath);
        assertTrue(aseb1.isEquivalent(aseb3));
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testIsEquivalent(PathPairConstructors constructors) {
        Path genericLightPath = new Path(List.of(T11), T12, T21);
        Path genericDarkPath = new Path(List.of(T31), T32, T21);
        PathPair generic = PathPair.create(genericLightPath, genericDarkPath);

        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = PathPair.create(constructors.light.get(), constructors.dark.get());
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair2.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(generic));
        assertFalse(generic.isEquivalent(pair1));

        Path lightPath = new Path(
                pair2.getLight().tiles,
                pair2.getLight().startTile,
                pair2.getLight().endTile
        );
        Path darkPath = new Path(
                pair2.getDark().tiles,
                pair2.getLight().startTile,
                pair2.getLight().endTile
        );
        PathPair pair3 = PathPair.create(lightPath, darkPath);
        assertTrue(pair3.isEquivalent(pair1));
    }
}
