package net.royalur;

import net.royalur.agent.RandomAgent;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.model.path.MastersPathPair;
import net.royalur.model.path.MurrayPathPair;
import net.royalur.model.path.SkiriukPathPair;
import net.royalur.rules.simple.SimplePiece;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    @RepeatedTest(3)
    public void testStandardBellGameRandom() {
        Game<SimplePiece, PlayerState, Roll> game = Game.createStandard();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = game.playAutonomously(light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 72 actions.
        int winMinActions = (2 /* roll + move */) * (4 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = (2 /* zeroes rolled per piece scored */) * (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertTrue(game.states.size() >= actions);
    }

    @RepeatedTest(3)
    public void testStandardMastersGameRandom() {
        Game<SimplePiece, PlayerState, Roll> game = Game.builder().standard().paths(new MastersPathPair()).build();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = game.playAutonomously(light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 77 actions.
        int winMinActions = (2 /* roll + move */) * (5 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertTrue(game.states.size() >= actions);
    }

    @RepeatedTest(3)
    public void testStandardSkiriukGameRandom() {
        Game<SimplePiece, PlayerState, Roll> game = Game.builder().standard().paths(new SkiriukPathPair()).build();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = game.playAutonomously(light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 91 actions.
        int winMinActions = (2 /* roll + move */) * (6 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertTrue(game.states.size() >= actions);
    }

    @RepeatedTest(3)
    public void testStandardMurrayGameRandom() {
        Game<SimplePiece, PlayerState, Roll> game = Game.builder().standard().paths(new MurrayPathPair()).build();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = game.playAutonomously(light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 105 actions.
        int winMinActions = (2 /* roll + move */) * (7 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertTrue(game.states.size() >= actions);
    }

    @RepeatedTest(3)
    public void testAsebGameRandom() {
        Game<SimplePiece, PlayerState, Roll> game = Game.createAseb();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = game.playAutonomously(light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 77 actions.
        int winMinActions = (2 /* roll + move */) * (5 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertTrue(game.states.size() >= actions);
    }
}
