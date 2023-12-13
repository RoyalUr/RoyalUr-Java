package net.royalur;

import net.royalur.agent.Agent;
import net.royalur.agent.FinkelLUTAgent;
import net.royalur.agent.LikelihoodAgent;
import net.royalur.agent.utility.PiecesAdvancedUtilityFn;
import net.royalur.lut.StateLUT;
import net.royalur.lut.store.BigEntryStore;
import net.royalur.model.GameSettings;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.SimpleRuleSet;
import net.royalur.stats.GameStats;
import net.royalur.stats.GameStatsSummary;
import net.royalur.stats.GameStatsTarget;
import net.royalur.stats.SummaryStat;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This file intends to hold tests that can be performed
 * to different sets of game rules and paths to compare them.
 */
public class RGUStatistics {

    /**
     * Instantiate to run statistics about the paths available for the Royal Game of Ur.
     */
    public RGUStatistics() {}

    /**
     * Generates statistics for a single game generated by {@code gameGenerator},
     * and played by two AI agents.
     * @param gameGenerator A generator to produce a new game for the agents to play.
     * @param lightAgentGenerator A generator to produce the light agent to play the game.
     * @param darkAgentGenerator A generator to produce the dark agent to play the game.
     * @return Statistics about the game that was played between two random agents.
     */
    private @Nonnull GameStats testAgentActions(
            @Nonnull Supplier<Game<Piece, PlayerState, Roll>> gameGenerator,
            @Nonnull Function<
                    SimpleRuleSet<Piece, PlayerState, Roll>,
                    Agent<Piece, PlayerState, Roll>
            > lightAgentGenerator,
            @Nonnull Function<
                    SimpleRuleSet<Piece, PlayerState, Roll>,
                    Agent<Piece, PlayerState, Roll>
            > darkAgentGenerator
    ) {

        Game<Piece, PlayerState, Roll> game = gameGenerator.get();
        RuleSet<Piece, PlayerState, Roll> rules = game.getRules();
        if (!(rules instanceof SimpleRuleSet<Piece, PlayerState, Roll> simpleRules))
            throw new IllegalArgumentException("Game does not use simple rules");

        Agent<Piece, PlayerState, Roll> light = lightAgentGenerator.apply(simpleRules);
        Agent<Piece, PlayerState, Roll> dark = darkAgentGenerator.apply(simpleRules);
        Agent.playAutonomously(game, light, dark);
        return GameStats.gather(game);
    }

