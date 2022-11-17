package net.royalur.model.state;

import net.royalur.model.Board;
import net.royalur.model.Player;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;

import javax.annotation.Nonnull;

/**
 * A GameState that represents a roll that resulted in no available moves due to all moves being blocked.
 */
public class AllMovesBlockedGameState extends InfoGameState {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    public final @Nonnull Roll roll;

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     * @param roll        The value of the dice that was rolled that can be
     *                    used as the number of places to move a piece.
     */
    public AllMovesBlockedGameState(
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player turn,
            @Nonnull Roll roll) {

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
