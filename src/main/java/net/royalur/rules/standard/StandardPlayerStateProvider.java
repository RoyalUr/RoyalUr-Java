package net.royalur.rules.standard;

import net.royalur.model.Player;
import net.royalur.model.PlayerState;
import net.royalur.rules.PlayerStateProvider;

import javax.annotation.Nonnull;

/**
 * TODO
 */
public class StandardPlayerStateProvider implements PlayerStateProvider<PlayerState> {

    /**
     * The number of pieces that each player starts with.
     */
    private final int startingPieceCount;

    /**
     * Creates a new standard player state provider.
     * @param startingPieceCount The number of pieces that each player starts with.
     */
    public StandardPlayerStateProvider(int startingPieceCount) {
        if (startingPieceCount <= 0)
            throw new IllegalArgumentException("startingPieces must be at least 1, not " + startingPieceCount);

        this.startingPieceCount = startingPieceCount;
    }

    @Override
    public @Nonnull PlayerState create(@Nonnull Player player) {
        return new PlayerState(player, startingPieceCount, 0);
    }

    @Override
    public @Nonnull PlayerState applyPiecesChange(PlayerState playerState, int pieces) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount() + pieces,
                playerState.getScore()
        );
    }

    @Override
    public @Nonnull PlayerState applyScoreChange(PlayerState playerState, int pieces) {
        return new PlayerState(
                playerState.getPlayer(),
                playerState.getPieceCount(),
                playerState.getScore() + pieces
        );
    }
}
