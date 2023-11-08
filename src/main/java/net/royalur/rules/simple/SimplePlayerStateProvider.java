package net.royalur.rules.simple;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.model.PlayerState;
import net.royalur.rules.PlayerStateProvider;

import javax.annotation.Nonnull;

/**
 * Provides new instances of, and manipulations to, simple player states.
 */
public class SimplePlayerStateProvider implements PlayerStateProvider<Piece, PlayerState> {

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
    public @Nonnull PlayerState create(@Nonnull PlayerType player, int pieces, int score) {
        return new PlayerState(player, pieces, score);
    }

    @Override
    public @Nonnull PlayerState createStartingState(@Nonnull PlayerType player) {
        return new PlayerState(player, startingPieceCount, 0);
    }

    @Override
    public @Nonnull PlayerState applyPieceIntroduced(@Nonnull PlayerState playerState, @Nonnull Piece piece) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount() - 1,
                playerState.getScore()
        );
    }

    @Override
    public @Nonnull PlayerState applyPieceCaptured(@Nonnull PlayerState playerState, @Nonnull Piece piece) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount() + 1,
                playerState.getScore()
        );
    }

    @Override
    public @Nonnull PlayerState applyPieceScored(@Nonnull PlayerState playerState, @Nonnull Piece piece) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount(),
                playerState.getScore() + 1
        );
    }
}
