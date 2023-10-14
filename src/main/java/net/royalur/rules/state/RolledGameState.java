package net.royalur.rules.state;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A game state that represents a roll that was made in a game.
 * @param <P> The type of pieces that are stored on the board in this game state.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of roll that was made in this game state.
 */
public class RolledGameState<
        P extends Piece,
        S extends PlayerState,
        R extends Roll
> extends ActionGameState<P, S, R> {

    /**
     * The roll that represents the number of places the player can move a piece.
     */
    private final @Nonnull R roll;

    /**
     * The moves that are available from this position using the given roll.
     */
    private final @Nonnull List<Move<P>> availableMoves;

    /**
     * Instantiates a game state that represents a roll that was made in a game.
     * @param board       The state of the pieces on the board.
     * @param lightPlayer The state of the light player.
     * @param darkPlayer  The state of the dark player.
     * @param turn        The player who can roll the dice.
     * @param roll        The value of the dice that was rolled that can be
     *                    used as the number of places to move a piece.
     * @param availableMoves The moves that are available from this position
     *                       using the given roll.
     */
    public RolledGameState(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer,
            @Nonnull PlayerType turn,
            @Nonnull R roll,
            @Nonnull List<Move<P>> availableMoves
    ) {
        super(board, lightPlayer, darkPlayer, turn);
        this.roll = roll;
        this.availableMoves = List.copyOf(availableMoves);
    }

    /**
     * Gets the roll that the player made.
     * @return The roll that the player made.
     */
    public @Nonnull R getRoll() {
        return roll;
    }

    /**
     * Gets the moves that are available from this position
     * using the given roll.
     * @return The moves that are available from
     *         this position using the given roll.
     */
    public @Nonnull List<Move<P>> getAvailableMoves() {
        return this.availableMoves;
    }

    @Override
    public @Nonnull String describe() {
        StringBuilder builder = new StringBuilder();
        builder.append("The ")
                .append(getTurn().getTextName().toLowerCase())
                .append(" player rolled ")
                .append(roll.value());

        if (availableMoves.isEmpty()) {
            if (roll.value() == 0) {
                builder.append(", and had no moves");
            } else {
                builder.append(", and all moves were blocked");
            }
        }
        return builder.toString();
    }
}
