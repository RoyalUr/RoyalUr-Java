package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;

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
     * @param <P> The type of pieces that are stored on the board.
     * @param <S> The type of state that is stored for each player.
     * @param <R> The type of rolls that may be made.
     * @return Text that represents {@code game} in this notation.
     */
    <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull String
    encodeGame(@Nonnull Game<P, S, R> game);

    /**
     * Decodes the game from the text {@code encoded}, based upon the rules {@code rules}.
     * @param rules The rules used to simulate the game as it is decoded.
     * @param encoded The text to decode into a game.
     * @return The decoded game.
     * @param <P> The type of pieces that are stored on the board.
     * @param <S> The type of state that is stored for each player.
     * @param <R> The type of rolls that may be made.
     */
    <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull Game<P, S, R>
    decodeGame(@Nonnull RuleSet<P, S, R> rules, @Nonnull String encoded);
}
