package net.royalur;

import net.royalur.agent.RandomAgent;
import net.royalur.model.Player;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.path.MastersPathPair;
import net.royalur.model.path.MurrayPathPair;
import net.royalur.model.path.SkiriukPathPair;
import net.royalur.rules.simple.SimplePiece;

import java.util.List;
import java.util.function.Supplier;

/**
 * This file intends to hold tests that can be performed
 * to different sets of game rules and paths to compare them.
 */
public class RGUStatistics {

    private static int testRandomAgentActions(Supplier<Game<SimplePiece, PlayerState, Roll>> gameGenerator) {
        Game<SimplePiece, PlayerState, Roll> game = gameGenerator.get();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>(game, Player.LIGHT);
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>(game, Player.DARK);
        return game.playAutonomously(light, dark);
    }

    public static void testRandomAgentActions() {
        int tests = 10_000;
        List<Supplier<Game<SimplePiece, PlayerState, Roll>>> generators = List.of(
                () -> Game.builder().standard().paths(new BellPathPair()).build(),
                () -> Game.builder().standard().paths(new MastersPathPair()).build(),
                () -> Game.builder().standard().paths(new SkiriukPathPair()).build(),
                () -> Game.builder().standard().paths(new MurrayPathPair()).build(),
                () -> Game.builder().aseb().build()
        );
        for (Supplier<Game<SimplePiece, PlayerState, Roll>> gameGenerator : generators) {
            Game<SimplePiece, PlayerState, Roll> sample = gameGenerator.get();
            String desc = sample.getCurrentState().board.shape.name + ", " + sample.rules.paths.name;

            int[] results = new int[tests];
            int cumulativeSum = 0;
            for (int test = 0; test < tests; ++test) {
                int result = testRandomAgentActions(gameGenerator);
                results[test] = result;
                cumulativeSum += result;
            }

            double mean = (double) cumulativeSum / tests;
            double variance = 0;
            for (int index = 0; index < tests; ++index) {
                double diff = results[index] - mean;
                variance += diff * diff;
            }
            double stdDev = Math.sqrt(variance / tests);

            System.out.println(desc + ": " + ((int) mean) + " ± " + ((int) stdDev));
            System.out.println();
        }
    }

    public static void main(String[] args) {
        testRandomAgentActions();
    }
}
