package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {

    @Test
    public void testOf() {
        Piece light = Piece.of(PlayerType.LIGHT);
        Piece dark = Piece.of(PlayerType.DARK);

        assertNotNull(light);
        assertNotNull(dark);

        assertEquals(PlayerType.LIGHT, light.getOwner());
        assertEquals(PlayerType.DARK, dark.getOwner());
    }

    @Test
    public void testToChar() {
        Piece light = Piece.of(PlayerType.LIGHT);
        Piece dark = Piece.of(PlayerType.DARK);

        assertEquals(PlayerType.LIGHT.getCharacter(), Piece.toChar(light));
        assertEquals(PlayerType.DARK.getCharacter(), Piece.toChar(dark));

        light = new Piece(PlayerType.LIGHT);
        dark = new Piece(PlayerType.DARK);

        assertEquals(PlayerType.LIGHT.getCharacter(), Piece.toChar(light));
        assertEquals(PlayerType.DARK.getCharacter(), Piece.toChar(dark));

        assertEquals(PlayerType.toChar(null), Piece.toChar(null));
    }

    @Test
    public void testEquals() {
        Piece light = new Piece(PlayerType.LIGHT);
        Piece dark = new Piece(PlayerType.DARK);

        assertEquals(light, light);
        assertEquals(dark, dark);
        assertNotEquals(light, dark);

        assertEquals(Piece.of(PlayerType.LIGHT), Piece.of(PlayerType.LIGHT));
        assertEquals(Piece.of(PlayerType.DARK), Piece.of(PlayerType.DARK));
        assertNotEquals(Piece.of(PlayerType.LIGHT), Piece.of(PlayerType.DARK));

        assertEquals(Piece.of(PlayerType.LIGHT), light);
        assertEquals(Piece.of(PlayerType.DARK), dark);
        assertNotEquals(Piece.of(PlayerType.LIGHT), dark);

        assertEquals(light, Piece.of(PlayerType.LIGHT));
        assertEquals(dark, Piece.of(PlayerType.DARK));
        assertNotEquals(light, Piece.of(PlayerType.DARK));

        Object notPiece = new Object();
        assertNotEquals(light, notPiece);
        assertNotEquals(dark, notPiece);
        assertNotEquals(light, null);
    }

    @Test
    public void testHashCode() {
        Piece light = new Piece(PlayerType.LIGHT);
        Piece dark = new Piece(PlayerType.DARK);

        assertEquals(light.hashCode(), light.hashCode());
        assertEquals(dark.hashCode(), dark.hashCode());

        assertEquals(Piece.of(PlayerType.LIGHT).hashCode(), Piece.of(PlayerType.LIGHT).hashCode());
        assertEquals(Piece.of(PlayerType.DARK).hashCode(), Piece.of(PlayerType.DARK).hashCode());

        assertEquals(Piece.of(PlayerType.LIGHT).hashCode(), light.hashCode());
        assertEquals(Piece.of(PlayerType.DARK).hashCode(), dark.hashCode());

        assertEquals(light.hashCode(), Piece.of(PlayerType.LIGHT).hashCode());
        assertEquals(dark.hashCode(), Piece.of(PlayerType.DARK).hashCode());
    }

    @Test
    public void testToString() {
        Piece light = new Piece(PlayerType.LIGHT);
        Piece dark = new Piece(PlayerType.DARK);

        assertEquals(PlayerType.LIGHT.getTextName(), light.toString());
        assertEquals(PlayerType.DARK.getTextName(), dark.toString());

        assertEquals(PlayerType.LIGHT.getTextName(), Piece.of(PlayerType.LIGHT).toString());
        assertEquals(PlayerType.DARK.getTextName(), Piece.of(PlayerType.DARK).toString());
    }
}
