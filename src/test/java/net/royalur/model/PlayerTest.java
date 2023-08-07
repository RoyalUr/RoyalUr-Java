package net.royalur.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerTest {

    @Test
    public void testToCharacter() {
        assertEquals('.', PlayerType.toChar(null));
        assertEquals('L', PlayerType.toChar(PlayerType.LIGHT));
        assertEquals('D', PlayerType.toChar(PlayerType.DARK));
    }

    @Test
    public void testGetOtherPlayer() {
        assertEquals(PlayerType.DARK, PlayerType.LIGHT.getOtherPlayer());
        assertEquals(PlayerType.LIGHT, PlayerType.DARK.getOtherPlayer());
    }
}
