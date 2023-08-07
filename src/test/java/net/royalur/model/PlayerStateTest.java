package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerStateTest {

    @Test
    public void testNew() {
        PlayerState player = new PlayerState(PlayerType.LIGHT, 1, 1);
        assertEquals(PlayerType.LIGHT, player.getPlayer());
        assertEquals(1, player.getPieceCount());
        assertEquals(1, player.getScore());

        player = new PlayerState(PlayerType.DARK, 2, 3);
        assertEquals(PlayerType.DARK, player.getPlayer());
        assertEquals(2, player.getPieceCount());
        assertEquals(3, player.getScore());

        player = new PlayerState(PlayerType.LIGHT, 5, 4);
        assertEquals(PlayerType.LIGHT, player.getPlayer());
        assertEquals(5, player.getPieceCount());
        assertEquals(4, player.getScore());

        player = new PlayerState(PlayerType.DARK, 10, 11);
        assertEquals(PlayerType.DARK, player.getPlayer());
        assertEquals(10, player.getPieceCount());
        assertEquals(11, player.getScore());

        assertThrows(IllegalArgumentException.class, () -> new PlayerState(PlayerType.LIGHT, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> new PlayerState(PlayerType.LIGHT, 1, -1));
        assertThrows(IllegalArgumentException.class, () -> new PlayerState(PlayerType.DARK, -9, -9));
    }

    @Test
    public void testHashCode() {
        PlayerState one = new PlayerState(PlayerType.LIGHT, 2, 3);
        PlayerState two = new PlayerState(PlayerType.DARK, 3, 2);
        PlayerState three = new PlayerState(PlayerType.LIGHT, 1, 1);
        PlayerState four = new PlayerState(PlayerType.DARK, 9, 9);

        assertEquals(one.hashCode(), one.hashCode());
        assertEquals(two.hashCode(), two.hashCode());
        assertEquals(three.hashCode(), three.hashCode());
        assertEquals(four.hashCode(), four.hashCode());

        assertEquals(one.hashCode(), new PlayerState(PlayerType.LIGHT, 2, 3).hashCode());
        assertEquals(two.hashCode(), new PlayerState(PlayerType.DARK, 3, 2).hashCode());
        assertEquals(three.hashCode(), new PlayerState(PlayerType.LIGHT, 1, 1).hashCode());
        assertEquals(four.hashCode(), new PlayerState(PlayerType.DARK, 9, 9).hashCode());
    }

    @Test
    public void testEquals() {
        PlayerState one = new PlayerState(PlayerType.LIGHT, 2, 3);
        PlayerState two = new PlayerState(PlayerType.DARK, 3, 2);
        PlayerState three = new PlayerState(PlayerType.LIGHT, 1, 1);
        PlayerState four = new PlayerState(PlayerType.DARK, 9, 9);

        assertEquals(one, one);
        assertEquals(two, two);
        assertEquals(three, three);
        assertEquals(four, four);

        assertEquals(one, new PlayerState(PlayerType.LIGHT, 2, 3));
        assertEquals(two, new PlayerState(PlayerType.DARK, 3, 2));
        assertEquals(three, new PlayerState(PlayerType.LIGHT, 1, 1));
        assertEquals(four, new PlayerState(PlayerType.DARK, 9, 9));

        assertNotEquals(one, two);
        assertNotEquals(one, three);
        assertNotEquals(one, four);

        assertNotEquals(two, one);
        assertNotEquals(two, three);
        assertNotEquals(two, four);

        assertNotEquals(three, one);
        assertNotEquals(three, two);
        assertNotEquals(three, four);

        assertNotEquals(four, one);
        assertNotEquals(four, two);
        assertNotEquals(four, three);

        Object notPlayerState = new Object();
        assertNotEquals(one, notPlayerState);
        assertNotEquals(two, notPlayerState);
        assertNotEquals(three, notPlayerState);
        assertNotEquals(four, notPlayerState);
        assertNotEquals(one, null);
    }

    @Test
    public void testToString() {
        PlayerState one = new PlayerState(PlayerType.LIGHT, 2, 3);
        PlayerState two = new PlayerState(PlayerType.DARK, 3, 2);
        PlayerState three = new PlayerState(PlayerType.LIGHT, 1, 1);
        PlayerState four = new PlayerState(PlayerType.DARK, 9, 9);

        assertEquals("Light: 2 Pieces, 3 Score", one.toString());
        assertEquals("Dark: 3 Pieces, 2 Score", two.toString());
        assertEquals("Light: 1 Piece, 1 Score", three.toString());
        assertEquals("Dark: 9 Pieces, 9 Score", four.toString());

        assertEquals(one.toString(), one.toString());
        assertEquals(two.toString(), two.toString());
        assertEquals(three.toString(), three.toString());
        assertEquals(four.toString(), four.toString());

        assertEquals(one.toString(), new PlayerState(PlayerType.LIGHT, 2, 3).toString());
        assertEquals(two.toString(), new PlayerState(PlayerType.DARK, 3, 2).toString());
        assertEquals(three.toString(), new PlayerState(PlayerType.LIGHT, 1, 1).toString());
        assertEquals(four.toString(), new PlayerState(PlayerType.DARK, 9, 9).toString());

        assertNotEquals(one.toString(), two.toString());
        assertNotEquals(one.toString(), three.toString());
        assertNotEquals(one.toString(), four.toString());

        assertNotEquals(two.toString(), one.toString());
        assertNotEquals(two.toString(), three.toString());
        assertNotEquals(two.toString(), four.toString());

        assertNotEquals(three.toString(), one.toString());
        assertNotEquals(three.toString(), two.toString());
        assertNotEquals(three.toString(), four.toString());

        assertNotEquals(four.toString(), one.toString());
        assertNotEquals(four.toString(), two.toString());
        assertNotEquals(four.toString(), three.toString());
    }
}
