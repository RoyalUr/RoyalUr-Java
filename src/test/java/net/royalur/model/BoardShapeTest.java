package net.royalur.model;

import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.BoardShape;
import net.royalur.model.shape.StandardBoardShape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BoardShapeTest {

    public static class BoardShapeProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(new StandardBoardShape()),
                    Arguments.of(new AsebBoardShape())
            );
        }
    }

    @Test
    public void testNewNoRosettes() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "Empty", Collections.emptySet(), Collections.emptySet()
                )
        );

        BoardShape singleTile = new BoardShape(
                "Single", Set.of(new Tile(1, 1)), Collections.emptySet()
        );
        assertEquals(1, singleTile.getWidth());
        assertEquals(1, singleTile.getHeight());
        assertEquals(1, singleTile.getArea());

        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "No1Y", Set.of(new Tile(1, 2)), Collections.emptySet()
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "No1X", Set.of(new Tile(2, 1)), Collections.emptySet()
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "No1XY", Set.of(new Tile(2, 2)), Collections.emptySet()
                )
        );

        BoardShape noZeroZero = new BoardShape(
                "No11", Set.of(new Tile(1, 2), new Tile(2, 1)), Collections.emptySet()
        );
        assertEquals(2, noZeroZero.getWidth());
        assertEquals(2, noZeroZero.getHeight());
        assertEquals(2, noZeroZero.getArea());
    }

    @Test
    public void testNewWithRosettes() {
        assertThrows(
                IllegalArgumentException.class,
                    () -> new BoardShape(
                            "Empty", Collections.emptySet(), Set.of(new Tile(1, 1))
                    )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "BadRosette", Set.of(new Tile(1, 1)), Set.of(new Tile(2, 2))
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "BadRosette",
                        Set.of(new Tile(1, 1)),
                        Set.of(new Tile(1, 1), new Tile(2, 1))
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "BadRosette",
                        Set.of(new Tile(1, 1), new Tile(1, 2)),
                        Set.of(new Tile(1, 1), new Tile(2, 1))
                )
        );

        BoardShape singleTile = new BoardShape(
                "Single", Set.of(new Tile(1, 1)), Set.of(new Tile(1, 1))
        );
        assertEquals(1, singleTile.getWidth());
        assertEquals(1, singleTile.getHeight());
        assertEquals(1, singleTile.getArea());

        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "No1Y", Set.of(new Tile(1, 2)), Set.of(new Tile(1, 2))
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "No1X", Set.of(new Tile(2, 1)), Set.of(new Tile(2, 1))
                )
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "No1XY", Set.of(new Tile(2, 2)), Set.of(new Tile(2, 2))
                )
        );

        BoardShape noZeroZero = new BoardShape(
                "No11", Set.of(new Tile(1, 2), new Tile(2, 1)), Set.of(new Tile(1, 2))
        );
        assertEquals(2, noZeroZero.getWidth());
        assertEquals(2, noZeroZero.getHeight());
        assertEquals(2, noZeroZero.getArea());

        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(
                        "BadRosette",
                        Set.of(new Tile(1, 2), new Tile(2, 1)),
                        Set.of(new Tile(1, 1))
                )
        );
    }

    @Test
    public void testBaseProperties() {
        BoardShape standard = new StandardBoardShape();
        assertEquals(3, standard.getWidth());
        assertEquals(8, standard.getHeight());
        assertEquals(20, standard.getArea());

        BoardShape aseb = new AsebBoardShape();
        assertEquals(3, aseb.getWidth());
        assertEquals(12, aseb.getHeight());
        assertEquals(20, aseb.getArea());
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testContains(BoardShape shape) {
        // Deliberately includes out-of-bounds coordinates.
        int area = 0;
        for (int ix = -1; ix <= shape.getWidth(); ++ix) {
            for (int iy = -1; iy <= shape.getHeight(); ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.getWidth() || iy >= shape.getHeight()) {
                    assertFalse(shape.containsIndices(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.contains(tile), shape.containsIndices(ix, iy));
                if (shape.contains(tile)) {
                    area += 1;
                }
            }
        }

        // Contains should have been true for an area number of tiles.
        assertEquals(shape.getArea(), area);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testContainsWithCopy(BoardShape shape) {
        // Create an untyped copy of the board shape, to ensure it acts the same for contains.
        BoardShape copy = new BoardShape("Copy", shape.getTiles(), shape.getRosetteTiles());

        // Deliberately includes out-of-bounds coordinates.
        for (int ix = -1; ix <= shape.getWidth(); ++ix) {
            for (int iy = -1; iy <= shape.getHeight(); ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.getWidth() || iy >= shape.getHeight()) {
                    assertFalse(shape.containsIndices(ix, iy));
                    assertFalse(copy.containsIndices(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.contains(tile), shape.containsIndices(ix, iy));
                assertEquals(shape.contains(tile), copy.contains(tile));
                assertEquals(shape.containsIndices(ix, iy), copy.containsIndices(ix, iy));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testIsRosette(BoardShape shape) {
        // Deliberately includes out-of-bounds coordinates.
        int rosetteCount = 0;
        for (int ix = -1; ix <= shape.getWidth(); ++ix) {
            for (int iy = -1; iy <= shape.getHeight(); ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.getWidth() || iy >= shape.getHeight()) {
                    assertFalse(shape.isRosetteIndices(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.isRosette(tile), shape.isRosetteIndices(ix, iy));
                if (shape.isRosette(tile)) {
                    rosetteCount += 1;
                }
            }
        }

        // We should have seen all rosettes.
        assertEquals(shape.getRosetteTiles().size(), rosetteCount);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testIsRosetteWithCopy(BoardShape shape) {
        // Create an untyped copy of the board shape, to ensure it acts the same for contains.
        BoardShape copy = new BoardShape("Copy", shape.getTiles(), shape.getRosetteTiles());

        // Deliberately includes out-of-bounds coordinates.
        for (int ix = -1; ix <= shape.getWidth(); ++ix) {
            for (int iy = -1; iy <= shape.getHeight(); ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.getWidth() || iy >= shape.getHeight()) {
                    assertFalse(shape.isRosetteIndices(ix, iy));
                    assertFalse(copy.isRosetteIndices(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.isRosette(tile), shape.isRosetteIndices(ix, iy));
                assertEquals(shape.isRosette(tile), copy.isRosette(tile));
                assertEquals(shape.isRosetteIndices(ix, iy), copy.isRosetteIndices(ix, iy));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testGetTilesByRow(BoardShape shape) {
        List<Tile> byRow = shape.getTilesByRow();
        assertEquals(shape.getArea(), byRow.size());

        Set<Tile> seen = new HashSet<>();
        int lastX = -1;
        int lastY = -1;
        for (Tile tile : byRow) {
            assertNotNull(tile);
            int x = tile.getX();
            int y = tile.getY();

            assertTrue(y > lastY || (y == lastY && x > lastX));
            assertTrue(shape.contains(tile));
            assertTrue(seen.add(tile));

            lastX = x;
            lastY = y;
        }
        assertEquals(shape.getArea(), seen.size());
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testGetTilesByColumn(BoardShape shape) {
        List<Tile> byCol = shape.getTilesByColumn();
        assertEquals(shape.getArea(), byCol.size());

        Set<Tile> seen = new HashSet<>();
        int lastX = -1;
        int lastY = -1;
        for (Tile tile : byCol) {
            assertNotNull(tile);
            int x = tile.getX();
            int y = tile.getY();

            assertTrue(x > lastX || (x == lastX && y > lastY));
            assertTrue(shape.contains(tile));
            assertTrue(seen.add(tile));

            lastX = x;
            lastY = y;
        }
        assertEquals(shape.getArea(), seen.size());
    }

    @Test
    public void testIsEquivalent() {
        BoardShape standard1 = new StandardBoardShape();
        BoardShape standard2 = new StandardBoardShape();
        BoardShape aseb1 = new AsebBoardShape();
        BoardShape aseb2 = new AsebBoardShape();

        assertTrue(standard1.isEquivalent(standard1));
        assertTrue(standard1.isEquivalent(standard2));
        assertTrue(standard2.isEquivalent(standard1));
        assertTrue(standard2.isEquivalent(standard2));

        assertTrue(aseb1.isEquivalent(aseb1));
        assertTrue(aseb1.isEquivalent(aseb2));
        assertTrue(aseb2.isEquivalent(aseb1));
        assertTrue(aseb2.isEquivalent(aseb2));

        assertFalse(standard1.isEquivalent(aseb1));
        assertFalse(standard1.isEquivalent(aseb2));
        assertFalse(standard2.isEquivalent(aseb1));
        assertFalse(standard2.isEquivalent(aseb2));
    }
}
