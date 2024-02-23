package net.royalur.notation;

import net.royalur.model.Move;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.PlayerType;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;

/**
 * Produces game states from serialised information.
 */
public abstract class StateSource<
    P extends Piece, S extends PlayerState, R extends Roll
> {

    public abstract RolledGameState<P, S, R> createRolledState(
            RuleSet<P, S, R> rules,
            PlayerType turn,
            R roll
    );

    public abstract MovedGameState<P, S, R> createMovedState(
            RuleSet<P, S, R> rules,
            PlayerType turn,
            R roll,
            Move<P> move
    );

    public abstract WaitingForRollGameState<P, S, R> createWaitingForRollState(
            RuleSet<P, S, R> rules,
            PlayerType turn
    );

    public abstract WaitingForMoveGameState<P, S, R> createWaitingForMoveState(
            RuleSet<P, S, R> rules,
            PlayerType turn,
            R roll
    );

    public abstract WinGameState<P, S, R> createWinState(
            RuleSet<P, S, R> rules,
            PlayerType winner
    );
}
