package net.royalur.lut;

import net.royalur.rules.simple.fast.FastSimpleGame;

/**
 * Encodes the state of a game to a small binary key.
 */
public interface GameStateEncoding {

    /**
     * Encode the given game state to a binary key.
     * @param game The game to encode.
     * @return The game state encoded as a binary key.
     */
    long encodeGameState(FastSimpleGame game);

    /**
     * Gets the upper 32 bits of the given key.
     */
    static int calcUpperKey(long key) {
        return (int) ((key & 0xFFFFFFFF00000000L) >> 32);
    }

    /**
     * Gets the lower 32 bits of the given key.
     */
    static int calcLowerKey(long key) {
        return (int) key;
    }
}
