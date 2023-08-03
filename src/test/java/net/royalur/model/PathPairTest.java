package net.royalur.model;

import net.royalur.model.path.AsebPathPair;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.path.PathPair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import javax.annotation.Nonnull;
import java.util.ArrayList;
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
        public final @Nonnull Supplier<List<Tile>> lightWithStartEnd;
        public final @Nonnull Supplier<List<Tile>> darkWithStartEnd;

        public PathPairConstructors(
                @Nonnull String name,
                @Nonnull Supplier<PathPair> pair,
                @Nonnull Supplier<List<Tile>> lightWithStartEnd,
                @Nonnull Supplier<List<Tile>> darkWithStartEnd
        ) {
            this.name = name;
            this.pair = pair;
            this.lightWithStartEnd = lightWithStartEnd;
            this.darkWithStartEnd = darkWithStartEnd;
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
                            () -> AsebPathPair.LIGHT_PATH,
                            () -> AsebPathPair.DARK_PATH
                    )),
                    Arguments.of(new PathPairConstructors(
                            "Bell",
                            BellPathPair::new,
                            () -> BellPathPair.LIGHT_PATH,
                            () -> BellPathPair.DARK_PATH
                    ))
            );
        }
    }

    @Test
    public void testNew() {
        List<Tile> lightPath = List.of(T12, T11, T21);
        List<Tile> darkPath = List.of(T32, T31, T21);

        PathPair pair = new PathPair(lightPath, darkPath);
        assertEquals(lightPath, pair.getLightWithStartEnd());
        assertEquals(darkPath, pair.getDarkWithStartEnd());
        assertEquals(lightPath, pair.getWithStartEnd(Player.LIGHT));
        assertEquals(darkPath, pair.getWithStartEnd(Player.DARK));

        assertEquals(lightPath.subList(1, 2), pair.getLight());
        assertEquals(darkPath.subList(1, 2), pair.getDark());
        assertEquals(lightPath.subList(1, 2), pair.get(Player.LIGHT));
        assertEquals(darkPath.subList(1, 2), pair.get(Player.DARK));

        assertEquals(T12, pair.getLightStart());
        assertEquals(T32, pair.getDarkStart());
        assertEquals(T12, pair.getStart(Player.LIGHT));
        assertEquals(T32, pair.getStart(Player.DARK));

        assertEquals(T21, pair.getLightEnd());
        assertEquals(T21, pair.getDarkEnd());
        assertEquals(T21, pair.getEnd(Player.LIGHT));
        assertEquals(T21, pair.getEnd(Player.DARK));
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testNew(PathPairConstructors constructors) {
        List<Tile> light = constructors.lightWithStartEnd.get();
        assertNotNull(light);

        List<Tile> dark = constructors.darkWithStartEnd.get();
        assertNotNull(dark);

        PathPair pair = constructors.pair.get();
        assertNotNull(pair);
    }

    @Test
    public void testIsEquivalent() {
        List<Tile> lightPath1 = List.of(T12, T11, T21);
        List<Tile> darkPath1 = List.of(T32, T31, T21);
        List<Tile> lightPath2 = List.of(T12, T11, T21, T22);
        List<Tile> darkPath2 = List.of(T32, T31, T21, T22);

        PathPair pair1 = new PathPair(lightPath1, darkPath1);
        PathPair pair2 = PathPair.create("2", lightPath1, darkPath1);
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair1.isEquivalent(new PathPair(lightPath1, darkPath1)));
        assertFalse(pair1.isEquivalent(new PathPair(lightPath2, darkPath1)));
        assertFalse(pair1.isEquivalent(PathPair.create("12", lightPath1, darkPath2)));
        assertFalse(pair1.isEquivalent(PathPair.create("22", lightPath2, darkPath2)));

        PathPair pair3 = new PathPair(lightPath2, darkPath2);
        PathPair pair4 = new PathPair(lightPath2, darkPath2);
        assertTrue(pair3.isEquivalent(pair4));
        assertFalse(pair1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(pair1));
        assertTrue(pair3.isEquivalent(new PathPair(lightPath2, darkPath2)));
        assertFalse(pair3.isEquivalent(new PathPair(lightPath2, darkPath1)));
        assertFalse(pair3.isEquivalent(PathPair.create("12", lightPath1, darkPath2)));
        assertFalse(pair3.isEquivalent(PathPair.create("11", lightPath1, darkPath1)));

        PathPair aseb1 = new AsebPathPair();
        PathPair aseb2 = new PathPair(AsebPathPair.LIGHT_PATH, AsebPathPair.DARK_PATH);
        assertTrue(aseb1.isEquivalent(aseb2));
        assertTrue(aseb2.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(aseb1));
        assertFalse(aseb1.isEquivalent(pair3));
        assertFalse(pair3.isEquivalent(aseb1));

        List<Tile> asebLightPath = new ArrayList<>();
        asebLightPath.add(aseb1.getLightStart());
        asebLightPath.addAll(aseb1.getLight());
        asebLightPath.add(aseb1.getLightEnd());
        List<Tile> asebDarkPath = new ArrayList<>();
        asebDarkPath.add(aseb1.getDarkStart());
        asebDarkPath.addAll(aseb1.getDark());
        asebDarkPath.add(aseb1.getDarkEnd());
        PathPair aseb3 = new PathPair(asebLightPath, asebDarkPath);
        assertTrue(aseb1.isEquivalent(aseb3));

        List<Tile> asebMixedPathLight = new ArrayList<>();
        asebMixedPathLight.add(aseb1.getDarkStart());
        asebMixedPathLight.addAll(aseb1.getLight());
        asebMixedPathLight.add(aseb1.getDarkEnd());
        List<Tile> asebMixedPathDark = new ArrayList<>();
        asebMixedPathDark.add(aseb1.getLightStart());
        asebMixedPathDark.addAll(aseb1.getDark());
        asebMixedPathDark.add(aseb1.getLightEnd());
        PathPair aseb4 = new PathPair(asebMixedPathLight, asebMixedPathDark);
        assertTrue(aseb1.isEquivalent(aseb4));
    }

    @ParameterizedTest
    @ArgumentsSource(PathPairConstructorsProvider.class)
    public void testIsEquivalent(PathPairConstructors constructors) {
        List<Tile> genericLightPath = List.of(T12, T11, T21);
        List<Tile> genericDarkPath = List.of(T32, T31, T21);
        PathPair generic = new PathPair(genericLightPath, genericDarkPath);

        PathPair pair1 = constructors.pair.get();
        PathPair pair2 = new PathPair(
                constructors.lightWithStartEnd.get(),
                constructors.darkWithStartEnd.get()
        );
        assertTrue(pair1.isEquivalent(pair2));
        assertTrue(pair2.isEquivalent(pair1));
        assertFalse(pair1.isEquivalent(generic));
        assertFalse(generic.isEquivalent(pair1));

        List<Tile> lightPath = new ArrayList<>();
        lightPath.add(pair2.getLightStart());
        lightPath.addAll(pair2.getLight());
        lightPath.add(pair2.getLightEnd());
        List<Tile> darkPath = new ArrayList<>();
        darkPath.add(pair2.getDarkStart());
        darkPath.addAll(pair2.getDark());
        darkPath.add(pair2.getDarkEnd());
        PathPair pair3 = new PathPair(lightPath, darkPath);
        assertTrue(pair3.isEquivalent(pair1));

        List<Tile> mixedPathLight = new ArrayList<>();
        mixedPathLight.add(pair2.getDarkStart());
        mixedPathLight.addAll(pair2.getLight());
        mixedPathLight.add(pair2.getDarkEnd());
        List<Tile> mixedPathDark = new ArrayList<>();
        mixedPathDark.add(pair2.getLightStart());
        mixedPathDark.addAll(pair2.getDark());
        mixedPathDark.add(pair2.getLightEnd());
        PathPair pair4 = new PathPair(mixedPathLight, mixedPathDark);
        assertTrue(pair4.isEquivalent(pair1));
    }
}
