package net.royalur.stats.cli;

import net.royalur.Game;
import net.royalur.agent.Agent;
import net.royalur.agent.BetterGreedyAgent;
import net.royalur.agent.GreedyAgent;
import net.royalur.agent.LutAgent;
import net.royalur.cli.CLI;

import net.royalur.lut.Lut;
import net.royalur.model.GameMetadata;
import net.royalur.model.GameSettings;
import net.royalur.rules.RuleSet;
import net.royalur.rules.simple.SimpleRuleSet;
import net.royalur.rules.simple.SimpleRuleSetProvider;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;
import net.royalur.stats.GameStats;
import net.royalur.stats.GameStatsSummary;
import net.royalur.stats.GameStatsTarget;
import net.royalur.stats.SummaryStat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Throw-away functions to potentially be turned into proper
 * CLI commands at some point.
 */
public class OldStatsFunctions {

    /**
     * Generates statistics for a single game generated by {@code gameGenerator},
     * and played by two AI agents.
     */
    private GameStats testAgentActions(
            GameSettings settings,
            Function<SimpleRuleSet, Agent> lightAgentGenerator,
            Function<SimpleRuleSet, Agent> darkAgentGenerator
    ) {
        Game game = Game.create(settings);
        RuleSet rules = game.getRules();
        if (!(rules instanceof SimpleRuleSet simpleRules))
            throw new IllegalArgumentException("Game does not use simple rules");

        Agent light = lightAgentGenerator.apply(simpleRules);
        Agent dark = darkAgentGenerator.apply(simpleRules);
        Agent.playAutonomously(game, light, dark);
        return GameStats.gather(game);
    }

