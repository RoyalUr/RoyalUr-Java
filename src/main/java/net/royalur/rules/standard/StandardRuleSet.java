package net.royalur.rules.standard;

import net.royalur.model.*;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.rules.Dice;
import net.royalur.rules.PieceProvider;
import net.royalur.rules.PlayerStateProvider;
import net.royalur.rules.RuleSet;
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
        P extends StandardPiece,
        S extends PlayerState,
        R extends Roll
> implements RuleSet<P, S, R> {

    /**
     * The shape of the game board.
     */
    private final @Nonnull BoardShape boardShape;

    /**
     * The paths that each player must take around the board.
     */
    private final @Nonnull PathPair paths;

    /**
     * The dice that are used to generate dice rolls.
     */
    private final @Nonnull Dice<R> dice;

    /**
     * Whether rosette tiles are safe squares for pieces.
     */
    private final boolean safeRosettes;

    /**
     * Provides the manipulation of piece values.
     */
    private final @Nonnull PieceProvider<P> pieceProvider;

    /**
     * Provides the manipulation of player state values.
     */
    private final @Nonnull PlayerStateProvider<S> playerStateProvider;

    /**
     * Instantiates a simple rule set for the Royal Game of Ur.
     * @param boardShape The shape of the game board.
     * @param paths The paths that the players must take around the board.
     * @param dice The dice that are used to generate dice rolls.
     * @param safeRosettes Whether rosette tiles are safe squares for pieces.
     * @param pieceProvider Provides the manipulation of piece values.
     * @param playerStateProvider Provides the manipulation of player states.
     */
    public StandardRuleSet(
            @Nonnull BoardShape boardShape,
            @Nonnull PathPair paths,
            @Nonnull Dice<R> dice,
            boolean safeRosettes,
            @Nonnull PieceProvider<P> pieceProvider,
            @Nonnull PlayerStateProvider<S> playerStateProvider
    ) {
        if (!boardShape.isCompatible(paths)) {
            throw new IllegalArgumentException(
                    "The " + paths.getName().getTextName() + " paths are not compatible with the " +
                            boardShape.getName().getTextName() + " board shape"
            );
        }

        this.boardShape = boardShape;
        this.paths = paths;
        this.dice = dice;
        this.safeRosettes = safeRosettes;
        this.pieceProvider = pieceProvider;
        this.playerStateProvider = playerStateProvider;
    }

    @Override
    public @Nonnull BoardShape getBoardShape() {
        return boardShape;
    }

    @Override
    public @Nonnull PathPair getPaths() {
        return paths;
    }

    @Override
    public @Nonnull Dice<R> getDice() {
        return dice;
    }

    @Override
    public boolean areRosettesSafe() {
        return safeRosettes;
    }

    @Override
    public @Nonnull PieceProvider<P> getPieceProvider() {
        return pieceProvider;
    }

    @Override
    public @Nonnull PlayerStateProvider<S> getPlayerStateProvider() {
        return playerStateProvider;
    }

    /**
     * Generates the initial state for a game.
     * @return The initial state for a game.
     */
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
        if (roll.getValue() <= 0)
            return Collections.emptyList();

        PlayerType playerType = player.getPlayer();
        List<Tile> path = paths.get(playerType);
        List<Move<P>> moves = new ArrayList<>();

        // Check if a piece can be taken off the board.
        if (roll.getValue() <= path.size()) {
            int scorePathIndex = path.size() - roll.getValue();
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
        for (int index = -1; index < path.size() - roll.getValue(); ++index) {

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
            int destPathIndex = index + roll.getValue();
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

        // If the player rolled zero, we need to change the turn to the other player.
        if (roll.getValue() == 0) {
            PlayerType newTurn = state.getTurn().getOtherPlayer();
            return List.of(rolledState, new WaitingForRollGameState<>(
                    state.getBoard(),
                    state.getLightPlayer(),
                    state.getDarkPlayer(),
                    newTurn
            ));
        }

        // Determine if the player has any available moves.
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
                roll
        ));
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
        if (move.isIntroducingPiece() || move.isScoringPiece()) {
            if (move.isIntroducingPiece()) {
                turnPlayer = playerStateProvider.applyPiecesChange(turnPlayer, -1);
            }
            if (move.isScoringPiece()) {
                turnPlayer = playerStateProvider.applyScoreChange(turnPlayer, 1);
            }
        }

        // Apply the effects of the move to the other player.
        S otherPlayer = state.getWaitingPlayer();
        if (move.capturesPiece()) {
            otherPlayer = playerStateProvider.applyPiecesChange(otherPlayer, 1);
        }

        // Determine which player is which.
        S lightPlayer = (turnPlayer.getPlayer() == PlayerType.LIGHT ? turnPlayer : otherPlayer);
        S darkPlayer = (turnPlayer.getPlayer() == PlayerType.DARK ? turnPlayer : otherPlayer);

        // Check if the player has won the game.
        if (move.isScoringPiece() && turnPlayer.getPieceCount() <= 0 && board.countPieces(turnPlayer.getPlayer()) <= 0)
            return List.of(movedState, new WinGameState<>(board, lightPlayer, darkPlayer, state.getTurn()));

        // Determine who's turn it will be in the next state.
        PlayerType turn = state.getTurn();
        if (!move.isLandingOnRosette(board.getShape())) {
            turn = turn.getOtherPlayer();
        }
        return List.of(movedState, new WaitingForRollGameState<>(board, lightPlayer, darkPlayer, turn));
    }
}
