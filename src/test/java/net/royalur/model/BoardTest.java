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

        for (Tile tile : shape.getTilesByRow()) {
            for (Player player : Player.values())  {
                copy = board.copy();
                copy.set(tile, player);
                assertNotEquals(board, copy);
                assertEquals(copy, copy.copy());
            }
        }

        for (Player player : Player.values()) {
            copy = board.copy();
            for (Tile tile : shape.getTilesByColumn()) {
                board.set(tile, player);
            }
            assertNotEquals(board, copy);
            for (Tile tile : shape.getTilesByColumn()) {
                assertNotEquals(player, copy.get(tile));
            }
        }
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

    @ParameterizedTest
    @ArgumentsSource(BoardShapeTest.BoardShapeProvider.class)
    public void testGetSet(BoardShape shape) {
        Board board1 = new Board(shape);
        Board board2 = new Board(shape);

        for (Tile tile : shape.getTilesByColumn()) {
            assertNull(board2.get(tile.x, tile.y));
            assertNull(board1.get(tile));
        }

        for (Player player : Player.values()) {
            // Deliberately includes out-of-bounds coordinates.
            for (int x = -1; x <= shape.width; ++x) {
                for (int y = -1; y <= shape.height; ++y) {
                    // Copies to be used in Lambda expressions.
                    int tileX = x, tileY = y;

                    if (x < 0 || y < 0 || x >= shape.width || y >= shape.height) {
                        assertThrows(IllegalArgumentException.class, () -> board1.get(tileX, tileY));
                        assertThrows(IllegalArgumentException.class, () -> board1.get(tileX, tileY));
                        assertThrows(IllegalArgumentException.class, () -> board1.set(tileX, tileY, player));
                        continue;
                    }

                    Tile tile = new Tile(x, y);
                    if (shape.contains(tile)) {
                        board1.set(tileX, tileY, player);
                        board2.set(tile, player);
                        assertEquals(player, board1.get(tileX, tileY));
                        assertEquals(player, board2.get(tile));
                    } else {
                        assertThrows(IllegalArgumentException.class, () -> board1.get(tileX, tileY));
                        assertThrows(IllegalArgumentException.class, () -> board2.get(tile));
                        assertThrows(IllegalArgumentException.class, () -> board1.set(tileX, tileY, player));
                        assertThrows(IllegalArgumentException.class, () -> board2.set(tile, player));
                    }
                }
            }

            for (Tile tile : shape.getTilesByColumn()) {
                assertEquals(player, board2.get(tile.x, tile.y));
                assertEquals(player, board1.get(tile));
            }
        }
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

        Object notBoard = new Object();
        assertNotEquals(standard1, notBoard);
        assertNotEquals(standard2, notBoard);
        assertNotEquals(aseb1, notBoard);
        assertNotEquals(aseb2, notBoard);
    }

    @Test
    public void testToString() {
        Board standard1 = new Board(new StandardBoardShape());
        Board standard2 = new Board(new StandardBoardShape());
        Board aseb1 = new Board(new AsebBoardShape());
        Board aseb2 = new Board(new AsebBoardShape());

        assertEquals(
                "....  ..\n" +
                "........\n" +
                "....  ..",
                standard1.toString()
        );
        assertEquals(
                "....        \n" +
                "............\n" +
                "....        ",
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

        standard1.set(0, 0, Player.LIGHT);
        assertEquals(
                "L...  ..\n" +
                "........\n" +
                "....  ..",
                standard1.toString()
        );
        assertEquals(standard1.toString(), standard1.toString());
        assertNotEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard1.toString(), aseb1.toString());

        standard2.set(0, 0, Player.LIGHT);
        assertEquals(standard2.toString(), standard2.toString());
        assertEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard2.toString(), aseb1.toString());

        standard1.set(2, 6, Player.DARK);
        assertEquals(
                "L...  ..\n" +
                "........\n" +
                "....  D.",
                standard1.toString()
        );
        assertEquals(standard1.toString(), standard1.toString());
        assertNotEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard1.toString(), aseb1.toString());

        standard2.set(2, 6, Player.LIGHT);
        assertEquals(
                "L...  ..\n" +
                "........\n" +
                "....  L.",
                standard2.toString()
        );
        assertEquals(standard2.toString(), standard2.toString());
        assertNotEquals(standard1.toString(), standard2.toString());
        assertNotEquals(standard2.toString(), aseb1.toString());

        aseb1.set(1, 10, Player.DARK);
        assertEquals(
                "....        \n" +
                "..........D.\n" +
                "....        ",
                aseb1.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertNotEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        aseb1.set(1, 10, null);
        assertEquals(
                "....        \n" +
                "............\n" +
                "....        ",
                aseb1.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        aseb1.set(1, 10, Player.LIGHT);
        assertEquals(
                "....        \n" +
                "..........L.\n" +
                "....        ",
                aseb1.toString()
        );
        aseb2.set(1, 10, Player.DARK);
        assertEquals(
                "....        \n" +
                "..........D.\n" +
                "....        ",
                aseb2.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertNotEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        aseb2.set(1, 10, Player.LIGHT);
        assertEquals(
                "....        \n" +
                "..........L.\n" +
                "....        ",
                aseb2.toString()
        );
        assertEquals(aseb1.toString(), aseb1.toString());
        assertEquals(aseb1.toString(), aseb2.toString());
        assertNotEquals(aseb1.toString(), standard1.toString());

        for (Tile tile : standard1.shape.getTilesByRow()) {
            standard1.set(tile, (tile.x + tile.y) % 2 == 0 ? Player.LIGHT : Player.DARK);
        }
        assertEquals(
                "LDLD  LD\n" +
                "DLDLDLDL\n" +
                "LDLD  LD",
                standard1.toString()
        );

        for (Tile tile : aseb1.shape.getTilesByRow()) {
            aseb1.set(tile, (tile.x + tile.y) % 2 == 0 ? Player.LIGHT : Player.DARK);
        }
        assertEquals(
                "LDLD        \n" +
                "DLDLDLDLDLDL\n" +
                "LDLD        ",
                aseb1.toString()
        );
    }
}