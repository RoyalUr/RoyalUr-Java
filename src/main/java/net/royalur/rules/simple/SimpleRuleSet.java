package net.royalur.rules.simple;

import net.royalur.model.*;
import net.royalur.rules.Dice;
import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * The most common, simplified, rules of the Royal Game of Ur.
 * Any piece with a valid move can be moved. Rosettes give another
 * turn and are safe squares.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public abstract class SimpleRuleSet<
        P extends SimplePiece,
        S extends PlayerState,
        R extends Roll
> extends RuleSet<P, S, R> {

    /**
     * The number of pieces that each player starts with.
     */
    public final int startingPieceCount;

    /**
     * @param boardShape The shape of the game board.
     * @param paths The paths that the players must take around the board.
     * @param dice The dice that are used to generate dice rolls.
     * @param startingPieceCount The number of pieces that each player starts with.
     */
    public SimpleRuleSet(
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull Dice<R> dice,
            int startingPieceCount
    ) {
        super("Simple", boardShape, paths, dice);

        if (startingPieceCount <= 0)
            throw new IllegalArgumentException("startingPieces must be at least 1, not " + startingPieceCount);

        this.startingPieceCount = startingPieceCount;
    }

    /**
     * Generates a new piece to be introduced to the board.
     * @param owner The owner of the new piece.
     * @param newPathIndex The destination index of the piece in the player's path.
     * @return The new piece that may be introduced to the board.
     */
    public abstract @Nonnull P createNewPiece(@Nonnull Player owner, int newPathIndex);

    /**
     * Generates a piece that has been moved from another tile on the board.
     * @param fromPiece The piece that will be moved.
     * @param newPathIndex The destination index of the piece in the player's path.
     * @return The new piece to be placed on the board.
     */
    public abstract @Nonnull P createMovedPiece(@Nonnull P fromPiece, int newPathIndex);

    @Override
    public @Nonnull List<Move<P>> findAvailableMoves(
            @Nonnull Board<P> board,
            @Nonnull S player,
            @Nonnull R roll
    ) {
        if (roll.value <= 0)
            throw new IllegalArgumentException("The roll's value must be at least 1, not " + roll.value);

        Path path = paths.get(player.player);
        List<Move<P>> moves = new ArrayList<>();

        // Check if a piece can be taken off the board.
        if (roll.value <= path.length) {
            Tile scoreTile = path.get(path.length - roll.value);
            P scorePiece = board.get(scoreTile);
            if (scorePiece != null && scorePiece.owner == player.player) {
                moves.add(new Move<>(scoreTile, scorePiece, null, null, null));
            }
        }

        // Check for pieces on the board that can be moved to another tile on the board.
        for (int index = -1; index < path.length - roll.value; ++index) {

            Tile tile;
            P piece;
            if (index >= 0) {
                // Move a piece on the board.
                tile = path.get(index);
                piece = board.get(tile);
                if (piece == null || piece.owner != player.player || piece.pathIndex != index)
                    continue;

            } else if (player.pieces > 0) {
                // Introduce a piece to the board.
                tile = null;
                piece = null;

            } else {
                continue;
            }

            // Check if the destination is free.
            int destPathIndex = index + roll.value;
            Tile dest = path.get(destPathIndex);
            P destPiece = board.get(dest); // destPiece == null || destPiece.owner != player
            if (destPiece != null && destPiece.owner == player.player)
                continue;

            // Generate the move.
            P movedPiece;
            if (index >= 0) {
                movedPiece = createMovedPiece(piece, destPathIndex);
            } else {
                movedPiece = createNewPiece(player.player, destPathIndex);
            }
            moves.add(new Move<>(tile, piece, dest, movedPiece, destPiece));
        }
        return moves;
    }
}
