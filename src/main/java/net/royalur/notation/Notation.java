package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;

import javax.annotation.Nonnull;

/**
 * A notation can be used as a shared format for distributing
 * games of the Royal Game of Ur. Notations can be used to encode
 * games into text, and to decode games from text. They are not
 * guaranteed to be lossless in their conversion. Therefore, you
 * should check the specific notation you are considering to
 * determine the information that it is able to save.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public abstract class Notation<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The name of this notation.
     */
    public final @Nonnull String name;

    public Notation(@Nonnull String name) {
        this.name = name;
    }

    /**
     * Encodes the given game, {@param game}, into text.
     * @param game The game to be encoded.
     * @return Text that represents {@param game} in this notation.
     */
    public abstract @Nonnull String encode(@Nonnull Game<P, S, R> game);

    /**
     * Decodes the game from the text {@param encoded}.
     * @param encoded The text to decode into a game.
     * @return The decoded game.
     */
    public abstract @Nonnull Game<P, S, R> decode(@Nonnull String encoded);
}
