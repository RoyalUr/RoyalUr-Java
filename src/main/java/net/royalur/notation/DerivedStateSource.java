package net.royalur.notation;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 Produces game states from previous game states using the actions
 that were taken in a game. This effectively simulates games and
 uses saved information to fill in the gaps and as a sanity check.
 */
public class DerivedStateSource<
        P extends Piece, S extends PlayerState, R extends Roll
> extends StateSource<P, S, R> {

    private final @Nonnull List<GameState<P, S, R>> states;
    private int stateIndex;

    public DerivedStateSource(@Nonnull GameState<P, S, R> initialState) {
        states = new ArrayList<>();
        stateIndex = 0;
        states.add(initialState);
    }

    public @Nonnull List<GameState<P, S, R>> getAllStates() {
        return states;
    }

    public int lastIndexOf(@Nonnull GameState<P, S, R> state) {
        for (int index = states.size() - 1; index >= 0; --index) {
            if (state.equals(states.get(index)))
                return index;
        }
        throw new IllegalArgumentException("State could not be found");
    }

    private GameState<P, S, R> peekState() {
        if (stateIndex >= states.size())
            throw new IllegalStateException("No available states!");
        return states.get(stateIndex);
    }

    private GameState<P, S, R> nextState() {
        if (stateIndex >= states.size())
            throw new IllegalStateException("No available states!");

        int index = stateIndex;
        stateIndex += 1;
        return states.get(index);
    }

    private GameState<P, S, R> getCurrentState() {
        if (stateIndex == states.size()) {
            return states.get(stateIndex - 1);
        } else {
            return states.get(stateIndex);
        }
    }

    private void pushStates(List<GameState<P, S, R>> states) {
        if (stateIndex < this.states.size()) {
            throw new IllegalStateException(
                    "There are remaining unused states! " +
                            this.states.subList(stateIndex, this.states.size())
            );
        }
        this.states.addAll(states);
    }

    @Override
    public RolledGameState<P, S, R> createRolledState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn,
            @Nonnull R roll
    ) {
        GameState<P, S, R> precedingState = nextState();
        if (!(precedingState instanceof WaitingForRollGameState<P, S, R> waitingState))
            throw new IllegalStateException("Preceding state is not a WaitingForRollGameState");

        pushStates(rules.applyRoll(waitingState, roll));
        GameState<P, S, R> state = nextState();
        if (!(state instanceof RolledGameState<P, S, R> rolledState))
            throw new IllegalStateException("The state was not a RolledGameState after applying a roll");

        return rolledState;
    }

    @Override
    public MovedGameState<P, S, R> createMovedState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn,
            @Nonnull R roll,
            @Nonnull Move<P> move
    ) {
        // Support for implied roll states from moves.
        if (peekState() instanceof WaitingForRollGameState) {
            createRolledState(rules, turn, roll);
        }

        GameState<P, S, R> precedingState = nextState();
        if (!(precedingState instanceof WaitingForMoveGameState<P, S, R> waitingState))
            throw new IllegalStateException("Preceding state is not a WaitingForMoveGameState");

        pushStates(rules.applyMove(waitingState, move));
        GameState<P, S, R> state = nextState();
        if (!(state instanceof MovedGameState<P, S, R> movedState))
            throw new IllegalStateException("The state was not a MovedGameState after applying a move");

        return movedState;
    }

    @Override
    public WaitingForRollGameState<P, S, R> createWaitingForRollState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn
    ) {
        GameState<P, S, R> state = getCurrentState();
        if (!(state instanceof WaitingForRollGameState<P, S, R> waitingState)) {
            throw new IllegalStateException(
                    "The state is not a WaitingForRollGameState, it is a " + state.getClass()
            );
        }

        if (waitingState.getTurn() != turn) {
            throw new IllegalStateException(
                    "Inconsistent derivation! " +
                    "Expected turn = " + turn.getTextName() + "," +
                    "actual turn = " + waitingState.getTurn().getTextName()
            );
        }
        return waitingState;
    }

    @Override
    public WaitingForMoveGameState<P, S, R> createWaitingForMoveState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType turn,
            @Nonnull R roll
    ) {
        GameState<P, S, R> state = getCurrentState();
        if (!(state instanceof WaitingForMoveGameState<P, S, R> waitingState))
            throw new IllegalStateException("The state is not a WaitingForMoveGameState");

        if (waitingState.getTurn() != turn) {
            throw new IllegalStateException(
                    "Inconsistent derivation! " +
                    "Expected turn = " + turn.getTextName() + "," +
                    "actual turn = " + waitingState.getTurn().getTextName()
            );
        }
        if (waitingState.getRoll().value() != roll.value()) {
            throw new IllegalStateException(
                    "Inconsistent derivation! " +
                    "Expected roll value = " + roll.value() + "," +
                    "actual roll value = " + waitingState.getRoll().value()
            );
        }
        return waitingState;
    }

    @Override
    public WinGameState<P, S, R> createWinState(
            @Nonnull RuleSet<P, S, R> rules,
            @Nonnull PlayerType winner
    ) {
        GameState<P, S, R> state = getCurrentState();
        if (!(state instanceof WinGameState<P, S, R> winState))
            throw new IllegalStateException("The state is not a WinGameState");

        if (winState.getWinner() != winner) {
            throw new IllegalStateException(
                    "Inconsistent derivation! " +
                    "Expected winner = " + winner.getTextName() + "," +
                    "actual winner = " + winState.getWinner().getTextName()
            );
        }
        return winState;
    }
}
