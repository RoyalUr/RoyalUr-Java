package net.royalur.rules.simple;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.model.PlayerState;
import net.royalur.rules.PlayerStateProvider;

/**
 * Provides new instances of, and manipulations to, simple player states.
 */
public class SimplePlayerStateProvider implements PlayerStateProvider {

    /**
     * The number of pieces that each player starts with.
     */
    private final int startingPieceCount;

    /**
     * Creates a new simple player state provider.
     * @param startingPieceCount The number of pieces that each player starts with.
     */
    public SimplePlayerStateProvider(int startingPieceCount) {
        if (startingPieceCount <= 0)
            throw new IllegalArgumentException("startingPieces must be at least 1, not " + startingPieceCount);

        this.startingPieceCount = startingPieceCount;
    }

    @Override
    public int getStartingPieceCount() {
        return startingPieceCount;
    }

    @Override
    public PlayerState create(PlayerType player, int pieces, int score) {
        return new PlayerState(player, pieces, score);
    }

    @Override
    public PlayerState createStartingState(PlayerType player) {
        return new PlayerState(player, startingPieceCount, 0);
    }

    @Override
    public PlayerState applyPieceIntroduced(PlayerState playerState, Piece piece) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount() - 1,
                playerState.getScore()
        );
    }

    @Override
    public PlayerState applyPieceCaptured(PlayerState playerState, Piece piece) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount() + 1,
                playerState.getScore()
        );
    }

    @Override
    public PlayerState applyPieceScored(PlayerState playerState, Piece piece) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount(),
                playerState.getScore() + 1
        );
    }
}
