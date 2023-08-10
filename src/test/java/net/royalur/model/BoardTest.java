package net.royalur.model;

import net.royalur.model.shape.AsebBoardShape;
import net.royalur.model.shape.BoardShape;
import net.royalur.model.shape.StandardBoardShape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void testBasicProperties() {
        Board<Piece> standard = new Board<>(new StandardBoardShape());
        assertEquals(3, standard.getWidth());
        assertEquals(8, standard.getHeight());

        Board<Piece> aseb = new Board<>(new AsebBoardShape());
        assertEquals(3, aseb.getWidth());
        assertEquals(12, aseb.getHeight());
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeTest.BoardShapeProvider.class)
    public void testCopy(BoardShape shape) {
        Board<Piece> board = new Board<>(shape);
        Board<Piece> copy = board.copy();
        assertEquals(board, copy);

        for (Tile tile : shape.getTilesByRow()) {
            for (PlayerType player : PlayerType.values())  {
                Piece piece = Piece.of(player);
                copy = board.copy();
                copy.set(tile, piece);
                assertNotEquals(board, copy);
                assertEquals(copy, copy.copy());
            }
        }

        for (PlayerType player : PlayerType.values()) {
            Piece piece = Piece.of(player);

            copy = board.copy();
            for (Tile tile : shape.getTilesByColumn()) {
                board.set(tile, piece);
            }
            assertNotEquals(board, copy);
            for (Tile tile : shape.getTilesByColumn()) {
                assertNotEquals(piece, copy.get(tile));
            }
        }
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeTest.BoardShapeProvider.class)
    public void testContains(BoardShape shape) {
        Board<Piece> board = new Board<>(shape);

        // Deliberately includes out-of-bounds coordinates.
        int area = 0;
        for (int ix = -1; ix <= board.getWidth(); ++ix) {
            for (int iy = -1; iy <= board.getHeight(); ++iy) {
                if (ix < 0 || iy < 0 || ix >= board.getWidth() || iy >= board.getHeight()) {
                    assertFalse(board.contains(ix, iy));
                    continue;
                }

                Tile tile = Tile.fromIndices(ix, iy);
                assertEquals(board.contains(tile), board.contains(ix, iy));
                if (board.contains(tile)) {
                    area += 1;
                }
            }
        }

        // Contains should have been true for an area number of tiles.
        assertEquals(board.getShape().getArea(), area);
    }

    @ParameterizedTest
    @ArgumentsSource(BoardShapeTest.BoardShapeProvider.class)
    public void testGetSet(BoardShape shape) {
        Board<Piece> board1 = new Board<>(shape);
        Board<Piece> board2 = new Board<>(shape);

        for (Tile tile : shape.getTilesByColumn()) {
            assertNull(board2.get(tile.getXIndex(), tile.getYIndex()));
            assertNull(board1.get(tile));
        }

        for (PlayerType player : PlayerType.values()) {
            Piece piece = Piece.of(player);
            // Deliberately includes out-of-bounds coordinates.
            for (int ix = -1; ix <= shape.getWidth(); ++ix) {
                for (int iy = -1; iy <= shape.getHeight(); ++iy) {
                    // Copies to be used in Lambda expressions.
                    int tileX = ix, tileY = iy;

                    if (ix < 0 || iy < 0 || ix >= shape.getWidth() || iy >= shape.getHeight()) {
                        assertThrows(IllegalArgumentException.class, () -> board1.get(tileX, tileY));
                        assertThrows(IllegalArgumentException.class, () -> board1.get(tileX, tileY));
                        assertThrows(IllegalArgumentException.class, () -> board1.set(tileX, tileY, piece));
                        continue;
                    }

                    Tile tile = Tile.fromIndices(ix, iy);
                    if (shape.contains(tile)) {
                        board1.set(tileX, tileY, piece);
                        board2.set(tile, piece);
                        assertEquals(piece, board1.get(tileX, tileY));
                        assertEquals(piece, board2.get(tile));
                    } else {
                        assertThrows(IllegalArgumentException.class, () -> board1.get(tileX, tileY));
                        assertThrows(IllegalArgumentException.class, () -> board2.get(tile));
                        assertThrows(IllegalArgumentException.class, () -> board1.set(tileX, tileY, piece));
                        assertThrows(IllegalArgumentException.class, () -> board2.set(tile, piece));
                    }
                }
            }

            for (Tile tile : shape.getTilesByColumn()) {
                assertEquals(piece, board2.get(tile.getXIndex(), tile.getYIndex()));
                assertEquals(piece, board1.get(tile));
            }
        }
    }

    @Test
    public void testEquals() {
        Board<Piece> standard1 = new Board<>(new StandardBoardShape());
        Board<Piece> standard2 = new Board<>(new StandardBoardShape());
        Board<Piece> aseb1 = new Board<>(new AsebBoardShape());
        Board<Piece> aseb2 = new Board<>(new AsebBoardShape());

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

        standard1.set(0, 0, Piece.of(PlayerType.LIGHT));
        assertEquals(standard1, standard1);
        assertNotEquals(standard1, standard2);
        assertNotEquals(standard1, aseb1);

        standard2.set(0, 0, Piece.of(PlayerType.LIGHT));
        assertEquals(standard2, standard2);
        assertEquals(standard1, standard2);
        assertNotEquals(standard2, aseb1);

        standard1.set(2, 6, Piece.of(PlayerType.DARK));
        assertEquals(standard1, standard1);
        assertNotEquals(standard1, standard2);
        assertNotEquals(standard1, aseb1);

        standard2.set(2, 6, Piece.of(PlayerType.LIGHT));
        assertEquals(standard2, standard2);
        assertNotEquals(standard1, standard2);
        assertNotEquals(standard2, aseb1);

        aseb1.set(1, 10, Piece.of(PlayerType.DARK));
        assertEquals(aseb1, aseb1);
        assertNotEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);

        aseb1.set(1, 10, null);
        assertEquals(aseb1, aseb1);
        assertEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);

        aseb1.set(1, 10, Piece.of(PlayerType.LIGHT));
        aseb2.set(1, 10, Piece.of(PlayerType.DARK));
        assertEquals(aseb1, aseb1);
        assertNotEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);

        aseb2.set(1, 10, Piece.of(PlayerType.LIGHT));
        assertEquals(aseb1, aseb1);
        assertEquals(aseb1, aseb2);
        assertNotEquals(aseb1, standard1);

        Object notBoard = new Object();
        assertNotEquals(standard1, notBoard);
        assertNotEquals(standard2, notBoard);
        assertNotEquals(aseb1, notBoard);
        assertNotEquals(aseb2, notBoard);
        assertNotEquals(standard1, null);
    }

    @Test
    public void testToString() {
        Board<Piece> standard1 = new Board<>(new StandardBoardShape());
        Board<Piece> standard2 = new Board<>(new StandardBoardShape());
        Board<Piece> aseb1 = new Board<>(new AsebBoardShape());
        Board<Piece> aseb2 = new Board<>(new AsebBoardShape());

        assertEquals(
                """
                ....  ..
                ........
                ....  ..""",
                standard1.toString()
        );
        assertEquals(
                """
                ....       \s
                ............
                ....       \s""",
                aseb1.toString()
        );

        assertEquals(standard1.toString(), standard1.toString());
        assertEquals(standard1.toString(), standard2.toString());
        assertEquals(standard2.toString(), standard1.toString());
        assertEquals(standard2.toString(), standard2.toString());

        assertEquals(aseb1.toString(), aseb2.toString());
        assertEquals(aseb1.toString(), aseb2.toString());
        assertEquals(aseb2.toString(), aseb1.toString());
        assertEquals(aseb2.toString(), aseb2.toString());

        assertNotEquals(standard1.toString(), aseb1.toString());
        assertNotEquals(standard1.toString(), aseb2.toString());
        assertNotEquals(standard2.toString(), aseb1.toString());
        assertNotEquals(standard2.toString(), aseb2.toString());

        standard1.set(0, 0, Piece.of(PlayerType.LIGHT));
        assertEquals(
                """
                L...  ..
                ........
                ....  ..""",
                standard1.toString()
        );
        assertEquals(standard1.toString(), standard1.toString());
        assertNotEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard1.toString(), aseb1.toString());

        standard2.set(0, 0, Piece.of(PlayerType.LIGHT));
        assertEquals(standard2.toString(), standard2.toString());
        assertEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard2.toString(), aseb1.toString());

        standard1.set(2, 6, Piece.of(PlayerType.DARK));
        assertEquals(
                """
                L...  ..
                ........
                ....  D.""",
                standard1.toString()
        );
        assertEquals(standard1.toString(), standard1.toString());
        assertNotEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard1.toString(), aseb1.toString());

        standard2.set(2, 6, Piece.of(PlayerType.LIGHT));
        assertEquals(
                """
                L...  ..
                ........
                ....  L.""",
                standard2.toString()
        );
        assertEquals(standard2.toString(), standard2.toString());
        assertNotEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard2.toString(), aseb1.toString());

        aseb1.set(1, 10, Piece.of(PlayerType.DARK));
        assertEquals(
                """
                ....       \s
                ..........D.
                ....       \s""",
                aseb1.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertNotEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        aseb1.set(1, 10, null);
        assertEquals(
                """
                ....       \s
                ............
                ....       \s""",
                aseb1.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        aseb1.set(1, 10, Piece.of(PlayerType.LIGHT));
        assertEquals(
                """
                ....       \s
                ..........L.
                ....       \s""",
                aseb1.toString()
        );
        aseb2.set(1, 10, Piece.of(PlayerType.DARK));
        assertEquals(
                """
                ....       \s
                ..........D.
                ....       \s""",
                aseb2.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertNotEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        aseb2.set(1, 10, Piece.of(PlayerType.LIGHT));
        assertEquals(
                """
                ....       \s
                ..........L.
                ....       \s""",
                aseb2.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        for (Tile tile : standard1.getShape().getTilesByRow()) {
            standard1.set(tile, Piece.of((tile.getX() + tile.getY()) % 2 == 0 ? PlayerType.LIGHT : PlayerType.DARK));
        }
        assertEquals(
                """
                LDLD  LD
                DLDLDLDL
                LDLD  LD""",
                standard1.toString()
        );

        for (Tile tile : aseb1.getShape().getTilesByRow()) {
            aseb1.set(tile, Piece.of((tile.getX() + tile.getY()) % 2 == 0 ? PlayerType.LIGHT : PlayerType.DARK));
        }
        assertEquals(
                """
                LDLD       \s
                DLDLDLDLDLDL
                LDLD       \s""",
                aseb1.toString()
        );
    }
}
