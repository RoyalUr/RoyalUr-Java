package net.royalur;

import net.royalur.agent.RandomAgent;
import net.royalur.model.PlayerState;
import net.royalur.model.Roll;
import net.royalur.model.path.BellPathPair;
import net.royalur.model.path.MastersPathPair;
import net.royalur.model.path.MurrayPathPair;
import net.royalur.model.path.SkiriukPathPair;
import net.royalur.rules.simple.SimplePiece;
import net.royalur.stats.GameStats;
import net.royalur.stats.GameStatsSummary;
import net.royalur.stats.GameStatsTarget;
import net.royalur.stats.SummaryStat;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;

/**
 * This file intends to hold tests that can be performed
 * to different sets of game rules and paths to compare them.
 */
public class RGUStatistics {

    private static @Nonnull GameStats testRandomAgentActions(
            @Nonnull Supplier<Game<SimplePiece, PlayerState, Roll>> gameGenerator
    ) {

        Game<SimplePiece, PlayerState, Roll> game = gameGenerator.get();
        RandomAgent<SimplePiece, PlayerState, Roll> light = new RandomAgent<>();
        RandomAgent<SimplePiece, PlayerState, Roll> dark = new RandomAgent<>();
        game.playAutonomously(light, dark);
        return GameStats.gather(game);
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
            String desc = sample.getBoard().shape.getIdentifier() + ", " + sample.rules.paths.getIdentifier();

            GameStats[] stats = new GameStats[tests];
            for (int test = 0; test < tests; ++test) {
                stats[test] = testRandomAgentActions(gameGenerator);
            }
            GameStatsSummary summary = GameStats.summarise(stats);

            System.out.println(desc + ":");
            for (GameStatsTarget target : GameStatsTarget.values()) {
                double movesMean = summary.getMovesStatistic(target, SummaryStat.MEAN);
                double movesStd = summary.getMovesStatistic(target, SummaryStat.STD_DEV);
                double rollsMean = summary.getRollsStatistic(target, SummaryStat.MEAN);
                double rollsStd = summary.getRollsStatistic(target, SummaryStat.STD_DEV);
                System.out.printf(
                        "%-15s%-19s%-19s%n",
                        target.name + ":",
                        ((int) movesMean) + " moves ± " + ((int) movesStd) + ",",
                        ((int) rollsMean) + " rolls ± " + ((int) rollsStd)
                );
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        testRandomAgentActions();
    }
}
