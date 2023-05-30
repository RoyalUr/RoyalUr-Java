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
                () -> new BoardShape(Collections.emptySet(), Collections.emptySet())
        );

        BoardShape singleTile = new BoardShape(Set.of(new Tile(1, 1)), Collections.emptySet());
        assertEquals(1, singleTile.width);
        assertEquals(1, singleTile.height);
        assertEquals(1, singleTile.area);

        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(1, 2)), Collections.emptySet())
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(2, 1)), Collections.emptySet())
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(2, 2)), Collections.emptySet())
        );

        BoardShape noZeroZero = new BoardShape(Set.of(new Tile(1, 2), new Tile(2, 1)), Collections.emptySet());
        assertEquals(2, noZeroZero.width);
        assertEquals(2, noZeroZero.height);
        assertEquals(2, noZeroZero.area);
    }

    @Test
    public void testNewWithRosettes() {
        assertThrows(
                IllegalArgumentException.class,
                    () -> new BoardShape(Collections.emptySet(), Set.of(new Tile(1, 1)))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(1, 1)), Set.of(new Tile(2, 2)))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(1, 1)), Set.of(new Tile(1, 1), new Tile(2, 1)))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(1, 1), new Tile(1, 2)), Set.of(new Tile(1, 1), new Tile(2, 1)))
        );

        BoardShape singleTile = new BoardShape(Set.of(new Tile(1, 1)), Set.of(new Tile(1, 1)));
        assertEquals(1, singleTile.width);
        assertEquals(1, singleTile.height);
        assertEquals(1, singleTile.area);

        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(1, 2)), Set.of(new Tile(1, 2)))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(2, 1)), Set.of(new Tile(2, 1)))
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(2, 2)), Set.of(new Tile(2, 2)))
        );

        BoardShape noZeroZero = new BoardShape(Set.of(new Tile(1, 2), new Tile(2, 1)), Set.of(new Tile(1, 2)));
        assertEquals(2, noZeroZero.width);
        assertEquals(2, noZeroZero.height);
        assertEquals(2, noZeroZero.area);

        assertThrows(
                IllegalArgumentException.class,
                () -> new BoardShape(Set.of(new Tile(1, 2), new Tile(2, 1)), Set.of(new Tile(1, 1)))
        );
    }

    @Test
    public void testBasicProperties() {
        BoardShape standard = new StandardBoardShape();
        assertEquals(3, standard.width);
        assertEquals(8, standard.height);
        assertEquals(20, standard.area);

        BoardShape aseb = new AsebBoardShape();
        assertEquals(3, aseb.width);
        assertEquals(12, aseb.height);
        assertEquals(20, aseb.area);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testContains(BoardShape shape) {
        // Deliberately includes out-of-bounds coordinates.
        int area = 0;
        for (int ix = -1; ix <= shape.width; ++ix) {
            for (int iy = -1; iy <= shape.height; ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.width || iy >= shape.height) {
                    assertFalse(shape.contains(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.contains(tile), shape.contains(ix, iy));
                if (shape.contains(tile)) {
                    area += 1;
                }
            }
        }

        // Contains should have been true for an area number of tiles.
        assertEquals(shape.area, area);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testContainsWithCopy(BoardShape shape) {
        // Create an untyped copy of the board shape, to ensure it acts the same for contains.
        BoardShape copy = new BoardShape(shape.tiles, shape.rosetteTiles);

        // Deliberately includes out-of-bounds coordinates.
        for (int ix = -1; ix <= shape.width; ++ix) {
            for (int iy = -1; iy <= shape.height; ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.width || iy >= shape.height) {
                    assertFalse(shape.contains(ix, iy));
                    assertFalse(copy.contains(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.contains(tile), shape.contains(ix, iy));
                assertEquals(shape.contains(tile), copy.contains(tile));
                assertEquals(shape.contains(ix, iy), copy.contains(ix, iy));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testIsRosette(BoardShape shape) {
        // Deliberately includes out-of-bounds coordinates.
        int rosetteCount = 0;
        for (int ix = -1; ix <= shape.width; ++ix) {
            for (int iy = -1; iy <= shape.height; ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.width || iy >= shape.height) {
                    assertFalse(shape.isRosette(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.isRosette(tile), shape.isRosette(ix, iy));
                if (shape.isRosette(tile)) {
                    rosetteCount += 1;
                }
            }
        }

        // We should have seen all rosettes.
        assertEquals(shape.rosetteTiles.size(), rosetteCount);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testIsRosetteWithCopy(BoardShape shape) {
        // Create an untyped copy of the board shape, to ensure it acts the same for contains.
        BoardShape copy = new BoardShape(shape.tiles, shape.rosetteTiles);

        // Deliberately includes out-of-bounds coordinates.
        for (int ix = -1; ix <= shape.width; ++ix) {
            for (int iy = -1; iy <= shape.height; ++iy) {
                if (ix < 0 || iy < 0 || ix >= shape.width || iy >= shape.height) {
                    assertFalse(shape.isRosette(ix, iy));
                    assertFalse(copy.isRosette(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(shape.isRosette(tile), shape.isRosette(ix, iy));
                assertEquals(shape.isRosette(tile), copy.isRosette(tile));
                assertEquals(shape.isRosette(ix, iy), copy.isRosette(ix, iy));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testGetTilesByRow(BoardShape shape) {
        List<Tile> byRow = shape.getTilesByRow();
        assertEquals(shape.area, byRow.size());

        Set<Tile> seen = new HashSet<>();
        Tile last = null;
        for (Tile tile : byRow) {
            assertNotNull(tile);

            if (last != null) {
                assertTrue(tile.y > last.y || (tile.y == last.y && tile.x > last.x));
            }
            assertTrue(shape.contains(tile));
            assertTrue(seen.add(tile));
            last = tile;
        }
        assertEquals(shape.area, seen.size());
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testGetTilesByColumn(BoardShape shape) {
        List<Tile> byCol = shape.getTilesByColumn();
        assertEquals(shape.area, byCol.size());

        Set<Tile> seen = new HashSet<>();
        Tile last = null;
        for (Tile tile : byCol) {
            assertNotNull(tile);

            if (last != null) {
                assertTrue(tile.x > last.x || (tile.x == last.x && tile.y > last.y));
            }
            assertTrue(shape.contains(tile));
            assertTrue(seen.add(tile));
            last = tile;
        }
        assertEquals(shape.area, seen.size());
    }

    @Test
    public void testHashCode() {
        BoardShape standard1 = new StandardBoardShape();
        BoardShape standard2 = new StandardBoardShape();
        BoardShape aseb1 = new AsebBoardShape();
        BoardShape aseb2 = new AsebBoardShape();

        assertEquals(standard1.hashCode(), standard1.hashCode());
        assertEquals(standard1.hashCode(), standard2.hashCode());
        assertEquals(standard2.hashCode(), standard1.hashCode());
        assertEquals(standard2.hashCode(), standard2.hashCode());

        assertEquals(aseb1.hashCode(), aseb1.hashCode());
        assertEquals(aseb1.hashCode(), aseb2.hashCode());
        assertEquals(aseb2.hashCode(), aseb1.hashCode());
        assertEquals(aseb2.hashCode(), aseb2.hashCode());
    }

    @Test
    public void testEquals() {
        BoardShape standard1 = new StandardBoardShape();
        BoardShape standard2 = new StandardBoardShape();
        BoardShape aseb1 = new AsebBoardShape();
        BoardShape aseb2 = new AsebBoardShape();

        assertEquals(standard1, standard1);
        assertEquals(standard1, standard2);
        assertEquals(standard2, standard1);
        assertEquals(standard2, standard2);

        assertEquals(aseb1, aseb2);
        assertEquals(aseb1, aseb2);
        assertEquals(aseb2, aseb1);
        assertEquals(aseb2, aseb2);

        assertNotEquals(standard1, aseb1);
        assertNotEquals(standard1, aseb2);
        assertNotEquals(standard2, aseb1);
        assertNotEquals(standard2, aseb2);

        Object notShape = new Object();
        assertNotEquals(standard1, notShape);
        assertNotEquals(standard2, notShape);
        assertNotEquals(aseb1, notShape);
        assertNotEquals(aseb2, notShape);
        assertNotEquals(standard1, null);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeProvider.class)
    public void testToString(BoardShape shape) {
        assertEquals(shape.getIdentifier() + " Board Shape", shape.toString());
    }
}
