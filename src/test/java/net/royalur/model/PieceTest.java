package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {

    @Test
    public void testToChar() {
        Piece light = new Piece(PlayerType.LIGHT, 1);
        Piece dark = new Piece(PlayerType.DARK, 1);

        assertEquals(PlayerType.LIGHT.getChar(), Piece.toChar(light));
        assertEquals(PlayerType.DARK.getChar(), Piece.toChar(dark));

        light = new Piece(PlayerType.LIGHT, 2);
        dark = new Piece(PlayerType.DARK, 2);

        assertEquals(PlayerType.LIGHT.getChar(), Piece.toChar(light));
        assertEquals(PlayerType.DARK.getChar(), Piece.toChar(dark));

        assertEquals(PlayerType.toChar(null), Piece.toChar(null));
    }

    @Test
    public void testEquals() {
        Piece light = new Piece(PlayerType.LIGHT, 1);
        Piece dark = new Piece(PlayerType.DARK, 1);

        assertEquals(light, light);
        assertEquals(dark, dark);
        assertNotEquals(light, dark);

        assertEquals(new Piece(PlayerType.LIGHT, 1), new Piece(PlayerType.LIGHT, 1));
        assertEquals(new Piece(PlayerType.DARK, 1), new Piece(PlayerType.DARK, 1));
        assertNotEquals(new Piece(PlayerType.LIGHT, 1), new Piece(PlayerType.DARK, 1));

        assertEquals(new Piece(PlayerType.LIGHT, 1), light);
        assertEquals(new Piece(PlayerType.DARK, 1), dark);
        assertNotEquals(new Piece(PlayerType.LIGHT, 1), dark);

        assertEquals(light, new Piece(PlayerType.LIGHT, 1));
        assertEquals(dark, new Piece(PlayerType.DARK, 1));
        assertNotEquals(light, new Piece(PlayerType.DARK, 1));

        assertEquals(new Piece(PlayerType.LIGHT, 1), new Piece(PlayerType.LIGHT, 1));
        assertEquals(new Piece(PlayerType.LIGHT, 5), new Piece(PlayerType.LIGHT, 5));
        assertNotEquals(new Piece(PlayerType.LIGHT, 1), new Piece(PlayerType.LIGHT, 5));

        assertEquals(new Piece(PlayerType.DARK, 1), new Piece(PlayerType.DARK, 1));
        assertEquals(new Piece(PlayerType.DARK, 8), new Piece(PlayerType.DARK, 8));
        assertNotEquals(new Piece(PlayerType.DARK, 1), new Piece(PlayerType.DARK, 8));

        Object notPiece = new Object();
        assertNotEquals(light, notPiece);
        assertNotEquals(dark, notPiece);
        assertNotEquals(light, null);
    }

    @Test
    public void testHashCode() {
        Piece light = new Piece(PlayerType.LIGHT, 1);
        Piece dark = new Piece(PlayerType.DARK, 2);

        assertEquals(light.hashCode(), light.hashCode());
        assertEquals(dark.hashCode(), dark.hashCode());

        assertEquals(
                new Piece(PlayerType.LIGHT, 1).hashCode(),
                new Piece(PlayerType.LIGHT, 1).hashCode()
        );
        assertEquals(
                new Piece(PlayerType.DARK, 2).hashCode(),
                new Piece(PlayerType.DARK, 2).hashCode()
        );

        assertEquals(new Piece(PlayerType.LIGHT, 1).hashCode(), light.hashCode());
        assertEquals(new Piece(PlayerType.DARK, 2).hashCode(), dark.hashCode());

        assertEquals(light.hashCode(), new Piece(PlayerType.LIGHT, 1).hashCode());
        assertEquals(dark.hashCode(), new Piece(PlayerType.DARK, 2).hashCode());
    }

    @Test
    public void testToString() {
        Piece light = new Piece(PlayerType.LIGHT, 1);
        Piece dark = new Piece(PlayerType.DARK, 2);

        assertEquals(PlayerType.LIGHT.getTextName(), light.toString());
        assertEquals(PlayerType.DARK.getTextName(), dark.toString());

        light = new Piece(PlayerType.LIGHT, 3);
        dark = new Piece(PlayerType.DARK, 4);

        assertEquals(PlayerType.LIGHT.getTextName(), light.toString());
        assertEquals(PlayerType.DARK.getTextName(), dark.toString());
    }
}