    /**
     * Runs tests using AI agents with many game settings.
     */
    public void testAgentActions(
            List<GameSettings> settingsList,
            Function<SimpleRuleSet, Agent> agent1Generator,
            Function<SimpleRuleSet, Agent> agent2Generator,
            int tests,
            GameStatsTarget[] reportTargets
    ) {
        System.out.println("Testing " + settingsList.size() + " sets of rules:");
        System.out.println("* <measure>: <mean> ± <std dev> -");
        System.out.println("      Q1=<25th-percentile>, Q2=<50th>, Q3=<75th>");
        System.out.println("      middle-90%=[<5th percentile>, <95th percentile>]");
        System.out.println("* drama = number of lead changes per game");
        System.out.println("* turns-in-lead = # of turns the winner had the lead before winning");

        for (int index = 0; index < settingsList.size(); ++index) {
            GameSettings settings = settingsList.get(index);
            String desc = settings.getBoardShape().getID()
                    + ", " + settings.getPaths().getID()
                    + ", " + settings.getStartingPieceCount() + " pieces"
                    + ", " + settings.getDice().getID()
                    + ", " + (settings.areRosettesSafe() ? "safe" : "unsafe")
                    + ", " + (settings.doRosettesGrantExtraRolls() ? "rosettes+" : "rosettes-")
                    + ", " + (settings.doCapturesGrantExtraRolls() ? "captures+" : "captures-");

            System.out.println("Processing " + desc);

            GameStats[] stats = new GameStats[tests];
            int agent1Wins = 0;
            int agent2Wins = 0;
            int lightWins = 0;
            int darkWins = 0;
            long start = System.nanoTime();
            for (int test = 0; test < tests; ++test) {
                boolean swap = (test % 2 == 0);
                GameStats gameStats = testAgentActions(
                        settings,
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

                if (test % 10000 == 0) {
                    System.out.println(".. " + test + " / " + tests);
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
            System.out.printf("Agent 1 won %.4f%% of games%n", agent1WinPercentage);
            System.out.printf("Agent 2 won %.4f%% of games%n", agent2WinPercentage);
            System.out.println();
            System.out.printf("Light won %.4f%% of games%n", lightWinPercentage);
            System.out.printf("Dark won %.4f%% of games%n", darkWinPercentage);
            System.out.println();
        }
    }

    private static void runGames() throws IOException {
        Lut lut = Lut.read(new File("./models/masters.rgu"));
        GameSettings settings = lut.getMetadata().getGameSettings();

        FastSimpleFlags flags = new FastSimpleFlags(settings);

        float[] probabilities = settings.getDice().createDice().getRollProbabilities();
        FastSimpleGame rollGame = new FastSimpleGame(settings);
        FastSimpleGame moveGame = new FastSimpleGame(settings);
        FastSimpleGame tempGame = new FastSimpleGame(settings);
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        double[] moveValues = new double[settings.getStartingPieceCount()];

        System.out.println(lut.getMaps().length);

        FastSimpleGame game = new FastSimpleGame(settings);
        game.copyFrom(Game.create(settings));
        System.out.println(lut.getLightWinPercent(game));

        long start = System.nanoTime();
        new OldStatsFunctions().testAgentActions(
                List.of(settings),
                (rules) -> new GreedyAgent(), //new LutAgent(lut),
                (rules) -> new BetterGreedyAgent(), //new LutAgent(lut),
                1000000,
                GameStatsTarget.values()
        );
        double durationMS = (System.nanoTime() - start) / 1e6d;

        System.out.println("\nTook " + CLI.MS_DURATION.format(durationMS) + " ms");
    }

    private static String leftPad(String value, int length) {
        return " ".repeat(
                Math.max(0, length - value.length())
        ) + value;
    }

    private static class RollMove {
        public int roll;
        public int move;
    }

    private static void loopLightGameStatesAndBestRollMoves(
            GameSettings settings,
            FastSimpleFlags flags,
            Lut lut,
            BiConsumer<FastSimpleGame, List<RollMove>> consumer
    ) {
        FastSimpleGame rollGame = new FastSimpleGame(settings);
        FastSimpleGame moveGame = new FastSimpleGame(settings);
        FastSimpleGame tempGame = new FastSimpleGame(settings);
        FastSimpleMoveList moveList = new FastSimpleMoveList();
        float[] probabilities = settings.getDice().createDice().getRollProbabilities();

        List<RollMove> rollMoveCache = new ArrayList<>();
        List<RollMove> bestRollMoves = new ArrayList<>();

        flags.loopLightGameStates(game -> {
            if (game.isFinished)
                return;

            double bestWinPercentage = Double.NEGATIVE_INFINITY;
            bestRollMoves.clear();

            for (int roll = 0; roll < probabilities.length; ++roll) {
                if (probabilities[roll] <= 0.0f)
                    continue;

                rollGame.copyFrom(game);
                rollGame.applyRoll(roll, moveList);

                if (rollGame.isWaitingForMove()) {
                    // Evaluate each possible move.
                    for (int moveIndex = 0; moveIndex < moveList.moveCount; ++moveIndex) {
                        moveGame.copyFrom(rollGame);
                        moveGame.applyMove(moveList.moves[moveIndex]);

                        double winPercentage = lut.getLightWinPercent(moveGame, tempGame);
                        if (winPercentage > bestWinPercentage) {
                            bestRollMoves.clear();
                        }
                        if (winPercentage >= bestWinPercentage) {
                            bestWinPercentage = winPercentage;
                            RollMove rollMove;
                            if (bestRollMoves.size() == rollMoveCache.size()) {
                                rollMove = new RollMove();
                                rollMoveCache.add(rollMove);
                            } else {
                                rollMove = rollMoveCache.get(bestRollMoves.size());
                            }
                            rollMove.move = moveList.moves[moveIndex];
                            rollMove.roll = roll;
                            bestRollMoves.add(rollMove);
                        }
                    }
                } else {
                    double winPercentage = lut.getLightWinPercent(rollGame, tempGame);
                    if (winPercentage > bestWinPercentage) {
                        bestRollMoves.clear();
                    }
                    if (winPercentage >= bestWinPercentage) {
                        bestWinPercentage = winPercentage;
                        RollMove rollMove;
                        if (bestRollMoves.size() == rollMoveCache.size()) {
                            rollMove = new RollMove();
                            rollMoveCache.add(rollMove);
                        } else {
                            rollMove = rollMoveCache.get(bestRollMoves.size());
                        }
                        // Indicate that no move was made (-1 represents introducing a piece)
                        rollMove.move = -2;
                        rollMove.roll = roll;
                        bestRollMoves.add(rollMove);
                    }
                }
            }
            consumer.accept(game, bestRollMoves);
        });
    }

    private static void findHowOftenRollsAreBest() throws IOException {
        Lut lut = Lut.read(new File("./models/finkel.rgu"));
        LutAgent agent = new LutAgent(lut);
        GameSettings settings = GameSettings.FINKEL;

        int pieceCount = settings.getStartingPieceCount() + 1;
        RuleSet rules = new SimpleRuleSetProvider().create(settings, new GameMetadata());

        AtomicInteger[] rollExclusivelyBestCounts = new AtomicInteger[5];
        AtomicInteger[] bestCounts = new AtomicInteger[5];
        AtomicInteger[] noMoveBestCounts = new AtomicInteger[5];
        for (int roll = 0; roll <= 4; ++roll) {
            rollExclusivelyBestCounts[roll] = new AtomicInteger(0);
            bestCounts[roll] = new AtomicInteger(0);
            noMoveBestCounts[roll] = new AtomicInteger(0);
        }
        FastSimpleFlags flags = new FastSimpleFlags(settings);
        boolean[] seenRolls = new boolean[5];
        loopLightGameStatesAndBestRollMoves(
                settings, flags, lut,
                (game, rollMoves) -> {
                    Arrays.fill(seenRolls, false);

                    // Exclusive best.
                    if (rollMoves.size() == 1) {
                        RollMove rollMove = rollMoves.get(0);
                        rollExclusivelyBestCounts[rollMove.roll].incrementAndGet();
                    }

                    for (RollMove rollMove : rollMoves) {
                        // We don't want to count when there are two best moves for one roll.
                        // We just care about the roll being the best or not.
                        if (seenRolls[rollMove.roll])
                            continue;
                        seenRolls[rollMove.roll] = true;

                        // It is the best roll.
                        bestCounts[rollMove.roll].incrementAndGet();

                        // No moves *and* best.
                        if (rollMove.move == -2) {
                            noMoveBestCounts[rollMove.roll].incrementAndGet();
                            if (rollMove.roll == 1 && noMoveBestCounts[0].get() < 10) {
                                System.out.println(game);
                            }
                        }
                    }
                }
        );

        System.out.println("Roll:");
        for (int roll = 0; roll <= 4; ++roll) {
            System.out.println("* roll of " + roll + ":");
            System.out.println("  - Best in " + bestCounts[roll].get() + " states");
            System.out.println("  - Exclusively best in " + rollExclusivelyBestCounts[roll].get() + " states");
            System.out.println("  - Best when no moves in " + noMoveBestCounts[roll].get() + " states");
        }
    }
}
