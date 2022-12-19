package net.royalur.notation;

import net.royalur.Game;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;

/**
 * RGN stands for Royal Game Notation. This is a textual format
 * that is intended to be readable by both humans and machines.
 * This notation is based upon Chess' PGN (Portable Game Notation).
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public class RGNNotation<P extends Piece, S extends PlayerState, R extends Roll> extends Notation<P, S, R> {

    /**
     * The rules to use to generate the game. This is important,
     * as some implicit information may only be available by
     * playing through all the recorded moves. For example,
     * the index of a piece on its path.
     */
    public final @Nonnull RuleSet<P, S, R> rules;

    public RGNNotation(@Nonnull RuleSet<P, S, R> rules) {
        super("RGN");
        this.rules = rules;
    }

    @Override
    public @Nonnull String encode(@Nonnull Game<P, S, R> game) {
        throw new UnsupportedOperationException("TODO");
    }

    @Override
    public @Nonnull Game<P, S, R> decode(@Nonnull String encoded) {
        throw new UnsupportedOperationException("TODO");
    }
}
