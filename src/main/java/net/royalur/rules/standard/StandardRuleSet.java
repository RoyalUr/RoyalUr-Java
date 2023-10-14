package net.royalur.rules.standard;

import net.royalur.model.*;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.rules.PieceProvider;
import net.royalur.rules.PlayerStateProvider;
import net.royalur.rules.RuleSet;
import net.royalur.rules.standard.fast.FastGame;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The most common, simplified, rules of the Royal Game of Ur.
 * Any piece with a valid move can be moved. Rosettes give another
 * turn and are safe squares.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public class StandardRuleSet<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends RuleSet<P, S, R> {

    /**
     * Whether rosette tiles are safe squares for pieces.
     */
    private final boolean safeRosettes;

    /**
     * Whether landing on rosette tiles grants an additional roll.
     */
    private final boolean rosettesGrantExtraRolls;

    /**
     * Whether capturing a piece grants an additional roll.
     */
    private final boolean capturesGrantExtraRolls;

    /**
     * Instantiates a simple rule set for the Royal Game of Ur.
     * @param boardShape The shape of the game board.
     * @param paths The paths that the players must take around the board.
     * @param diceFactory The generator of dice that are used to generate dice rolls.
     * @param pieceProvider Provides the manipulation of piece values.
     * @param playerStateProvider Provides the manipulation of player states.
     * @param safeRosettes Whether rosette tiles are safe squares for pieces.
     * @param rosettesGrantExtraRolls Whether landing on rosette tiles gives an extra roll.
     * @param capturesGrantExtraRolls Whether capturing a piece gives an extra roll.
     */
    public StandardRuleSet(
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull DiceFactory<R> diceFactory,
            @Nonnull PieceProvider<P> pieceProvider,
            @Nonnull PlayerStateProvider<P, S> playerStateProvider,
            boolean safeRosettes,
            boolean rosettesGrantExtraRolls,
            boolean capturesGrantExtraRolls
    ) {
        super(boardShape, paths, diceFactory, pieceProvider, playerStateProvider);
        this.safeRosettes = safeRosettes;
        this.rosettesGrantExtraRolls = rosettesGrantExtraRolls;
        this.capturesGrantExtraRolls = capturesGrantExtraRolls;
    }

    public @Nonnull FastGame createCompatibleFastGame() {
        int startingPieceCount = playerStateProvider.getStartingPieceCount();
        return new FastGame(this, startingPieceCount);
    }

    @Override
    public boolean areRosettesSafe() {
        return safeRosettes;
    }

    @Override
    public boolean doRosettesGrantExtraRolls() {
        return rosettesGrantExtraRolls;
    }

    @Override
    public boolean doCapturesGrantExtraRolls() {
        return capturesGrantExtraRolls;
    }

    @Override
    public @Nonnull GameState<P, S, R> generateInitialGameState() {
        return new WaitingForRollGameState<>(
                new Board<>(boardShape),
                playerStateProvider.create(PlayerType.LIGHT),
                playerStateProvider.create(PlayerType.DARK),
                PlayerType.LIGHT
        );
    }

    @Override
    public @Nonnull List<Move<P>> findAvailableMoves(
            @Nonnull Board<P> board,
            @Nonnull S player,
            @Nonnull R roll
    ) {
        if (roll.value() <= 0)
            return Collections.emptyList();

        PlayerType playerType = player.getPlayer();
        List<Tile> path = paths.get(playerType);
        List<Move<P>> moves = new ArrayList<>();

        // Check if a piece can be taken off the board.
        if (roll.value() <= path.size()) {
            int scorePathIndex = path.size() - roll.value();
            Tile scoreTile = path.get(scorePathIndex);
            P scorePiece = board.get(scoreTile);
            if (scorePiece != null &&
                    scorePiece.getOwner() == playerType &&
                    scorePiece.getPathIndex() == scorePathIndex
            ) {
                moves.add(new Move<>(playerType, scoreTile, scorePiece, null, null, null));
            }
        }

        // Check for pieces on the board that can be moved to another tile on the board.
        for (int index = -1; index < path.size() - roll.value(); ++index) {

            Tile tile;
            P piece;
            if (index >= 0) {
                // Move a piece on the board.
                tile = path.get(index);
                piece = board.get(tile);
                if (piece == null)
                    continue;
                if (piece.getOwner() != playerType || piece.getPathIndex() != index)
                    continue;

            } else if (player.getPieceCount() > 0) {
                // Introduce a piece to the board.
                tile = null;
                piece = null;

            } else {
                continue;
            }

            // Check if the destination is free.
            int destPathIndex = index + roll.value();
            Tile dest = path.get(destPathIndex);
            P destPiece = board.get(dest);
            if (destPiece != null)  {
                // Can't capture your own pieces.
                if (destPiece.getOwner() == playerType)
                    continue;

                // Can't capture pieces on rosettes if they are safe.
                if (safeRosettes && board.getShape().isRosette(dest))
                    continue;
            }

            // Generate the move.
            P movedPiece;
            if (index >= 0) {
                movedPiece = pieceProvider.createMoved(piece, destPathIndex);
            } else {
                movedPiece = pieceProvider.createIntroduced(playerType, destPathIndex);
            }
            moves.add(new Move<>(playerType, tile, piece, dest, movedPiece, destPiece));
        }
        return moves;
    }

    @Override
    public @Nonnull List<GameState<P, S, R>> applyRoll(
            @Nonnull WaitingForRollGameState<P, S, R> state,
            @Nonnull R roll
    ) {

        // Construct the state representing the roll that was made.
        List<Move<P>> availableMoves = findAvailableMoves(
                state.getBoard(), state.getTurnPlayer(), roll
        );
        RolledGameState<P, S, R> rolledState = new RolledGameState<>(
                state.getBoard(),
                state.getLightPlayer(),
                state.getDarkPlayer(),
                state.getTurn(),
                roll,
                availableMoves
        );

        // Swap turn when rolling a zero.
        if (roll.value() == 0) {
            PlayerType newTurn = state.getTurn().getOtherPlayer();
            return List.of(rolledState, new WaitingForRollGameState<>(
                    state.getBoard(),
                    state.getLightPlayer(),
                    state.getDarkPlayer(),
                    newTurn
            ));
        }

        // Determine if the player has no available moves.
        if (availableMoves.isEmpty()) {
            PlayerType newTurn = state.getTurn().getOtherPlayer();
            return List.of(rolledState, new WaitingForRollGameState<>(
                    state.getBoard(),
                    state.getLightPlayer(),
                    state.getDarkPlayer(),
                    newTurn
            ));
        }

        // The player has moves they can make.
        return List.of(rolledState, new WaitingForMoveGameState<>(
                state.getBoard(),
                state.getLightPlayer(),
                state.getDarkPlayer(),
                state.getTurn(),
                roll,
                availableMoves
        ));
    }

    /**
     * Determines whether the move represent by {@code movedState}
     * should grant another roll to the player that made the move.
     * @param movedState The state representing a move.
     * @return Whether the player that made the move should be granted
     *         another roll.
     */
    public boolean shouldGrantRoll(@Nonnull MovedGameState<P, S, R> movedState) {
        Move<P> move = movedState.getMove();

        if (rosettesGrantExtraRolls) {
            BoardShape boardShape = movedState.getBoard().getShape();
            if (move.isDestRosette(boardShape))
                return true;
        }
        return capturesGrantExtraRolls && move.isCapture();
    }

    @Override
    public @Nonnull List<GameState<P, S, R>> applyMove(
            @Nonnull WaitingForMoveGameState<P, S, R> state,
            @Nonnull Move<P> move
    ) {

        // Generate the state representing the move that was made.
        MovedGameState<P, S, R> movedState = new MovedGameState<>(
                state.getBoard(), state.getLightPlayer(), state.getDarkPlayer(),
                state.getTurn(), state.getRoll(), move
        );

        // Apply the move to the board.
        Board<P> board = state.getBoard().copy();
        move.apply(board);

        // Apply the move to the player that made the move.
        S turnPlayer = state.getTurnPlayer();
        if (move.isIntroducingPiece()) {
            P introducedPiece = move.getDestPiece();
            turnPlayer = playerStateProvider.applyPieceIntroduced(turnPlayer, introducedPiece);
        }
        if (move.isScoringPiece()) {
            P scoredPiece = move.getSourcePiece();
            turnPlayer = playerStateProvider.applyPieceScored(turnPlayer, scoredPiece);
        }

        // Apply the effects of the move to the other player.
        S otherPlayer = state.getWaitingPlayer();
        if (move.isCapture()) {
            P capturedPiece = move.getCapturedPiece();
            otherPlayer = playerStateProvider.applyPieceCaptured(otherPlayer, capturedPiece);
        }

        // Determine which player is which.
        PlayerType turn = turnPlayer.getPlayer();
        S lightPlayer = (turn == PlayerType.LIGHT ? turnPlayer : otherPlayer);
        S darkPlayer = (turn == PlayerType.DARK ? turnPlayer : otherPlayer);

        // Check if the player has won the game.
        int turnPlayerPieces = turnPlayer.getPieceCount();
        if (move.isScoringPiece() && turnPlayerPieces + board.countPieces(turn) <= 0)  {
            return List.of(movedState, new WinGameState<>(
                    board, lightPlayer, darkPlayer, turn
            ));
        }

        // Determine whose turn it will be in the next state.
        PlayerType nextTurn = (shouldGrantRoll(movedState) ? turn : turn.getOtherPlayer());
        return List.of(movedState, new WaitingForRollGameState<>(
                board, lightPlayer, darkPlayer, nextTurn
        ));
    }
}
