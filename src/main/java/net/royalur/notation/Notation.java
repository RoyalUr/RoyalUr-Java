package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
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
public abstract class Notation {

    /**
     * Instantiates a notation.
     */
    public Notation() {}

    /**
     * Gets an identifier that can be used to uniquely identify these paths.
     * @return An identifier that can be used to uniquely identify these paths.
     */
    public @Nonnull String getIdentifier() {
        throw new UnsupportedOperationException("This notation does not have an identifier (" + getClass() + ")");
    }

    /**
     * Encodes the given game, {@param game}, into text.
     * @param game The game to be encoded.
     * @param <P> The type of pieces that are stored on the board.
     * @param <S> The type of state that is stored for each player.
     * @param <R> The type of rolls that may be made.
     * @return Text that represents {@param game} in this notation.
     */
    public abstract <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull String
    encodeGame(@Nonnull Game<P, S, R> game);

    /**
     * Decodes the game from the text {@param encoded}, based upon the rules {@param rules}.
     * @param rules The rules used to simulate the game as it is decoded.
     * @param encoded The text to decode into a game.
     * @return The decoded game.
     * @param <P> The type of pieces that are stored on the board.
     * @param <S> The type of state that is stored for each player.
     * @param <R> The type of rolls that may be made.
     */
    public abstract <P extends Piece, S extends PlayerState, R extends Roll> @Nonnull Game<P, S, R>
    decodeGame(@Nonnull RuleSet<P, S, R> rules, @Nonnull String encoded);
}
