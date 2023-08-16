package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;

/**
 * A game state represents a single point within a game.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public abstract class AbstractGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> implements GameState<P, S, R> {

    /**
     * The state of the pieces on the board.
     */
    private final @Nonnull Board<P> board;

    /**
     * The state of the light player.
     */
    private final @Nonnull S lightPlayer;

    /**
     * The state of the dark player.
     */
    private final @Nonnull S darkPlayer;

    /**
     * Instantiates the baseline state of a game state.
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     */
    public AbstractGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer
    ) {
        if (lightPlayer.getPlayer() != PlayerType.LIGHT)
            throw new IllegalArgumentException("lightPlayer should be a Player.LIGHT, not " + lightPlayer.getPlayer());
        if (darkPlayer.getPlayer() != PlayerType.DARK)
            throw new IllegalArgumentException("darkPlayer should be a Player.DARK, not " + darkPlayer.getPlayer());

        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    @Override
    public @Nonnull Board<P> getBoard() {
        return board;
    }

    @Override
    public @Nonnull S getLightPlayer() {
        return lightPlayer;
    }

    @Override
    public @Nonnull S getDarkPlayer() {
        return darkPlayer;
    }

    @Override
    public boolean isPlayable() {
        return this instanceof PlayableGameState;
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
