package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.name.Name;

import javax.annotation.Nonnull;

/**
 * A game state where we are waiting for interactions from a player.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be stored in this game state.
 * @param <A> The type of name given to this action.
 */
public abstract class PlayableGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll,
        A extends Name
> extends OngoingGameState<P, S, R> {

    /**
     * The type of action that is expected in this state.
     */
    private final @Nonnull A expectedActionType;

    /**
     * Instantiates a game state for an ongoing point in a game.
     * @param board               The state of the pieces on the board.
     * @param lightPlayer         The state of the light player.
     * @param darkPlayer          The state of the dark player.
     * @param turn                The player who made an action or that should make an action.
     * @param expectedActionType  The type of action that is expected in this state.
     */
    public PlayableGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull PlayerType turn,
            @Nonnull A expectedActionType
    ) {
        super(board, lightPlayer, darkPlayer, turn);
        this.expectedActionType = expectedActionType;
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    /**
     * Gets the type of action that is expected from the current turn player.
     * @return The type of action that is expected from the current player.
     */
    public @Nonnull A getExpectedActionType() {
        return expectedActionType;
    }
}
