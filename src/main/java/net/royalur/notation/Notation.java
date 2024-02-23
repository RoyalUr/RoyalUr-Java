package net.royalur.notation;

import net.royalur.Game;

/**
 * A notation can be used as a shared format for distributing
 * games of the Royal Game of Ur. Notations can be used to encode
 * games into text, and to decode games from text. They are not
 * guaranteed to be lossless in their conversion. Therefore, you
 * should check the specific notation you are considering to
 * determine the information that it is able to save.
 */
public interface Notation {

    /**
     * Encodes the given game, {@code game}, into text.
     * @param game The game to be encoded.
     * @return Text that represents {@code game} in this notation.
     */
    String encodeGame(Game game);

    /**
     * Decodes the game from the text {@code encoded}, based upon the rules {@code rules}.
     * @param encoded The text to decode into a game.
     * @return The decoded game.
     */
    Game decodeGame(String encoded);
}
