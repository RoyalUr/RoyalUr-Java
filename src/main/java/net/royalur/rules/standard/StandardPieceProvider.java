package net.royalur.rules.standard;

import net.royalur.model.Player;
import net.royalur.rules.PieceProvider;

import javax.annotation.Nonnull;

/**
 * Provides new instances of, and manipulations to, standard pieces.
 */
public class StandardPieceProvider implements PieceProvider<StandardPiece> {

    @Override
    public @Nonnull StandardPiece createIntroduced(@Nonnull Player owner, int newPathIndex) {
        return new StandardPiece(owner, newPathIndex);
    }

    @Override
    public @Nonnull StandardPiece createMoved(@Nonnull StandardPiece fromPiece, int newPathIndex) {
        return new StandardPiece(fromPiece.owner, newPathIndex);
    }
}
