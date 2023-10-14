package net.royalur.rules.simple.fast;

import net.royalur.model.PlayerState;
import net.royalur.model.PlayerType;

import javax.annotation.Nonnull;

public class FastPlayer {

    /**
     * The tile indices of each tile on the path for this player.
     */
    public final @Nonnull int[] path;

    /**
     * The sign of this player for pieces.
     * Light is +1, dark is -1.
     */
    public final int sign;
    public final boolean isLight;

    public int pieces;
    public int score;

    public FastPlayer(@Nonnull int[] path, boolean isLight) {
        this.path = path;
        this.sign = (isLight ? 1 : -1);
        this.isLight = isLight;
        this.pieces = 0;
        this.score = 0;
    }

    public void copyFrom(@Nonnull FastPlayer other) {
        if (sign != other.sign)
            throw new IllegalArgumentException("Different player!");

        this.pieces = other.pieces;
        this.score = other.score;
    }

    public void copyFrom(@Nonnull PlayerState state) {
        PlayerType expectedPlayerType = (isLight ? PlayerType.LIGHT : PlayerType.DARK);
        if (expectedPlayerType != state.getPlayer())
            throw new IllegalArgumentException("Different player!");

        this.pieces = state.getPieceCount();
        this.score = state.getScore();
    }
}
