package net.royalur.rules.state;

import net.royalur.model.*;

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
     * The type of this game state, representing its purpose.
     */
    private final @Nonnull GameStateType type;

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
     * @param type The type of this game state, representing its purpose.
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     */
    public AbstractGameState(
            @Nonnull GameStateType type,
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer) {

        if (lightPlayer.player != Player.LIGHT)
            throw new IllegalArgumentException("The lightPlayer should be of Player.LIGHT, not " + lightPlayer.player);
        if (darkPlayer.player != Player.DARK)
            throw new IllegalArgumentException("The darkPlayer should be of Player.DARK, not " + lightPlayer.player);

        // Enforce that the GameStateTypes match the types used for the game states.
        if (!type.baseClass.isInstance(this)) {
            throw new IllegalArgumentException(
                    "This state's type is " + type + ", but the state is not a subclass of " + type.baseClass + ". " +
                            "This state is of type " + getClass() + " instead"
            );
        }

        this.type = type;
        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    @Override
    public @Nonnull GameStateType getType() {
        return type;
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
}
