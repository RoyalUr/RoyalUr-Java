package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.rules.simple.fast.FastSimpleGame;

import javax.annotation.Nullable;

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
     * Encode the given game state to a binary key that is always encoded as
     * the light player. States where it is the dark player's turn will be
     * reversed before being encoded. If a temp game is not provided, and it is
     * the dark player's turn, then a temp game will be constructed.
     * @param game The game to encode.
     * @param tempGame An optional temporary game to use to reverse the players
     *                 in game.
     * @return The game state encoded as a symmetrical binary key.
     */
    default long encodeSymmetricalGameState(
            FastSimpleGame game,
            @Nullable FastSimpleGame tempGame
    ) {
        FastSimpleGame keyGame = game;
        if (!game.isLightTurn) {
            keyGame = game.reversePlayers(tempGame);
        }
        return encodeGameState(keyGame);
    }

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

    /**
     * Creates an encoding for a simple set of rules.
     */
    static GameStateEncoding createSimple(GameSettings settings) {
        if (GameSettings.FINKEL.equals(settings))
            return new FinkelGameStateEncoding();

        return new SimpleGameStateEncoding(settings);
    }
}
