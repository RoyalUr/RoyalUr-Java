package net.royalur;

import net.royalur.agent.Agent;
import net.royalur.agent.DeterministicAgent;
import net.royalur.agent.RandomAgent;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.model.path.MurrayPathPair;
import net.royalur.model.path.SkiriukPathPair;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    @Test
    public void testCopy() {
        Game<Piece, PlayerState, Roll> game = Game.createFinkel();
        assertNotNull(game);

        Game<Piece, PlayerState, Roll> copy = game.copy();
        assertNotNull(copy);
        assertNotSame(game, copy);
        assertSame(game.getRules(), copy.getRules());
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());

        DeterministicAgent<Piece, PlayerState, Roll> agent = new DeterministicAgent<>();

        game.rollDice(2);
        agent.playTurn(game);

        assertSame(game.getRules(), copy.getRules());
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertNotEquals(game.getStates(), copy.getStates());

        copy = game.copy();
        assertSame(game.getRules(), copy.getRules());
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());

        copy.rollDice(2);
        assertSame(game.getRules(), copy.getRules());
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertNotEquals(game.getStates(), copy.getStates());

        copy = game.copy();
        assertSame(game.getRules(), copy.getRules());
        assertEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());

        copy.putMetadata("TEST", "Some data");
        assertSame(game.getRules(), copy.getRules());
        assertNotEquals(game.getMetadata(), copy.getMetadata());
        assertEquals(game.getStates(), copy.getStates());
    }

    @RepeatedTest(3)
    public void testStandardBellGameRandom() {
        Game<Piece, PlayerState, Roll> game = Game.createFinkel();
        RandomAgent<Piece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<Piece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = Agent.playAutonomously(game, light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 72 actions.
        int winMinActions = (2 /* roll + move */) * (4 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = (2 /* zeroes rolled per piece scored */) * (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertEquals(actions, game.getActionStates().size());
    }

    @RepeatedTest(3)
    public void testStandardMastersGameRandom() {
        Game<Piece, PlayerState, Roll> game = Game.builder().masters().build();
        RandomAgent<Piece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<Piece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = Agent.playAutonomously(game, light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 77 actions.
        int winMinActions = (2 /* roll + move */) * (5 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertEquals(actions, game.getActionStates().size());
    }

    @RepeatedTest(3)
    public void testStandardSkiriukGameRandom() {
        Game<Piece, PlayerState, Roll> game = Game.builder().finkel().paths(new SkiriukPathPair()).build();
        RandomAgent<Piece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<Piece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = Agent.playAutonomously(game, light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 91 actions.
        int winMinActions = (2 /* roll + move */) * (6 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertEquals(actions, game.getActionStates().size());
    }

    @RepeatedTest(3)
    public void testStandardMurrayGameRandom() {
        Game<Piece, PlayerState, Roll> game = Game.builder().finkel().paths(new MurrayPathPair()).build();
        RandomAgent<Piece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<Piece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = Agent.playAutonomously(game, light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 105 actions.
        int winMinActions = (2 /* roll + move */) * (7 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertEquals(actions, game.getActionStates().size());
    }

    @RepeatedTest(3)
    public void testAsebGameRandom() {
        Game<Piece, PlayerState, Roll> game = Game.createAseb();
        RandomAgent<Piece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<Piece, PlayerState, Roll> dark = new RandomAgent<>();
        int actions = Agent.playAutonomously(game, light, dark);
        assertTrue(game.isFinished());

        // The shortest possible game requires 77 actions.
        int winMinActions = (2 /* roll + move */) * (5 /* min. moves per piece */) * (7 /* pieces */);
        int loseMinActions = /* 1  zero rolled per piece scored */ (7 /* pieces */);
        assertTrue(actions >= winMinActions + loseMinActions);
        assertEquals(actions, game.getActionStates().size());
    }
}
