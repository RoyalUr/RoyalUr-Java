package net.royalur.lut.cli;

import net.royalur.agent.LutAgent;
import net.royalur.cli.*;
import net.royalur.lut.Lut;
import net.royalur.lut.LutMetadata;
import net.royalur.model.GameSettings;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.rules.simple.fast.FastSimpleGame;
import net.royalur.rules.simple.fast.FastSimpleMoveList;
import net.royalur.stats.Histogram;
import net.royalur.stats.StatGatherer;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LutMoveStatsCommand extends CLICommand {

    public static final String NAME = "move_stats";
    public static final String DESC = "Calculate move statistics about an existing lut";

    public LutMoveStatsCommand(CLICommand parent) {
        super(parent, NAME, DESC);
        addRequiredArg(
                "<file>", CLIArgumentType.FILE,
                "LUT file"
        );
    }

    @Override
    public @Nullable CLIHandler handle(CLI cli) {
        if (!cli.hasNext())
            return null;

        File file = cli.nextExistingFile();
        return () -> {
            Lut lut = Lut.read(file);
            LutMetadata metadata = lut.getMetadata();

            String cliSettingsName = CLIConstants.getCLINameOrNull(metadata.getGameSettings());
            if (cliSettingsName != null) {
                System.out.println("Settings = " + cliSettingsName);
            } else {
                System.out.println("Settings = Custom");
                System.out.println("    " + metadata.getGameSettings());
            }
            System.out.println("Value Storage Type = " + metadata.getValueType().getTextID());
            System.out.println("Entry Count = " + lut.getEntryCount());
            System.out.println();

            processLutMoveStats(lut);
        };
    }

    private static void processLutMoveStats(Lut lut) throws IOException {
        LutAgent agent = new LutAgent(lut);
        GameSettings settings = lut.getGameSettings();

        int lightStateCount = lut.getEntryCount();
        int possibleRollCount = settings.getDice().createDice().getPossibleRollCount();

        FastSimpleFlags flags = new FastSimpleFlags(settings);
        FastSimpleGame tempGame = new FastSimpleGame(settings);
        FastSimpleGame tempGame2 = new FastSimpleGame(settings);
        FastSimpleMoveList tempMoveList = new FastSimpleMoveList();

        System.out.println("Gathering statistics...");
        AtomicInteger equalCount = new AtomicInteger(0);
        AtomicInteger states = new AtomicInteger(0);

        StatGatherer differences = new StatGatherer();
        Histogram differencesHistogram = new Histogram(-14, 2, 16);
        double histogramMax = 100;
        double histogramMin = Math.pow(10, -14);

        FastSimpleGame[] smallestDiffStates = new FastSimpleGame[10];
        double[] smallestDiffBestWP = new double[smallestDiffStates.length];
        int[] smallestDiffBestMove = new int[smallestDiffStates.length];
        int[] smallestDiffSecondBestMove = new int[smallestDiffStates.length];
        for (int index = 0; index < smallestDiffStates.length; ++index) {
            smallestDiffStates[index] = new FastSimpleGame(settings);
        }
        AtomicInteger smallestDiffStateCount = new AtomicInteger(0);

        flags.loopLightGameStatesAndRolls((game, roll, neighbours) -> {
            int processed = states.incrementAndGet();
            if (processed % (1000000 * possibleRollCount) == 0) {
                System.out.println(
                        ".. " + (processed / (1000000 * possibleRollCount))
                                + " / " + (lightStateCount / 1000000)
                );
            }
            if (neighbours.size() <= 1)
                return;

            double bestWP = -1;
            double secondBestWP = -1;
            for (FastSimpleGame neighbour : neighbours) {
                double wp = lut.getLightWinPercent(neighbour, tempGame);
                if (wp > bestWP) {
                    secondBestWP = bestWP;
                    bestWP = wp;
                } else if (wp > secondBestWP) {
                    secondBestWP = wp;
                }
            }
            if (secondBestWP >= 0 && bestWP >= 0) {
                double diff = bestWP - secondBestWP;

                // Keep track of the smallest difference states.
                double currentMin = differences.min();
                if (diff < currentMin) {
                    smallestDiffStateCount.set(0);
                }
                if (diff <= currentMin) {
                    int smallestDiffIndex = smallestDiffStateCount.getAndIncrement();
                    if (smallestDiffIndex < smallestDiffStates.length) {
                        FastSimpleGame state = smallestDiffStates[smallestDiffIndex];
                        state.copyFrom(game);
                        state.applyRoll(roll, tempMoveList);

                        boolean seenBest = false;
                        smallestDiffBestWP[smallestDiffIndex] = bestWP;
                        smallestDiffBestMove[smallestDiffIndex] = -2;
                        smallestDiffSecondBestMove[smallestDiffIndex] = -2;
                        for (int moveIndex = 0; moveIndex < tempMoveList.moveCount; ++moveIndex) {
                            int pathIndex = tempMoveList.moves[moveIndex];
                            tempGame2.copyFrom(state);
                            tempGame2.applyMove(pathIndex);
                            double wp = lut.getLightWinPercent(tempGame2, tempGame);
                            if (wp == bestWP) {
                                // Handle the case where the diff is zero.
                                if (!seenBest) {
                                    smallestDiffBestMove[smallestDiffIndex] = pathIndex;
                                    seenBest = true;
                                } else {
                                    smallestDiffSecondBestMove[smallestDiffIndex] = pathIndex;
                                }
                            } else if (wp == secondBestWP) {
                                smallestDiffSecondBestMove[smallestDiffIndex] = pathIndex;
                            }
                        }
                    }
                }

                // Keep track of difference statistics.
                differences.add(diff);
                double log10Diff = Math.log10(Math.max(histogramMin, Math.min(histogramMax, diff)));
                differencesHistogram.add(log10Diff);
            }
        });
        System.out.println("Done");
        System.out.println();
        System.out.printf("Found %d state/roll pairs where the best move is ambiguous%n", equalCount.get());
        System.out.println("* Difference between moves in a pair:");
        System.out.printf(" - Min = %g%n", differences.min());
        System.out.printf(" - Max = %g%n", differences.max());
        System.out.printf(" - Mean = %g%n", differences.mean());
        System.out.printf(" - Std. Dev. = %g%n", differences.stdDev());
        System.out.println();
        System.out.println("Histogram of differences: (log10 bins)");
        System.out.println(differencesHistogram.toLog10String());

        // We don't really handle the zero case very well, so skip it.
        if (differences.min() > 0) {
            System.out.println();
            System.out.printf(
                    "Found %d state/roll pairs at the minimum difference of %g%n",
                    smallestDiffStateCount.get(), differences.min()
            );
            for (int index = 0; index < smallestDiffStateCount.get(); ++index) {
                FastSimpleGame state = smallestDiffStates[index];
                System.out.printf(
                        "* State = %s%nTop move = %s (%g%%)%n2nd move = %s (-%g%%)%n",
                        state.toString(),
                        FastSimpleMoveList.moveToString(state, smallestDiffBestMove[index]),
                        smallestDiffBestWP[index],
                        FastSimpleMoveList.moveToString(state, smallestDiffSecondBestMove[index]),
                        differences.min()
                );
            }
        }
    }
}
