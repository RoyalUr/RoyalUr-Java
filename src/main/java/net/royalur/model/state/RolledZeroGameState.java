package net.royalur.model.state;

import net.royalur.model.Board;
import net.royalur.model.Player;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;

import javax.annotation.Nonnull;

/**
 * A GameState that represents a roll that resulted in no available moves due to a roll with a value of zero.
 */
public class RolledZeroGameState extends InfoGameState {

    /**
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     */
    public RolledZeroGameState(
            @Nonnull Board board,
            @Nonnull PlayerState lightPlayer,
            @Nonnull PlayerState darkPlayer,
            @Nonnull Player turn) {

        super(board, lightPlayer, darkPlayer, turn);
    }

    @Override
    public @Nonnull String describe() {
        return "The player " + getTurnPlayer().name + " rolled 0, so no pieces can be moved.";
    }
}
