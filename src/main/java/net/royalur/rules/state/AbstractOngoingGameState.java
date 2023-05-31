package net.royalur.rules.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A game state representing a single point within an ongoing game.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 */
public abstract class AbstractOngoingGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends AbstractGameState<P, S, R> implements OngoingGameState<P, S, R> {

    /**
     * The player who made an action or that should make an action.
     */
    private final @Nonnull Player turn;

    /**
     * Instantiates the baseline state of a game state.
     * @param board The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer The state of the dark player.
     * @param turn The player who made an action or that should make an action.
     */
    public AbstractOngoingGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn
    ) {
        super(board, lightPlayer, darkPlayer);
        this.turn = turn;
    }

    @Override
    public @Nonnull Player getTurn() {
        return turn;
    }
}
