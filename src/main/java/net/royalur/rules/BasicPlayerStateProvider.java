package net.royalur.rules;

import net.royalur.model.Piece;
import net.royalur.model.PlayerType;
import net.royalur.model.PlayerState;

import javax.annotation.Nonnull;

/**
 * Provides new instances of, and manipulations to, standard player states.
 */
public class BasicPlayerStateProvider implements PlayerStateProvider<Piece, PlayerState> {

    /**
     * The number of pieces that each player starts with.
     */
    private final int startingPieceCount;

    /**
     * Creates a new standard player state provider.
     * @param startingPieceCount The number of pieces that each player starts with.
     */
    public BasicPlayerStateProvider(int startingPieceCount) {
        if (startingPieceCount <= 0)
            throw new IllegalArgumentException("startingPieces must be at least 1, not " + startingPieceCount);

        this.startingPieceCount = startingPieceCount;
    }

    @Override
    public int getStartingPieceCount() {
        return startingPieceCount;
    }

    @Override
    public @Nonnull PlayerState create(@Nonnull PlayerType player) {
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
