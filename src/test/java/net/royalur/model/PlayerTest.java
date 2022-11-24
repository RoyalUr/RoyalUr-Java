package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    public void testToCharacter() {
        assertEquals('.', Player.toChar(null));
        assertEquals('L', Player.toChar(Player.LIGHT));
        assertEquals('D', Player.toChar(Player.DARK));
    }

    @Test
    public void testGetOtherPlayer() {
        assertEquals(Player.DARK, Player.LIGHT.getOtherPlayer());
        assertEquals(Player.LIGHT, Player.DARK.getOtherPlayer());
    }
}
