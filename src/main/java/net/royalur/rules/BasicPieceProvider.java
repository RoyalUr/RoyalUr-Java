package net.royalur.rules;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.rules.PieceProvider;

import javax.annotation.Nonnull;

/**
 * Provides new instances of, and manipulations to, standard pieces.
 */
public class BasicPieceProvider implements PieceProvider<Piece> {

    @Override
    public @Nonnull Piece createIntroduced(@Nonnull PlayerType owner, int newPathIndex) {
        return new Piece(owner, newPathIndex);
    }

    @Override
    public @Nonnull Piece createMoved(@Nonnull Piece fromPiece, int newPathIndex) {
        return new Piece(fromPiece.getOwner(), newPathIndex);
    }
}