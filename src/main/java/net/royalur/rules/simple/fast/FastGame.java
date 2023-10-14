package net.royalur.rules.simple.fast;

import net.royalur.Game;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.PlayerType;
import net.royalur.model.Tile;
import net.royalur.model.dice.Roll;
import net.royalur.rules.simple.SimpleRuleSet;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A simple game that is optimised for speed.
 * This speed comes at the cost of error checking,
 * convenience, and tracking of game history.
 */
public class FastGame {

    public final boolean areRosettesSafe;
    public final boolean rosettesGrantExtraRoll;
    public final boolean capturesGrantExtraRoll;
    public final int startingPieceCount;

    public final @Nonnull FastBoard board;
    public final @Nonnull FastPlayer light;
    public final @Nonnull FastPlayer dark;

    public boolean isLightTurn;
    public int rollValue;
    public boolean isFinished;

    public FastGame(
            @Nonnull SimpleRuleSet<
                    ? extends Piece,
                    ? extends PlayerState,
                    ? extends Roll
            > rules,
            int startingPieceCount
    ) {
        this.areRosettesSafe = rules.areRosettesSafe();
        this.rosettesGrantExtraRoll = rules.doRosettesGrantExtraRolls();
        this.capturesGrantExtraRoll = rules.doCapturesGrantExtraRolls();
        this.startingPieceCount = startingPieceCount;
        this.board = new FastBoard(rules.getBoardShape());

        int[] lightPath = tilesToIndices(board, rules.getPaths().getLight());
        int[] darkPath = tilesToIndices(board, rules.getPaths().getDark());

        this.light = new FastPlayer(lightPath, true);
        this.dark = new FastPlayer(darkPath, false);

        this.isLightTurn = true;
        this.rollValue = -1;
        this.isFinished = false;
    }

    public @Nonnull FastPlayer getPlayer(boolean isLight) {
        return isLight ? light : dark;
    }

    public @Nonnull FastPlayer getTurnPlayer() {
        return getPlayer(isLightTurn);
    }

    public void copyFrom(@Nonnull FastGame other) {
        board.copyFrom(other.board);
        light.copyFrom(other.light);
        dark.copyFrom(other.dark);
        this.isLightTurn = other.isLightTurn;
        this.rollValue = other.rollValue;
        this.isFinished = other.isFinished;
    }

    public void copyFrom(
            @Nonnull Game<
                    ? extends Piece,
                    ? extends PlayerState,
                    ? extends Roll
            > game
    ) {
        board.copyFrom(game.getBoard());
        light.copyFrom(game.getLightPlayer());
        dark.copyFrom(game.getDarkPlayer());
        this.isLightTurn = (game.getTurn() == PlayerType.LIGHT);
        this.rollValue = (game.isWaitingForMove() ? game.getRoll().value() : -1);
        this.isFinished = game.isFinished();
    }

    /**
     * Checks whether a roll of the dice is expected.
     * This may return true when the game has already
     * finished. Therefore, if the game may have finished,
     * you should check that first.
     * @return Whether a roll of the dice is expected.
     */
    public boolean isWaitingForRoll() {
        return rollValue < 0;
    }

    /**
     * Checks whether moving a piece is expected.
     * This may return true when the game has already
     * finished. Therefore, if the game may have finished,
     * you should check that first.
     * @return Whether moving a piece is expected.
     */
    public boolean isWaitingForMove() {
        return rollValue >= 0;
    }

