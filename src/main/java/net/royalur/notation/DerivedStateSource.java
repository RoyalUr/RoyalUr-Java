package net.royalur.notation;

import net.royalur.model.*;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Produces game states from previous game states using the actions
 * that were taken in a game. This effectively simulates games and
 * uses saved information to fill in the gaps and as a sanity check.
 */
public class DerivedStateSource extends StateSource {

    private final List<GameState> states;
    private int stateIndex;

    public DerivedStateSource(GameState initialState) {
        states = new ArrayList<>();
        stateIndex = 0;
        states.add(initialState);
    }

    public List<GameState> getAllStates() {
        return states;
    }

    public int lastIndexOf(GameState state) {
        for (int index = states.size() - 1; index >= 0; --index) {
            if (state.equals(states.get(index)))
                return index;
        }
        throw new IllegalArgumentException("State could not be found");
    }

    private GameState peekState() {
        if (stateIndex >= states.size())
            throw new IllegalStateException("No available states!");
        return states.get(stateIndex);
    }

    private GameState nextState() {
        if (stateIndex >= states.size())
            throw new IllegalStateException("No available states!");

        int index = stateIndex;
        stateIndex += 1;
        return states.get(index);
    }

    private GameState getCurrentState() {
        if (stateIndex == states.size()) {
            return states.get(stateIndex - 1);
        } else {
            return states.get(stateIndex);
        }
    }

    private void pushStates(List<GameState> states) {
        if (stateIndex < this.states.size()) {
            throw new IllegalStateException(
                    "There are remaining unused states! "
                            + this.states.subList(stateIndex, this.states.size())
            );
        }
        this.states.addAll(states);
    }

    @Override
    public RolledGameState createRolledState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll
    ) {
        GameState precedingState = nextState();
        if (!(precedingState instanceof WaitingForRollGameState waitingState))
            throw new IllegalStateException("Preceding state is not a WaitingForRollGameState");

        pushStates(rules.applyRoll(waitingState, timeSinceGameStartMs, roll));
        GameState state = nextState();
        if (!(state instanceof RolledGameState rolledState))
            throw new IllegalStateException("The state was not a RolledGameState after applying a roll");

        return rolledState;
    }

    @Override
    public MovedGameState createMovedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll,
            Move move
    ) {
        // Support for implied roll states from moves.
        if (peekState() instanceof WaitingForRollGameState) {
            createRolledState(rules, timeSinceGameStartMs, turn, roll);
        }

        GameState precedingState = nextState();
        if (!(precedingState instanceof WaitingForMoveGameState waitingState))
            throw new IllegalStateException("Preceding state is not a WaitingForMoveGameState");

        pushStates(rules.applyMove(waitingState, timeSinceGameStartMs, move));
        GameState state = nextState();
        if (!(state instanceof MovedGameState movedState))
            throw new IllegalStateException("The state was not a MovedGameState after applying a move");

        return movedState;
    }

    @Override
    public WaitingForRollGameState createWaitingForRollState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType turn
    ) {
        GameState state = getCurrentState();
        if (!(state instanceof WaitingForRollGameState waitingState)) {
            throw new IllegalStateException(
                    "The state is not a WaitingForRollGameState, it is a " + state.getClass()
            );
        }

        if (waitingState.getTurn() != turn) {
            throw new IllegalStateException(
                    "Inconsistent derivation! "
                            + "Expected turn = " + turn.getName()
                            + ", actual turn = " + waitingState.getTurn().getName()
            );
        }
        return waitingState;
    }

    @Override
    public WaitingForMoveGameState createWaitingForMoveState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType turn,
            Roll roll
    ) {
        GameState state = getCurrentState();
        if (!(state instanceof WaitingForMoveGameState waitingState))
            throw new IllegalStateException("The state is not a WaitingForMoveGameState");

        if (waitingState.getTurn() != turn) {
            throw new IllegalStateException(
                    "Inconsistent derivation! "
                            + "Expected turn = " + turn.getName()
                            + ", actual turn = " + waitingState.getTurn().getName()
            );
        }
        if (waitingState.getRoll().value() != roll.value()) {
            throw new IllegalStateException(
                    "Inconsistent derivation! "
                            + "Expected roll value = " + roll.value()
                            + ", actual roll value = " + waitingState.getRoll().value()
            );
        }
        return waitingState;
    }

    @Override
    public ResignedGameState createResignedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            PlayerType player
    ) {
        GameState currentState = nextState();
        if (currentState instanceof EndGameState)
            throw new IllegalStateException("Game is already finished");

        pushStates(rules.applyResign(currentState, timeSinceGameStartMs, player));
        GameState state = nextState();
        if (!(state instanceof ResignedGameState resignedState))
            throw new IllegalStateException("The state was not a ResignedGameState after applying a resign");

        return resignedState;
    }

    @Override
    public AbandonedGameState createAbandonedState(
            RuleSet rules,
            long timeSinceGameStartMs,
            AbandonReason reason,
            @Nullable PlayerType player
    ) {
        GameState currentState = nextState();
        if (currentState instanceof EndGameState)
            throw new IllegalStateException("Game is already finished");

        pushStates(rules.applyAbandon(currentState, timeSinceGameStartMs, reason, player));
        GameState state = nextState();
        if (!(state instanceof AbandonedGameState abandonedState))
            throw new IllegalStateException("The state was not an AbandonedGameState after applying an abandon");

        return abandonedState;
    }

    @Override
    public EndGameState createEndState(
            RuleSet rules,
            long timeSinceGameStartMs,
            @Nullable PlayerType winner
    ) {
        GameState state = getCurrentState();
        if (!(state instanceof EndGameState endState))
            throw new IllegalStateException("The state is not an EndGameState");

        PlayerType actualWinner = (endState.hasWinner() ? endState.getWinner() : null);
        if (actualWinner != winner) {
            throw new IllegalStateException(
                    "Inconsistent derivation! "
                            + "Expected winner = "
                            + (winner != null ? winner.getName() : null)
                            + ", actual winner = "
                            + (actualWinner != null ? actualWinner.getName() : null)
            );
        }
        return endState;
    }
}
