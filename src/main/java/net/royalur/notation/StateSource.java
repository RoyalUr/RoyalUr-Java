package net.royalur.notation;

import net.royalur.model.Move;
import net.royalur.model.PlayerType;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.state.*;

/**
 * Produces game states from serialised information.
 */
public abstract class StateSource {

    public abstract RolledGameState createRolledState(
            RuleSet rules,
            PlayerType turn,
            Roll roll
    );

    public abstract MovedGameState createMovedState(
            RuleSet rules,
            PlayerType turn,
            Roll roll,
            Move move
    );

    public abstract WaitingForRollGameState createWaitingForRollState(
            RuleSet rules,
            PlayerType turn
    );

    public abstract WaitingForMoveGameState createWaitingForMoveState(
            RuleSet rules,
            PlayerType turn,
            Roll roll
    );

    public abstract WinGameState createWinState(
            RuleSet rules,
            PlayerType winner
    );
}