    /**
     * Runs tests using AI agents with many game settings.
     * @param agent1Generator The generator of one of the agents to play in each game.
     * @param agent2Generator The generator of one of the agents to play in each game.
     * @param tests The number of tests to run for each game setting.
     */
    public void testAgentActions(
            @Nonnull Function<
                    SimpleRuleSet<Piece, PlayerState, Roll>,
                    Agent<Piece, PlayerState, Roll>
            > agent1Generator,
            @Nonnull Function<
                    SimpleRuleSet<Piece, PlayerState, Roll>,
                    Agent<Piece, PlayerState, Roll>
            > agent2Generator,
            int tests,
            @Nonnull GameStatsTarget[] reportTargets
    ) {

        List<Supplier<Game<Piece, PlayerState, Roll>>> generators = List.of(
                () -> Game.builder().finkel().build()//,
//                () -> Game.builder().finkel().safeRosettes(false).build(),
//                () -> Game.builder().finkel().rosettesGrantExtraRolls(false).build(),
//                () -> Game.builder().finkel().capturesGrantExtraRolls(true).build(),
//
//                // Blitz
//                () -> Game.builder()
//                        .masters()
//                        .startingPieceCount(5)
//                        .safeRosettes(false)
//                        .capturesGrantExtraRolls(true)
//                        .build(),
//
//                () -> Game.builder().masters().safeRosettes(true).build(),
//                () -> Game.builder().masters().safeRosettes(false).build(),
//                () -> Game.builder().finkel().paths(new SkiriukPathPair()).build(),
//                () -> Game.builder().finkel().paths(new MurrayPathPair()).build(),
//                () -> Game.builder().aseb().build()
//                () -> Game.builder().finkel().dice(DiceType.THREE_BINARY_0MAX).build()
        );

        System.out.println("Testing " + generators.size() + " sets of rules:");
        System.out.println("* <measure>: <mean> ± <std dev> -");
        System.out.println("      Q1=<25th-percentile>, Q2=<50th>, Q3=<75th>");
        System.out.println("      middle-90%=[<5th percentile>, <95th percentile>]");
        System.out.println("* drama = number of lead changes per game");
        System.out.println("* turns-in-lead = # of turns the winner had the lead before winning");

        for (int index = 0; index < generators.size(); ++index) {
            Supplier<Game<Piece, PlayerState, Roll>> gameGenerator = generators.get(index);
            Game<Piece, PlayerState, Roll> sample = gameGenerator.get();
            String desc = sample.getBoard().getShape().getName().getTextName()
                    + ", " + sample.getRules().getPaths().getName().getTextName()
                    + ", " + sample.getRules().getPlayerStateProvider().getStartingPieceCount() + " pieces"
                    + ", " + sample.getRules().getDiceFactory().getName().getTextName()
                    + ", " + (sample.getRules().areRosettesSafe() ? "safe" : "unsafe")
                    + ", " + (sample.getRules().doRosettesGrantExtraRolls() ? "rosettes+" : "rosettes-")
                    + ", " + (sample.getRules().doCapturesGrantExtraRolls() ? "captures+" : "captures-");

            GameStats[] stats = new GameStats[tests];
            int agent1Wins = 0;
            int agent2Wins = 0;
            int lightWins = 0;
            int darkWins = 0;
            long start = System.nanoTime();
            for (int test = 0; test < tests; ++test) {
                boolean swap = (test % 2 == 0);
                GameStats gameStats = testAgentActions(
                        gameGenerator,
                        (swap ? agent2Generator : agent1Generator),
                        (swap ? agent1Generator : agent2Generator)
                );
                stats[test] = gameStats;
                if ((!swap && gameStats.didLightWin()) || (swap && !gameStats.didLightWin())) {
                    agent1Wins += 1;
                } else {
                    agent2Wins += 1;
                }
                if (gameStats.didLightWin()) {
                    lightWins += 1;
                } else {
                    darkWins += 1;
                }
            }
            long nanosPerTest = (System.nanoTime() - start) / tests;
            double msPerTest = (double) nanosPerTest / 1_000_000.0;
            String timings = String.format(" (%.2f ms/game)", msPerTest);
            GameStatsSummary summary = GameStats.summarise(stats);

            System.out.println("\n#" + (index + 1) + ". " + desc + timings + ":");
            for (GameStatsTarget target : reportTargets) {

                List<String> reports = new ArrayList<>();
                reports.add(String.format(
                        "turns: %.0f ± %.0f - Q1=%.0f, Q2=%.0f, Q3=%.0f, middle-90%%=[%.0f, %.0f]",
                        summary.getTurnsStatistic(target, SummaryStat.MEAN),
                        summary.getTurnsStatistic(target, SummaryStat.STD_DEV),
                        summary.getTurnsStatistic(target, SummaryStat.PERCENTILE_25),
                        summary.getTurnsStatistic(target, SummaryStat.MEDIAN),
                        summary.getTurnsStatistic(target, SummaryStat.PERCENTILE_75),
                        summary.getTurnsStatistic(target, SummaryStat.PERCENTILE_5),
                        summary.getTurnsStatistic(target, SummaryStat.PERCENTILE_95)
                ));
                reports.add(String.format(
                        "moves: %.0f ± %.0f - Q1=%.0f, Q2=%.0f, Q3=%.0f, middle-90%%=[%.0f, %.0f]",
                        summary.getMovesStatistic(target, SummaryStat.MEAN),
                        summary.getMovesStatistic(target, SummaryStat.STD_DEV),
                        summary.getMovesStatistic(target, SummaryStat.PERCENTILE_25),
                        summary.getMovesStatistic(target, SummaryStat.MEDIAN),
                        summary.getMovesStatistic(target, SummaryStat.PERCENTILE_75),
                        summary.getMovesStatistic(target, SummaryStat.PERCENTILE_5),
                        summary.getMovesStatistic(target, SummaryStat.PERCENTILE_95)
                ));
                reports.add(String.format(
                        "rolls: %.0f ± %.0f - Q1=%.0f, Q2=%.0f, Q3=%.0f, middle-90%%=[%.0f, %.0f]",
                        summary.getRollsStatistic(target, SummaryStat.MEAN),
                        summary.getRollsStatistic(target, SummaryStat.STD_DEV),
                        summary.getRollsStatistic(target, SummaryStat.PERCENTILE_25),
                        summary.getRollsStatistic(target, SummaryStat.MEDIAN),
                        summary.getRollsStatistic(target, SummaryStat.PERCENTILE_75),
                        summary.getRollsStatistic(target, SummaryStat.PERCENTILE_5),
                        summary.getRollsStatistic(target, SummaryStat.PERCENTILE_95)
                ));
                reports.add(String.format(
                        "drama: %.1f ± %.1f - Q1=%.0f, Q2=%.0f, Q3=%.0f, middle-90%%=[%.0f, %.0f]",
                        summary.getDramaStatistic(target, SummaryStat.MEAN),
                        summary.getDramaStatistic(target, SummaryStat.STD_DEV),
                        summary.getDramaStatistic(target, SummaryStat.PERCENTILE_25),
                        summary.getDramaStatistic(target, SummaryStat.MEDIAN),
                        summary.getDramaStatistic(target, SummaryStat.PERCENTILE_75),
                        summary.getDramaStatistic(target, SummaryStat.PERCENTILE_5),
                        summary.getDramaStatistic(target, SummaryStat.PERCENTILE_95)
                ));

                if (target == GameStatsTarget.OVERALL) {
                    reports.add(String.format(
                            "turns-in-lead: %.0f ± %.0f - Q1=%.0f, Q2=%.0f, Q3=%.0f, middle-90%%=[%.0f, %.0f]",
                            summary.getTurnsInLeadStatistic(SummaryStat.MEAN),
                            summary.getTurnsInLeadStatistic(SummaryStat.STD_DEV),
                            summary.getTurnsInLeadStatistic(SummaryStat.PERCENTILE_25),
                            summary.getTurnsInLeadStatistic(SummaryStat.MEDIAN),
                            summary.getTurnsInLeadStatistic(SummaryStat.PERCENTILE_75),
                            summary.getTurnsInLeadStatistic(SummaryStat.PERCENTILE_5),
                            summary.getTurnsInLeadStatistic(SummaryStat.PERCENTILE_95)
                    ));
                }

                System.out.println(target.getName() + ":\n* " + String.join("\n* ", reports) + "\n\n");
            }
            double agent1WinPercentage = 100.0 * ((double) agent1Wins / tests);
            double agent2WinPercentage = 100.0 * ((double) agent2Wins / tests);
            double lightWinPercentage = 100.0 * ((double) lightWins / tests);
            double darkWinPercentage = 100.0 * ((double) darkWins / tests);
            System.out.printf("Agent 1 won %.2f%% of games%n", agent1WinPercentage);
            System.out.printf("Agent 2 won %.2f%% of games%n", agent2WinPercentage);
            System.out.println();
            System.out.printf("Light won %.2f%% of games%n", lightWinPercentage);
            System.out.printf("Dark won %.2f%% of games%n", darkWinPercentage);
            System.out.println();
        }
    }

    /**
     * The main entrypoint to run statistics about the Royal Game of Ur board shapes and paths.
     * @param args Ignored.
     */
    public static void main(String[] args) throws IOException {
        StateLUT lut = new StateLUT(GameSettings.FINKEL);
        BigEntryStore states = lut.readStateStore(new File("./finkel.rgu"));

        new RGUStatistics().testAgentActions(
                rules -> new FinkelLUTAgent<>(states),
                rules -> new FinkelLUTAgent<>(states),
                1000,
                new GameStatsTarget[] {GameStatsTarget.OVERALL}
        );
    }
}
