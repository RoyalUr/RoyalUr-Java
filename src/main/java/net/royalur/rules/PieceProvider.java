package net.royalur.rules;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;

/**
 * An interface that provides instances of pieces. This may be used
 * to store custom information with each piece, for situations such
 * as adding stacking or unique piece behavior.
 */
public interface PieceProvider {

    /**
     * Generate a piece on the board.
     */
    Piece create(PlayerType owner, int pathIndex);

    /**
     * Generates a new piece to be introduced to the board.
     * @param owner The owner of the new piece.
     * @param newPathIndex The destination index of the piece in the player's path.
     * @return The new piece that may be introduced to the board.
     */
    Piece createIntroduced(PlayerType owner, int newPathIndex);

    /**
     * Generates a piece that has been moved from another tile on the board.
     * @param originPiece The piece that will be moved.
     * @param newPathIndex The destination index of the piece in the player's path.
     * @return The new piece to be placed on the board.
     */
    Piece createMoved(Piece originPiece, int newPathIndex);
}
