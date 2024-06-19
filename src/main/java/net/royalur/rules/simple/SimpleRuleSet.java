package net.royalur.rules.simple;

import net.royalur.model.*;
import net.royalur.model.dice.DiceFactory;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.PathPair;
import net.royalur.model.shape.BoardShape;
import net.royalur.rules.PieceProvider;
import net.royalur.rules.PlayerStateProvider;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.state.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The most common, simple, rules of the Royal Game of Ur.
 * This still allows a large range of custom rules.
 */
public class SimpleRuleSet extends RuleSet {

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
    public SimpleRuleSet(
            BoardShape boardShape,
            PathPair paths,
            DiceFactory diceFactory,
            PieceProvider pieceProvider,
            PlayerStateProvider playerStateProvider,
            boolean safeRosettes,
            boolean rosettesGrantExtraRolls,
            boolean capturesGrantExtraRolls
    ) {
        super(boardShape, paths, diceFactory, pieceProvider, playerStateProvider);
        this.safeRosettes = safeRosettes;
        this.rosettesGrantExtraRolls = rosettesGrantExtraRolls;
        this.capturesGrantExtraRolls = capturesGrantExtraRolls;
    }

    public FastSimpleGame createCompatibleFastGame() {
        int startingPieceCount = playerStateProvider.getStartingPieceCount();
        return new FastSimpleGame(this.getSettings());
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
    public GameState generateInitialGameState() {
        return new WaitingForRollGameState(
                new Board(boardShape),
                playerStateProvider.createStartingState(PlayerType.LIGHT),
                playerStateProvider.createStartingState(PlayerType.DARK),
                0,
                PlayerType.LIGHT
        );
    }

    @Override
    public List<Move> findAvailableMoves(
            Board board,
            PlayerState player,
            Roll roll
    ) {
        if (roll.value() <= 0)
            return Collections.emptyList();

        PlayerType playerType = player.getPlayer();
        List<Tile> path = paths.get(playerType);
        List<Move> moves = new ArrayList<>();

        // Check if a piece can be taken off the board.
        if (roll.value() <= path.size()) {
            int scorePathIndex = path.size() - roll.value();
            Tile scoreTile = path.get(scorePathIndex);
            Piece scorePiece = board.get(scoreTile);
            if (scorePiece != null
                    && scorePiece.getOwner() == playerType
                    && scorePiece.getPathIndex() == scorePathIndex
            ) {
                moves.add(new Move(playerType, scoreTile, scorePiece, null, null, null));
            }
        }

        // Check for pieces on the board that can be moved to another tile on the board.
        for (int pathIndex = -1; pathIndex < path.size() - roll.value(); ++pathIndex) {

            Tile tile;
            Piece piece;
            if (pathIndex >= 0) {
                // Move a piece on the board.
                tile = path.get(pathIndex);
                piece = board.get(tile);
                if (piece == null || piece.getOwner() != playerType || piece.getPathIndex() != pathIndex)
                    continue;

            } else if (player.getPieceCount() > 0) {
                // Introduce a piece to the board.
                tile = null;
                piece = null;

            } else {
                continue;
            }

            // Check if the destination is free.
            int destPathIndex = pathIndex + roll.value();
            Tile dest = path.get(destPathIndex);
            Piece destPiece = board.get(dest);
            if (destPiece != null)  {
                // Can't capture your own pieces.
                if (destPiece.getOwner() == playerType)
                    continue;

                // Can't capture pieces on rosettes if they are safe
                if (safeRosettes && boardShape.isRosette(dest))
                    continue;
            }

            // Generate the move.
            Piece movedPiece;
            if (pathIndex >= 0) {
                movedPiece = pieceProvider.createMoved(piece, destPathIndex);
            } else {
                movedPiece = pieceProvider.createIntroduced(playerType, destPathIndex);
            }
            moves.add(new Move(playerType, tile, piece, dest, movedPiece, destPiece));
        }
        return moves;
    }

    @Override
    public List<GameState> applyRoll(
            WaitingForRollGameState state,
            long timeSinceGameStartMs,
            Roll roll
    ) {
        // Construct the state representing the roll that was made.
        List<Move> availableMoves = findAvailableMoves(
                state.getBoard(), state.getTurnPlayer(), roll
        );
        RolledGameState rolledState = new RolledGameState(
                state.getBoard(),
                state.getLightPlayer(),
                state.getDarkPlayer(),
                timeSinceGameStartMs,
                state.getTurn(),
                roll,
                availableMoves
        );

        // Swap turn when the player has no available moves.
        if (availableMoves.isEmpty()) {
            PlayerType newTurn = state.getTurn().getOtherPlayer();
            return List.of(rolledState, new WaitingForRollGameState(
                    state.getBoard(),
                    state.getLightPlayer(),
                    state.getDarkPlayer(),
                    timeSinceGameStartMs,
                    newTurn
            ));
        }

        // The player has moves they can make.
        return List.of(rolledState, new WaitingForMoveGameState(
                state.getBoard(),
                state.getLightPlayer(),
                state.getDarkPlayer(),
                timeSinceGameStartMs,
                state.getTurn(),
                roll,
                availableMoves
        ));
    }

    /**
     * Determines whether the move represented by {@code movedState}
     * should grant another roll to the player that made the move.
     * @param movedState The state representing a move.
     * @return Whether the player that made the move should be granted
     *         another roll.
     */
    public boolean shouldGrantExtraRoll(MovedGameState movedState) {
        Move move = movedState.getMove();
        if (rosettesGrantExtraRolls && move.isDestRosette(boardShape))
            return true;

        return capturesGrantExtraRolls && move.isCapture();
    }

    @Override
    public List<GameState> applyMove(
            WaitingForMoveGameState state,
            long timeSinceGameStartMs,
            Move move
    ) {
        // Generate the state representing the move that was made.
        MovedGameState movedState = new MovedGameState(
                state.getBoard(), state.getLightPlayer(), state.getDarkPlayer(),
                timeSinceGameStartMs, state.getTurn(), state.getRoll(), move
        );

        // Apply the move to the board.
        Board board = state.getBoard().copy();
        move.apply(board);

        // Apply the move to the player that made the move.
        PlayerState turnPlayer = state.getTurnPlayer();
        if (move.isIntroduction()) {
            Piece introducedPiece = move.getDestPiece();
            turnPlayer = playerStateProvider.applyPieceIntroduced(turnPlayer, introducedPiece);
        }
        if (move.isScore()) {
            Piece scoredPiece = move.getSourcePiece();
            turnPlayer = playerStateProvider.applyPieceScored(turnPlayer, scoredPiece);
        }

        // Apply the effects of the move to the other player.
        PlayerState otherPlayer = state.getWaitingPlayer();
        if (move.isCapture()) {
            Piece capturedPiece = move.getCapturedPiece();
            otherPlayer = playerStateProvider.applyPieceCaptured(otherPlayer, capturedPiece);
        }

        // Determine which player is which.
        PlayerType turn = turnPlayer.getPlayer();
        PlayerState lightPlayer = (turn == PlayerType.LIGHT ? turnPlayer : otherPlayer);
        PlayerState darkPlayer = (turn == PlayerType.DARK ? turnPlayer : otherPlayer);

        // Check if the player has won the game.
        int turnPlayerPieces = turnPlayer.getPieceCount();
        if (move.isScore() && turnPlayerPieces + board.countPieces(turn) <= 0)  {
            return List.of(movedState, new EndGameState(
                    board, lightPlayer, darkPlayer, timeSinceGameStartMs, turn
            ));
        }

        // Determine whose turn it will be in the next state.
        PlayerType nextTurn = (shouldGrantExtraRoll(movedState) ? turn : turn.getOtherPlayer());
        return List.of(movedState, new WaitingForRollGameState(
                board, lightPlayer, darkPlayer, timeSinceGameStartMs, nextTurn
        ));
    }

    @Override
    public List<GameState> selectLandmarkStates(List<GameState> states) {
        List<GameState> landmarkStates = new ArrayList<>();
        boolean seenAction = false;
        for (int index = states.size() - 1; index >= 0; --index) {
            GameState state = states.get(index);

            // We always want to include the last action that was made in the game.
            if (state instanceof ActionGameState && !seenAction) {
                landmarkStates.add(state);
                seenAction = true;
                continue;
            }

            // Moves are always important.
            if (state instanceof MovedGameState) {
                landmarkStates.add(state);
                continue;
            }

            // Include rolls that do not have a move state that describes them.
            if (state instanceof RolledGameState rolledState
                    && rolledState.getAvailableMoves().isEmpty()) {

                landmarkStates.add(state);
                continue;
            }

            // Always include control states.
            if (state instanceof ControlGameState controlState) {
                landmarkStates.add(controlState);
                continue;
            }

            // Always include the initial state and the current state.
            if (index == 0 || index == states.size() - 1) {
                landmarkStates.add(state);
            }
        }

        // We loop in reverse-order, so correct that.
        Collections.reverse(landmarkStates);

        if (!(landmarkStates.get(0) instanceof PlayableGameState))
            throw new RuntimeException("Sanity check failed: The first landmark state should be playable");

        return landmarkStates;
    }
}
