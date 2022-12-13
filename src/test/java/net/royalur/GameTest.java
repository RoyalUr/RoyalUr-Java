package net.royalur;

import net.royalur.agent.RandomAgent;
import net.royalur.model.Player;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.model.path.MastersPathPair;
import net.royalur.rules.simple.SimplePiece;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTest {

    @RepeatedTest(3)
    public void testStandardBellGameRandom() {
        Game<SimplePiece, PlayerState, Roll> game = Game.createStandard();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>(game, Player.LIGHT);
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>(game, Player.DARK);
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
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>(game, Player.LIGHT);
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>(game, Player.DARK);
        int actions = game.playAutonomously(light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 72 actions.
        int winMinActions = (2 /* roll + move */) * (4 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = (2 /* zeroes rolled per piece scored */) * (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertTrue(game.states.size() >= actions);
    }

    @RepeatedTest(3)
    public void testAsebGameRandom() {
        Game<SimplePiece, PlayerState, Roll> game = Game.createAseb();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>(game, Player.LIGHT);
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>(game, Player.DARK);
        int actions = game.playAutonomously(light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 72 actions.
        int winMinActions = (2 /* roll + move */) * (4 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = (2 /* zeroes rolled per piece scored */) * (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertTrue(game.states.size() >= actions);
    }
}
