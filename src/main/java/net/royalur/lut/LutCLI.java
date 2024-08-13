package net.royalur.lut;

import net.royalur.model.GameSettings;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleFlags;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;

public class LutCLI {

    public static final DecimalFormat MS_DURATION = new DecimalFormat("#,###");

    private LutCLI() {}

    public static void generateDataForShapeVisualisation(Lut lut, PrintStream out) {
        GameSettings settings = lut.getGameSettings();

        int maxAdvancementPerPiece = settings.getPaths().getLightWithStartEnd().size() - 1;
        int maxAdvancement = maxAdvancementPerPiece * settings.getStartingPieceCount();
        double[][] sums = new double[maxAdvancement + 1][maxAdvancement + 1];
        int[][] counts = new int[maxAdvancement + 1][maxAdvancement + 1];

        FastSimpleFlags flags = new FastSimpleFlags(settings);
        flags.loopLightGameStates(game -> {
            double winPercent = lut.getLightWinPercent(game);

            int lightAdvancement = game.light.score * maxAdvancementPerPiece;
            int darkAdvancement = game.dark.score * maxAdvancementPerPiece;
            for (int piece : game.board.pieces) {
                if (piece > 0) {
                    lightAdvancement += piece;
                } else if (piece < 0) {
                    darkAdvancement -= piece;
                }
            }

            // Add as light.
            sums[lightAdvancement][darkAdvancement] += winPercent;
            counts[lightAdvancement][darkAdvancement] += 1;

            // Add as dark.
            sums[darkAdvancement][lightAdvancement] += 100 - winPercent;
            counts[darkAdvancement][lightAdvancement] += 1;
        });

        for (int darkAdvancement = 0; darkAdvancement <= maxAdvancement; ++darkAdvancement) {
            StringBuilder row = new StringBuilder();
            for (int lightAdvancement = 0; lightAdvancement <= maxAdvancement; ++lightAdvancement) {
                if (lightAdvancement > 0) {
                    row.append(",");
                }

                int count = counts[lightAdvancement][darkAdvancement];
                double sum = sums[lightAdvancement][darkAdvancement];
                if (count > 0) {
                    row.append(String.format("%.1f", sum / count));
                }
            }
            if (darkAdvancement < maxAdvancement) {
                row.append(",");
            }
            out.println(row);
        }
    }

    public static void trainLut(
            GameSettings settings,
            @Nullable Lut lut,
            File checkpointFile,
            File outputFile
    ) throws IOException {
        GameStateEncoding encoding = new SimpleGameStateEncoding(settings);
        JsonNotation jsonNotation = new JsonNotation();
        LutTrainer trainer = new LutTrainer(settings, encoding, jsonNotation);

        if (lut == null) {
            long populateStart = System.nanoTime();
            lut = trainer.populateNewLut();
            double populateDurationMs = (System.nanoTime() - populateStart) / 1e6;
            System.out.println("Populate took " + MS_DURATION.format(populateDurationMs) + " ms");
        }

        lut = trainer.train(lut, checkpointFile, 0.0001f);

        long start = System.nanoTime();
        lut.write(jsonNotation, outputFile);
        double durationMs = (System.nanoTime() - start) / 1e6;
        System.out.println("Write took " + MS_DURATION.format(durationMs) + " ms");
    }

    public static void main(String[] args) throws IOException {

        File inputFile = new File("./models/finkel.rgu");
        File outputFile = new File("./models/finkel_round_two.rgu");
        File checkpointFile = new File("./finkel_checkpoint.rgu");

        Lut lut = null;
//        long readStart = System.nanoTime();
//        Lut lut = Lut.read(inputFile);
//        double readDurationMs = (System.nanoTime() - readStart) / 1e6;
//        System.out.println("Read took " + MS_DURATION.format(readDurationMs) + " ms");

//        generateDataForShapeVisualisation(lut);

        trainLut(GameSettings.FINKEL, lut, checkpointFile, outputFile);
    }
}