    /**
     * Populates {@code moveList} with all available moves in the current state of the game.
     */
    public void findAvailableMoves(@Nonnull FastMoveList moveList) {
        moveList.clear();

        int rollValue = this.rollValue;
        if (rollValue < 0)
            throw new IllegalStateException("No roll has been made");

        FastPlayer turnPlayer = getTurnPlayer();
        int turnPlayerSign = turnPlayer.sign;
        int[] path = turnPlayer.path;
        int[] boardPieces = board.pieces;

        // Check if a piece can be taken off the board.
        if (rollValue <= path.length) {
            int scorePathIndex = path.length - rollValue;
            int scoreTileIndex = path[scorePathIndex];
            int scorePiece = boardPieces[scoreTileIndex];
            if (scorePiece == turnPlayerSign * (scorePathIndex + 1)) {
                moveList.add(scorePathIndex);
            }
        }

        // Check for pieces on the board that can be moved to another tile on the board.
        boolean areRosettesSafe = this.areRosettesSafe;
        boolean turnPlayerHasPieces = (turnPlayer.pieces > 0);
        for (int pathIndex = -1; pathIndex < path.length - rollValue; ++pathIndex) {

            if (pathIndex >= 0) {
                // Move a piece on the board.
                int tileIndex = path[pathIndex];
                int piece = boardPieces[tileIndex];
                if (piece != turnPlayerSign * (pathIndex + 1))
                    continue;

            } else if (!turnPlayerHasPieces) {
                // Can't introduce a piece to the board.
                continue;
            }

            // Check if the destination is free.
            int destPathIndex = pathIndex + rollValue;
            int destTileIndex = path[destPathIndex];
            int destPiece = boardPieces[destTileIndex];
            if (destPiece != 0) {
                // Can't capture your own pieces.
                if (destPiece * turnPlayerSign > 0)
                    continue;

                // Can't capture pieces on rosettes if they are safe.
                if (areRosettesSafe && board.isTileRosette(destTileIndex))
                    continue;
            }

            // Add the move to the list.
            moveList.add(pathIndex);
        }
    }

    public void applyRoll(int rollValue, @Nonnull FastMoveList moveList) {
        if (this.rollValue >= 0)
            throw new IllegalStateException("A roll has already been made");

        // Swap turn when rolling a zero.
        if (rollValue == 0) {
            isLightTurn = !isLightTurn;
            moveList.clear();
            return;
        }

        // Determine if the player has any available moves.
        this.rollValue = rollValue;
        findAvailableMoves(moveList);
        if (moveList.moveCount == 0) {
            isLightTurn = !isLightTurn;
            this.rollValue = -1;
        }
    }

    public boolean shouldGrantRoll(int destTileIndex, int capturedPiece) {
        if (rosettesGrantExtraRoll && destTileIndex >= 0 && board.isTileRosette(destTileIndex))
            return true;

        return capturesGrantExtraRoll && capturedPiece != 0;
    }

    public void applyMove(int pathIndex) {
        int rollValue = this.rollValue;
        if (rollValue < 0)
            throw new IllegalStateException("No roll has been made");

        // We are using the roll now, so clear it.
        this.rollValue = -1;

        FastPlayer turnPlayer = this.getTurnPlayer();
        int turnPlayerSign = turnPlayer.sign;
        int[] path = turnPlayer.path;
        int[] boardPieces = this.board.pieces;

        if (pathIndex >= 0) {
            // Moving a piece on the board.
            int sourceTileIndex = path[pathIndex];
            boardPieces[sourceTileIndex] = 0;

        } else {
            // Introducing a piece to the board.
            turnPlayer.pieces -= 1;
        }

        int destPathIndex = pathIndex + rollValue;
        int destTileIndex = -1;
        int capturedPiece = 0;

        if (destPathIndex < path.length) {
            // Moving a piece on the board.
            destTileIndex = path[destPathIndex];
            capturedPiece = boardPieces[destTileIndex];
            if (capturedPiece != 0) {
                getPlayer(capturedPiece > 0).pieces += 1;
            }
            boardPieces[destTileIndex] = turnPlayerSign * (destPathIndex + 1);

        } else {
            // Scoring a piece.
            turnPlayer.score += 1;
            if (turnPlayer.score >= startingPieceCount) {
                isFinished = true;
                return;
            }
        }

        // Determine whose turn it should be.
        if (!shouldGrantRoll(destTileIndex, capturedPiece)) {
            isLightTurn = !isLightTurn;
        }
    }

    private static @Nonnull int[] tilesToIndices(
            @Nonnull FastBoard board,
            @Nonnull List<Tile> tiles
    ) {
        int[] indices = new int[tiles.size()];
        for (int index = 0; index < indices.length; ++index) {
            Tile tile = tiles.get(index);
            indices[index] = board.calcTileIndex(tile.getXIndex(), tile.getYIndex());
        }
        return indices;
    }
}
