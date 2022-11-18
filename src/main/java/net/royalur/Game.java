package net.royalur;

import net.royalur.model.GameState;
import net.royalur.rules.RuleSet;

import java.util.ArrayList;
import java.util.List;

/**
 * A game is modelled as metadata about the players,
 * and a list of GameStates.
 */
public abstract class Game {

    /**
     * The set of rules that are being used for this game.
     */
    public final RuleSet rules;

    /**
     * The states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     */
    public final List<GameState> states;

    /**
     * @param rules The set of rules that are being used for this game.
     */
    public Game(RuleSet rules) {
        this.rules = rules;
        this.states = new ArrayList<>();
    }

    /**
     * @param rules The set of rules that are being used for this game.
     * @param states The states that have occurred so far in the game.
     */
    public Game(RuleSet rules, List<GameState> states) {
        this(rules);
        this.states.addAll(states);
    }
}
