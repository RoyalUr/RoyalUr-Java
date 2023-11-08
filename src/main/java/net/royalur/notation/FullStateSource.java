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

    private final @Nonnull Board<P> board;
    private final @Nonnull S lightPlayer;
    private final @Nonnull S darkPlayer;

    public FullStateSource(
            @Nonnull Board<P> board,
            @Nonnull S lightPlayer,
            @Nonnull S darkPlayer
    ) {
        this.board = board;
        this.lightPlayer = lightPlayer;
        this.darkPlayer = darkPlayer;
    }

    private S getPlayer(@Nonnull PlayerType turn) {
        return switch (turn) {
            case LIGHT -> lightPlayer;
            case DARK -> darkPlayer;
        };
    }

    @Override
    public RolledGameState<P, S, R> createRolledState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn,
            @Nonnull R roll
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
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn,
            @Nonnull R roll,
            @Nonnull Move<P> move
    ) {
        return new MovedGameState<>(
                board, lightPlayer, darkPlayer,
                turn, roll, move
        );
    }

    @Override
    public WaitingForRollGameState<P, S, R> createWaitingForRollState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn
    ) {
        return new WaitingForRollGameState<>(
                board, lightPlayer, darkPlayer, turn
        );
    }

    @Override
    public WaitingForMoveGameState<P, S, R> createWaitingForMoveState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn,
            @Nonnull R roll
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
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType winner
    ) {
        return new WinGameState<>(
                board, lightPlayer, darkPlayer, winner
        );
    }
}
