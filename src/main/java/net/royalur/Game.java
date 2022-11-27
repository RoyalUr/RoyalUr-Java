package net.royalur;

import net.royalur.model.GameState;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.rules.RuleSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A game is modelled as metadata about the players,
 * and a list of GameStates.
 * @param <P> The type of pieces that are stored on the board.
 * @param <S> The type of state that is stored for each player.
 * @param <R> The type of rolls that may be made.
 */
public abstract class Game<P extends Piece, S extends PlayerState, R extends Roll> {

    /**
     * The set of rules that are being used for this game.
     */
    public final @Nonnull RuleSet<P, S, R> rules;

    /**
     * The states that have occurred so far in the game.
     * The last state in the list is the current state of the game.
     */
    public final @Nonnull List<GameState<P, S, R>> states;

    /**
     * @param rules The set of rules that are being used for this game.
     * @param states The states that have occurred so far in the game.
     */
    public Game(@Nonnull RuleSet<P, S, R> rules, @Nonnull List<GameState<P, S, R>> states) {
        if (states.isEmpty())
            throw new IllegalArgumentException("Games must have at least one state to play from");

        this.rules = rules;
        this.states = new ArrayList<>(states);
    }
}
