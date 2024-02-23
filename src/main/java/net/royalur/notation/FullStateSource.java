package net.royalur.notation;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Produces game states from scratch.
 */
public class FullStateSource<
        P extends Piece, S extends PlayerState, R extends Roll
> extends StateSource<P, S, R> {

    private final Board<P> board;
    private final S lightPlayer;
    private final S darkPlayer;

    public FullStateSource(
            Board<P> board,
            S lightPlayer,
            S darkPlayer
    ) {
        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    private S getPlayer(PlayerType turn) {
        return switch (turn) {
            case LIGHT -> lightPlayer;
            case DARK -> darkPlayer;
        };
    }

    @Override
    public RolledGameState<P, S, R> createRolledState(
            RuleSet<P, S, R> rules,
            PlayerType turn,
            R roll
    ) {
        List<Move<P>> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new RolledGameState<>(
                board, lightPlayer, darkPlayer,
                turn, roll, availableMoves
        );
    }

    @Override
    public MovedGameState<P, S, R> createMovedState(
            RuleSet<P, S, R> rules,
            PlayerType turn,
            R roll,
            Move<P> move
    ) {
        return new MovedGameState<>(
                board, lightPlayer, darkPlayer,
                turn, roll, move
        );
    }

    @Override
    public WaitingForRollGameState<P, S, R> createWaitingForRollState(
            RuleSet<P, S, R> rules,
            PlayerType turn
    ) {
        return new WaitingForRollGameState<>(
                board, lightPlayer, darkPlayer, turn
        );
    }

    @Override
    public WaitingForMoveGameState<P, S, R> createWaitingForMoveState(
            RuleSet<P, S, R> rules,
            PlayerType turn,
            R roll
    ) {
        List<Move<P>> availableMoves = rules.findAvailableMoves(
                board, getPlayer(turn), roll
        );
        return new WaitingForMoveGameState<>(
                board, lightPlayer, darkPlayer,
                turn, roll, availableMoves
        );
    }

    @Override
    public WinGameState<P, S, R> createWinState(
            RuleSet<P, S, R> rules,
            PlayerType winner
    ) {
        return new WinGameState<>(
                board, lightPlayer, darkPlayer, winner
        );
    }
}
