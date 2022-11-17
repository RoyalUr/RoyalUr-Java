package net.royalur.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void testBasicProperties() {
        Board standard = new Board(new StandardBoardShape());
        assertEquals(3, standard.width);
        assertEquals(8, standard.height);
        assertEquals(20, standard.area);

        Board aseb = new Board(new AsebBoardShape());
        assertEquals(3, aseb.width);
        assertEquals(12, aseb.height);
        assertEquals(20, aseb.area);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeTest.BoardShapeProvider.class)
    public void testCopy(BoardShape shape) {
        Board board = new Board(shape);
        Board copy = board.copy();
        assertEquals(board, copy);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeTest.BoardShapeProvider.class)
    public void testContains(BoardShape shape) {
        Board board = new Board(shape);

        // Deliberately includes out-of-bounds coordinates.
        int area = 0;
        for (int x = -1; x <= board.width; ++x) {
            for (int y = -1; y <= board.height; ++y) {
                if (x < 0 || y < 0 || x >= board.width || y >= board.height) {
                    assertFalse(board.contains(x, y));
                    continue;
                }

                Tile tile = new Tile(x, y);
                assertEquals(board.contains(tile), board.contains(x, y));
                if (board.contains(tile)) {
                    area += 1;
                }
            }
        }

        // Contains should have been true for an area number of tiles.
        assertEquals(board.area, area);
    }

    @Test
    public void testEquals() {
        Board standard1 = new Board(new StandardBoardShape());
        Board standard2 = new Board(new StandardBoardShape());
        Board aseb1 = new Board(new AsebBoardShape());
        Board aseb2 = new Board(new AsebBoardShape());

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

        standard1.set(0, 0, Player.LIGHT);
        assertEquals(standard1, standard1);
        assertNotEquals(standard1, standard2);
        assertNotEquals(standard1, aseb1);

        standard2.set(0, 0, Player.LIGHT);
        assertEquals(standard2, standard2);
        assertEquals(standard1, standard2);
        assertNotEquals(standard2, aseb1);

        standard1.set(2, 6, Player.DARK);
        assertEquals(standard1, standard1);
        assertNotEquals(standard1, standard2);
        assertNotEquals(standard1, aseb1);

        standard2.set(2, 6, Player.LIGHT);
        assertEquals(standard2, standard2);
        assertNotEquals(standard1, standard2);
        assertNotEquals(standard2, aseb1);

        aseb1.set(1, 10, Player.DARK);
        assertEquals(aseb1, aseb1);
        assertNotEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);

        aseb1.set(1, 10, null);
        assertEquals(aseb1, aseb1);
        assertEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);

        aseb1.set(1, 10, Player.LIGHT);
        aseb2.set(1, 10, Player.DARK);
        assertEquals(aseb1, aseb1);
        assertNotEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);

        aseb2.set(1, 10, Player.LIGHT);
        assertEquals(aseb1, aseb1);
        assertEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);
    }
}
