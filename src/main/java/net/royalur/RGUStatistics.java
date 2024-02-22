package net.royalur;

import net.royalur.agent.Agent;
import net.royalur.agent.FinkelLUTAgent;
import net.royalur.agent.GreedyAgent;
import net.royalur.lut.FinkelGameStateEncoding;
import net.royalur.lut.GameStateEncoding;
import net.royalur.lut.Lut;
import net.royalur.lut.SimpleGameStateEncoding;
import net.royalur.model.GameSettings;
import net.royalur.model.Piece;
import net.royalur.model.PlayerState;
import net.royalur.model.dice.Roll;
import net.royalur.notation.JsonNotation;
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

    /*
    public void testMoveStats(
            GameSettings<Roll> settings,
            LutTrainer lut,
            OrderedUInt32BufferSet states,
            Function<FastSimpleGame, Boolean> gameFilter
    ) {
        long start = System.nanoTime();

        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();

        FastSimpleMoveList moveList = new FastSimpleMoveList();
        FastSimpleGame rollGame = new FastSimpleGame(settings);
        FastSimpleGame moveGame = new FastSimpleGame(settings);

        AtomicInteger maxMoves = new AtomicInteger(0);
        AtomicLong maxMovesStateCount = new AtomicLong(0);
        FastSimpleGame maxMovesState = new FastSimpleGame(settings);

        AtomicInteger highestWinChance = new AtomicInteger(Float.floatToIntBits(-1.0f));
        FastSimpleGame highestWinChanceGame = new FastSimpleGame(settings);
        AtomicInteger lowestWinChance = new AtomicInteger(Float.floatToIntBits(101.0f));
        FastSimpleGame lowestWinChanceGame = new FastSimpleGame(settings);

        AtomicLong[] moveCounts = new AtomicLong[settings.getStartingPieceCount() + 1];
        for (int index = 0; index < moveCounts.length; ++index) {
            moveCounts[index] = new AtomicLong(0);
        }

        AtomicLong totalMoves = new AtomicLong();
        AtomicLong introducingMoves = new AtomicLong();
        AtomicLong scoringMoves = new AtomicLong();
        AtomicLong capturingMoves = new AtomicLong();
        AtomicLong grantExtraRollMoves = new AtomicLong();

        BigDecimal bigZero = new BigDecimal("0", MathContext.DECIMAL128);
        AtomicReference<BigDecimal> overallMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> introducingMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> scoringMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> capturingMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> grantExtraRollMoveScoreSum = new AtomicReference<>(bigZero);

        AtomicReference<BigDecimal> overallRelMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> introducingRelMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> scoringRelMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> capturingRelMoveScoreSum = new AtomicReference<>(bigZero);
        AtomicReference<BigDecimal> grantExtraRollRelMoveScoreSum = new AtomicReference<>(bigZero);

        System.out.println("Processing game states...");
        long totalGameStates = lut.countStates(gameFilter);
        AtomicLong stateCount = new AtomicLong(0);

        lut.loopGameStates(game -> {
            if (game.isFinished || !gameFilter.apply(game))
                return;

            long currentStateCount = stateCount.incrementAndGet();
            if (currentStateCount % 10000000L == 0) {
                System.out.printf("* %.2f%% (%d / %d)%n", 100.0 * currentStateCount / totalGameStates, currentStateCount, totalGameStates);
            }

            float baseScore = (100f + states.getAndUnwrapFloat(encoding.encode(game))) / 2f;

            if (baseScore > Float.intBitsToFloat(highestWinChance.get())) {
                highestWinChance.set(Float.floatToIntBits(baseScore));
                highestWinChanceGame.copyFrom(game);
            }
            if (baseScore < Float.intBitsToFloat(lowestWinChance.get())) {
                lowestWinChance.set(Float.floatToIntBits(baseScore));
                lowestWinChanceGame.copyFrom(game);
            }

            for (int roll = 0; roll <= 4; ++roll) {
                rollGame.copyFrom(game);
                rollGame.applyRoll(roll, moveList);

                int moveScoreMul = (rollGame.isLightTurn ? 1 : -1);

                int currentMaxMoves = maxMoves.get();
                if (moveList.moveCount > currentMaxMoves) {
                    maxMoves.set(moveList.moveCount);
                    maxMovesStateCount.set(1);
                    maxMovesState.copyFrom(rollGame);
                } else if (moveList.moveCount == currentMaxMoves) {
                    maxMovesStateCount.incrementAndGet();
                }

                moveCounts[moveList.moveCount].incrementAndGet();

                int[] moveStatuses = new int[moveList.moveCount];
                float[] moveNewScores = new float[moveList.moveCount];

                for (int moveIndex = 0; moveIndex < moveList.moveCount; ++moveIndex) {
                    int move = moveList.moves[moveIndex];
                    moveGame.copyFrom(rollGame);
                    int moveStatus = moveGame.applyMove(move);

                    float newScore = (100f + states.getAndUnwrapFloat(encoding.encode(moveGame))) / 2f;
                    float moveScoreFloat = (newScore - baseScore) * moveScoreMul;
                    BigDecimal moveScore = new BigDecimal(moveScoreFloat, MathContext.DECIMAL128);

                    moveStatuses[moveIndex] = moveStatus;
                    moveNewScores[moveIndex] = newScore;

                    totalMoves.incrementAndGet();
                    overallMoveScoreSum.set(overallMoveScoreSum.get().add(moveScore));
                    if (FastSimpleGame.didMoveIntroducePiece(moveStatus)) {
                        introducingMoves.incrementAndGet();
                        introducingMoveScoreSum.set(introducingMoveScoreSum.get().add(moveScore));
                    }
                    if (FastSimpleGame.didMoveScorePiece(moveStatus)) {
                        scoringMoves.incrementAndGet();
                        scoringMoveScoreSum.set(scoringMoveScoreSum.get().add(moveScore));
                    }
                    if (FastSimpleGame.didMoveCapturePiece(moveStatus)) {
                        capturingMoves.incrementAndGet();
                        capturingMoveScoreSum.set(capturingMoveScoreSum.get().add(moveScore));
                    }
                    if (FastSimpleGame.didMoveGrantExtraRoll(moveStatus)) {
                        grantExtraRollMoves.incrementAndGet();
                        grantExtraRollMoveScoreSum.set(grantExtraRollMoveScoreSum.get().add(moveScore));
                    }
                }

                if (moveList.moveCount >= 2) {
                    float[] sortedMoveNewScores = Arrays.copyOf(moveNewScores, moveNewScores.length);
                    Arrays.sort(sortedMoveNewScores);

                    int mid = (sortedMoveNewScores.length - 1) / 2;
                    float medianNewScore;

                    if (sortedMoveNewScores.length % 2 == 0) {
                        medianNewScore = (sortedMoveNewScores[mid] + sortedMoveNewScores[mid + 1]) / 2.0f;
                    } else {
                        medianNewScore = sortedMoveNewScores[mid];
                    }

                    for (int moveIndex = 0; moveIndex < moveList.moveCount; ++moveIndex) {
                        int moveStatus = moveStatuses[moveIndex];
                        float moveScoreFloat = (moveNewScores[moveIndex] - medianNewScore) * moveScoreMul;
                        BigDecimal moveScore = new BigDecimal(moveScoreFloat, MathContext.DECIMAL128);

                        overallRelMoveScoreSum.set(overallRelMoveScoreSum.get().add(moveScore));
                        if (FastSimpleGame.didMoveIntroducePiece(moveStatus)) {
                            introducingRelMoveScoreSum.set(introducingRelMoveScoreSum.get().add(moveScore));
                        }
                        if (FastSimpleGame.didMoveScorePiece(moveStatus)) {
                            scoringRelMoveScoreSum.set(scoringRelMoveScoreSum.get().add(moveScore));
                        }
                        if (FastSimpleGame.didMoveCapturePiece(moveStatus)) {
                            capturingRelMoveScoreSum.set(capturingRelMoveScoreSum.get().add(moveScore));
                        }
                        if (FastSimpleGame.didMoveGrantExtraRoll(moveStatus)) {
                            grantExtraRollRelMoveScoreSum.set(grantExtraRollRelMoveScoreSum.get().add(moveScore));
                        }
                    }
                }
            }
        });
        System.out.println("Done!");
        System.out.println();

        double durationSeconds = (System.nanoTime() - start) / 1e9d;

        System.out.printf("Evaluated %d moves in %.2f seconds%n", totalMoves.get(), durationSeconds);
        System.out.printf("* %.1f%% of moves introduced a piece (%d)%n", 100.0 * introducingMoves.get() / totalMoves.get(), introducingMoves.get());
        System.out.printf("* %.1f%% of moves scored a piece (%d)%n", 100.0 * scoringMoves.get() / totalMoves.get(), scoringMoves.get());
        System.out.printf("* %.1f%% of moves captured a piece (%d)%n", 100.0 * capturingMoves.get() / totalMoves.get(), capturingMoves.get());
        System.out.printf("* %.1f%% of moves granted an extra roll (%d)%n", 100.0 * grantExtraRollMoves.get() / totalMoves.get(), grantExtraRollMoves.get());

        BigDecimal overallMoveScore = overallMoveScoreSum.get().divide(BigDecimal.valueOf(totalMoves.get()), RoundingMode.HALF_UP);
        BigDecimal introducingMoveScore = introducingMoveScoreSum.get().divide(BigDecimal.valueOf(introducingMoves.get()), RoundingMode.HALF_UP);
        BigDecimal scoringMoveScore = scoringMoveScoreSum.get().divide(BigDecimal.valueOf(scoringMoves.get()), RoundingMode.HALF_UP);
        BigDecimal capturingMoveScore = capturingMoveScoreSum.get().divide(BigDecimal.valueOf(capturingMoves.get()), RoundingMode.HALF_UP);
        BigDecimal grantExtraRollMoveScore = grantExtraRollMoveScoreSum.get().divide(BigDecimal.valueOf(grantExtraRollMoves.get()), RoundingMode.HALF_UP);

        System.out.println();
        System.out.println("Changes to win percentage after moves:");
        System.out.printf("* %+f%% for all moves%n", overallMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves introducing a piece%n", introducingMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves scoring a piece%n", scoringMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves capturing a piece%n", capturingMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves granting an extra roll%n", grantExtraRollMoveScore.doubleValue());

        BigDecimal overallRelMoveScore = overallRelMoveScoreSum.get().divide(BigDecimal.valueOf(totalMoves.get()), RoundingMode.HALF_UP);
        BigDecimal introducingRelMoveScore = introducingRelMoveScoreSum.get().divide(BigDecimal.valueOf(introducingMoves.get()), RoundingMode.HALF_UP);
        BigDecimal scoringRelMoveScore = scoringRelMoveScoreSum.get().divide(BigDecimal.valueOf(scoringMoves.get()), RoundingMode.HALF_UP);
        BigDecimal capturingRelMoveScore = capturingRelMoveScoreSum.get().divide(BigDecimal.valueOf(capturingMoves.get()), RoundingMode.HALF_UP);
        BigDecimal grantExtraRollRelMoveScore = grantExtraRollRelMoveScoreSum.get().divide(BigDecimal.valueOf(grantExtraRollMoves.get()), RoundingMode.HALF_UP);

        System.out.println();
        System.out.println("Changes to win percentage after moves relative to the median change:");
        System.out.printf("* %+f%% for all moves%n", overallRelMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves introducing a piece%n", introducingRelMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves scoring a piece%n", scoringRelMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves capturing a piece%n", capturingRelMoveScore.doubleValue());
        System.out.printf("* %+f%% for moves granting an extra roll%n", grantExtraRollRelMoveScore.doubleValue());

        System.out.println();
        System.out.printf("Max moves from a position = %d for %d positions%n", maxMoves.get(), maxMovesStateCount.get());
        System.out.println("Example position = " + maxMovesState);

        System.out.println();
        System.out.printf("Highest win chance = %.2f%%%n", Float.intBitsToFloat(highestWinChance.get()));
        System.out.println("Example position = " + highestWinChanceGame);

        System.out.println();
        System.out.printf("Lowest win chance = %.2f%%%n", Float.intBitsToFloat(lowestWinChance.get()));
        System.out.println("Example position = " + lowestWinChanceGame);

        System.out.println();
        System.out.printf("Move counts:%n");
        for (int moveCount = 0; moveCount < moveCounts.length; ++moveCount) {
            long frequency = moveCounts[moveCount].get();
            System.out.printf("* %d moves from %d states%n", moveCount, frequency);
        }
    }

    private static void runOverallMoveStatsTests() throws IOException {
        GameSettings<Roll> settings = GameSettings.FINKEL;
        LutTrainer lut = new LutTrainer(settings);
        OrderedUInt32BufferSet states = lut.readStateStore(new File("./finkel.rgu"));

        RGUStatistics statistics = new RGUStatistics();
        statistics.testMoveStats(settings, lut, states, (game) -> true);
    }

    private static void runBucketedMoveStatsTests(int buckets) throws IOException {
        GameSettings<Roll> settings = GameSettings.FINKEL;
        LutTrainer lut = new LutTrainer(settings);
        OrderedUInt32BufferSet states = lut.readStateStore(new File("./finkel.rgu"));

        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();
        RGUStatistics statistics = new RGUStatistics();
        for (int bucket = 0; bucket < buckets; ++bucket) {
            float min = bucket * 100.0f / buckets;
            float max = (bucket + 1) * 100.0f / buckets;

            System.out.printf("=========================%n");
            System.out.printf("     %.0f%% -> %.0f%%%n", min, max);
            System.out.printf("=========================%n");
            statistics.testMoveStats(settings, lut, states, (game) -> {
                float score = (100f + states.getAndUnwrapFloat(encoding.encode(game))) / 2f;
                score *= (game.isLightTurn ? 1 : -1);
                return score >= min && score <= max;
            });
        }
    }

    private static void runGames() throws IOException {
        GameSettings<Roll> settings = GameSettings.FINKEL;
        LutTrainer lut = new LutTrainer(settings);
        OrderedUInt32BufferSet states = lut.readStateStore(new File("./finkel.rgu"));

        RGUStatistics statistics = new RGUStatistics();
        statistics.testAgentActions(
                rules -> new GreedyAgent<>(),
                rules -> new FinkelLUTAgent<>(states),
                1000_000,
                GameStatsTarget.values()
        );
    }*/

    /**
     * The main entrypoint to run statistics about the Royal Game of Ur board shapes and paths.
     * @param args Ignored.
     */
    public static void main(String[] args) throws IOException {
//        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();
        GameStateEncoding encoding = new SimpleGameStateEncoding(GameSettings.FINKEL);
        JsonNotation<?, ?, Roll> jsonNotation = JsonNotation.createSimple();
        Lut<Roll> lut = Lut.read(jsonNotation, encoding, new File("./models/finkel.rgu"));
        new RGUStatistics().testAgentActions(
                (rules) -> new GreedyAgent<>(),
                (rules) -> new FinkelLUTAgent<>(lut),
                10000,
                GameStatsTarget.values()
        );

//        runMoveStatsTests();
//        runBucketedMoveStatsTests(10);
//        runGames();

//        LutTrainer.main(args);

        /*GameSettings<Roll> settings = GameSettings.FINKEL;
        FinkelGameStateEncoding encoding = new FinkelGameStateEncoding();
        LutTrainer lut = new LutTrainer(settings);
        OrderedUInt32BufferSet states = lut.readStateStore(new File("./finkel.rgu"));

        AtomicReference<Double> maxDifference = new AtomicReference<>(Double.NEGATIVE_INFINITY);
        AtomicReference<Double> minDifference = new AtomicReference<>(Double.POSITIVE_INFINITY);

        FastSimpleGame darkGame = new FastSimpleGame(settings);
        int width = settings.getBoardShape().getWidth();
        int height = settings.getBoardShape().getHeight();

        lut.loopGameStates(lightGame -> {
            if (!lightGame.isLightTurn)
                return;

            int keyLight = encoding.encode(lightGame);

            // Swap players!
            darkGame.isLightTurn = false;
            darkGame.isFinished = lightGame.isFinished;
            darkGame.rollValue = lightGame.rollValue;
            darkGame.dark.score = lightGame.light.score;
            darkGame.dark.pieces = lightGame.light.pieces;
            darkGame.light.score = lightGame.dark.score;
            darkGame.light.pieces = lightGame.dark.pieces;

            FastSimpleBoard lightGameBoard = lightGame.board;
            FastSimpleBoard darkGameBoard = darkGame.board;
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    int fromIndex = lightGameBoard.calcTileIndex(x, y);
                    int toIndex = darkGameBoard.calcTileIndex(width - x - 1, y);
                    darkGameBoard.set(toIndex, -1 * lightGameBoard.get(fromIndex));
                }
            }
            int keyDark = encoding.encode(darkGame);

            float lightScore, darkScore;
            try {
                lightScore = states.getAndUnwrapFloat(keyLight);
            } catch (RuntimeException e) {
                System.out.println(lightGame);
                throw e;
            }
            try {
                darkScore = states.getAndUnwrapFloat(keyDark);
            } catch (RuntimeException e) {
                System.out.println(darkGame);
                throw e;
            }

            double diff = lightScore + darkScore;
            if (diff > maxDifference.get()) {
                maxDifference.set(diff);
            }
            if (diff < minDifference.get()) {
                minDifference.set(diff);
            }
        });
        System.out.println(minDifference + ", " + maxDifference);*/
    }
}
