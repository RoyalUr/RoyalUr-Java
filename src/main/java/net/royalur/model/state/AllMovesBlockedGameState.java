package net.royalur.model.state;

import net.royalur.model.*;

import javax.annotation.Nonnull;

/**
 * A GameState that represents a roll that resulted in no available moves due to all moves being blocked.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of roll that was made in this game state.
 */
public class AllMovesBlockedGameState<
        P extends Piece, S extends PlayerState, R extends Roll
> extends InfoGameState<P, S, R> {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    public final @Nonnull R roll;

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     * @param roll        The value of the dice that was rolled that can be
     *                    used as the number of places to move a piece.
     */
    public AllMovesBlockedGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull Player turn,
            @Nonnull R roll) {

        super(board, lightPlayer, darkPlayer, turn);

        if (roll.value == 0) {
            throw new IllegalArgumentException(
                    "All moves cannot be blocked when no moves are possible (rolled zero)"
            );
        }
        this.roll = roll;
    }

    @Override
    public @Nonnull String describe() {
        return "The player " + getTurnPlayer().name + " rolled " + roll +", but all moves are blocked.";
    }
}
