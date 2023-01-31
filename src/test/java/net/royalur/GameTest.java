package net.royalur;

import net.royalur.agent.DeterministicAgent;
import net.royalur.agent.RandomAgent;
import net.royalur.model.Player;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.model.path.MastersPathPair;
import net.royalur.model.path.MurrayPathPair;
import net.royalur.model.path.SkiriukPathPair;
import net.royalur.rules.simple.SimpleGame;
import net.royalur.rules.simple.SimplePiece;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testCopy() {
        SimpleGame game = Game.createStandard();
        assertNotNull(game);

        Game<SimplePiece, PlayerState, Roll> copy = game.copy();
        assertNotNull(copy);
        assertNotSame(game, copy);
        assertEquals(game.rules, copy.rules);
        assertEquals(game.lightIdentity, copy.lightIdentity);
        assertEquals(game.darkIdentity, copy.darkIdentity);
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());

        DeterministicAgent<SimplePiece, PlayerState, Roll> agent = new DeterministicAgent<>();

        game.rollDice(2);
        agent.playTurn(game, Player.LIGHT);

        assertEquals(game.rules, copy.rules);
        assertEquals(game.lightIdentity, copy.lightIdentity);
        assertEquals(game.darkIdentity, copy.darkIdentity);
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertNotEquals(game.getStates(), copy.getStates());

        copy = game.copy();
        assertEquals(game.rules, copy.rules);
        assertEquals(game.lightIdentity, copy.lightIdentity);
        assertEquals(game.darkIdentity, copy.darkIdentity);
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());

        copy.rollDice(2);
        assertEquals(game.rules, copy.rules);
        assertEquals(game.lightIdentity, copy.lightIdentity);
        assertEquals(game.darkIdentity, copy.darkIdentity);
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertNotEquals(game.getStates(), copy.getStates());

        copy = game.copy();
        assertEquals(game.rules, copy.rules);
        assertEquals(game.lightIdentity, copy.lightIdentity);
        assertEquals(game.darkIdentity, copy.darkIdentity);
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());

        copy.putMetadata("TEST", "Some data");
        assertEquals(game.rules, copy.rules);
        assertEquals(game.lightIdentity, copy.lightIdentity);
        assertEquals(game.darkIdentity, copy.darkIdentity);
        assertNotEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());
    }

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
        assertEquals(actions, game.getActionStates().size());
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
        assertEquals(actions, game.getActionStates().size());
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
        assertEquals(actions, game.getActionStates().size());
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
        assertEquals(actions, game.getActionStates().size());
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
        assertEquals(actions, game.getActionStates().size());
    }
}
