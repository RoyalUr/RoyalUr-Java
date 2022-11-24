package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PieceTest {

    @Test
    public void testOf() {
        Piece light = Piece.of(Player.LIGHT);
        Piece dark = Piece.of(Player.DARK);

        assertNotNull(light);
        assertNotNull(dark);

        assertEquals(Player.LIGHT, light.owner);
        assertEquals(Player.DARK, dark.owner);
    }

    @Test
    public void testToChar() {
        Piece light = Piece.of(Player.LIGHT);
        Piece dark = Piece.of(Player.DARK);

        assertEquals(Player.LIGHT.character, Piece.toChar(light));
        assertEquals(Player.DARK.character, Piece.toChar(dark));

        light = new Piece(Player.LIGHT);
        dark = new Piece(Player.DARK);

        assertEquals(Player.LIGHT.character, Piece.toChar(light));
        assertEquals(Player.DARK.character, Piece.toChar(dark));

        assertEquals(Player.toChar(null), Piece.toChar(null));
    }

    @Test
    public void testEquals() {
        Piece light = new Piece(Player.LIGHT);
        Piece dark = new Piece(Player.DARK);

        assertEquals(light, light);
        assertEquals(dark, dark);
        assertNotEquals(light, dark);

        assertEquals(Piece.of(Player.LIGHT), Piece.of(Player.LIGHT));
        assertEquals(Piece.of(Player.DARK), Piece.of(Player.DARK));
        assertNotEquals(Piece.of(Player.LIGHT), Piece.of(Player.DARK));

        assertEquals(Piece.of(Player.LIGHT), light);
        assertEquals(Piece.of(Player.DARK), dark);
        assertNotEquals(Piece.of(Player.LIGHT), dark);

        assertEquals(light, Piece.of(Player.LIGHT));
        assertEquals(dark, Piece.of(Player.DARK));
        assertNotEquals(light, Piece.of(Player.DARK));

        Object notPiece = new Object();
        assertNotEquals(light, notPiece);
        assertNotEquals(dark, notPiece);
        assertNotEquals(light, null);
    }

    @Test
    public void testHashCode() {
        Piece light = new Piece(Player.LIGHT);
        Piece dark = new Piece(Player.DARK);

        assertEquals(light.hashCode(), light.hashCode());
        assertEquals(dark.hashCode(), dark.hashCode());

        assertEquals(Piece.of(Player.LIGHT).hashCode(), Piece.of(Player.LIGHT).hashCode());
        assertEquals(Piece.of(Player.DARK).hashCode(), Piece.of(Player.DARK).hashCode());

        assertEquals(Piece.of(Player.LIGHT).hashCode(), light.hashCode());
        assertEquals(Piece.of(Player.DARK).hashCode(), dark.hashCode());

        assertEquals(light.hashCode(), Piece.of(Player.LIGHT).hashCode());
        assertEquals(dark.hashCode(), Piece.of(Player.DARK).hashCode());
    }

    @Test
    public void testToString() {
        Piece light = new Piece(Player.LIGHT);
        Piece dark = new Piece(Player.DARK);

        assertEquals(Player.LIGHT.name, light.toString());
        assertEquals(Player.DARK.name, dark.toString());

        assertEquals(Player.LIGHT.name, Piece.of(Player.LIGHT).toString());
        assertEquals(Player.DARK.name, Piece.of(Player.DARK).toString());
    }
}
