package net.royalur.lut;

import com.fasterxml.jackson.databind.JsonNode;
import net.royalur.cli.*;
import net.royalur.lut.buffer.ValueType;
import net.royalur.model.GameSettings;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.util.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Map;

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

    public static void printTrainHelp(PrintStream out) {
        out.println("LUT Train Usage:");
        out.println("* lut train - Generate a new solved game lookup-table, or refine an existing one");
        out.println("  --output (required):");
        out.println("      Path to final output file");
        out.println("  --settings:");
        out.println("      Game settings (default finkel)");
        out.println("  --checkpoint:");
        out.println("      Path to checkpoint file (default <output>.checkpoint.rgu)");
        out.println("  --input:");
        out.println("      Path to input file to train from");
        out.println("      (default read from checkpoint, or else start from scratch)");
        out.println("  --precision:");
        out.println("      The stopping precision to train to (default 0.0001)");
        out.println("  --training-value-type:");
        out.println("      Value type to use while training (default f32)");
        out.println("  --output-value-type:");
        out.println("      Value type to save the final output using (default percent16)");
        out.println("  --author:");
        out.println("      Include metadata about who trained the model");
    }

    public static @Nullable CLIHandler handleTrain(CLI cli) {
        cli.setHelp(LutCLI::printTrainHelp);
        GameSettings settings = cli.readKeywordMap(
                "settings", CLIConstants.SETTINGS_BY_CLI_NAME, GameSettings.FINKEL
        );
        String settingsCLIName = CLIConstants.getCLIName(settings);
        File outputFile = cli.readKeywordFile(
                "output", null
        );
        if (outputFile == null)
            throw new CLIBadCommandException("Missing --output");

        File inputFile = cli.readKeywordFile(
                "input", null
        );
        File checkpointFile = cli.readKeywordFile(
                "checkpoint", FileUtils.replaceExtension(outputFile, ".checkpoint.rgu")
        );
        ValueType trainingValueType = cli.readKeywordMap(
                "training-value-type", CLIConstants.VALUE_TYPE_BY_CLI_NAME, ValueType.FLOAT32
        );
        ValueType outputValueType = cli.readKeywordMap(
                "output-value-type", CLIConstants.VALUE_TYPE_BY_CLI_NAME, ValueType.PERCENT16
        );
        double precision = cli.readKeywordDouble("precision", 0.0001d);
        String author = cli.readKeywordOrNull("author");

        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists() || !outputDir.isDirectory())
            throw new CLIArgumentException("Output directory does not exist: " + outputDir);

        File readFromFile;
        if (inputFile == null && checkpointFile.exists()) {
            readFromFile = checkpointFile;
        } else {
            readFromFile = inputFile;
        }

        return () -> {
            GameStateEncoding encoding = new SimpleGameStateEncoding(settings);
            JsonNotation jsonNotation = new JsonNotation();
            LutTrainer trainer = new LutTrainer(settings, encoding, trainingValueType, jsonNotation);

            // Read a checkpoint to train from.
            Lut lut = null;
            GameSettings trainSettings = settings;
            if (readFromFile != null) {
                lut = Lut.read(readFromFile);
                trainSettings = lut.getMetadata().getGameSettings();
            } else {
                System.out.println("Populating new map...");
                long populateStart = System.nanoTime();
                lut = trainer.populateNewLut();
                double populateDurationMs = (System.nanoTime() - populateStart) / 1e6;
                System.out.println("Populating new map took " + MS_DURATION.format(populateDurationMs) + " ms");
            }

            // Set metadata.
            if (author != null) {
                lut.getMetadata().addMetadata("author", author);
            }

            // Train!
            trainer.train(lut, checkpointFile, outputValueType, precision);
            Files.move(checkpointFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        };
    }

    public static void printReadHelp(PrintStream out) {
        out.println("LUT Read Usage:");
        out.println("* lut read [file] - Read metadata about an existing solved game lookup-table");
    }

    public static @Nullable CLIHandler handleRead(CLI cli) {
        cli.setHelp(LutCLI::printReadHelp);
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

            Map<String, JsonNode> otherMetadata = metadata.getAdditionalMetadata();
            if (!otherMetadata.isEmpty()) {
                System.out.println("Other Metadata =");
                for (Map.Entry<String, JsonNode> entry : otherMetadata.entrySet()) {
                    System.out.println("* " + entry.getKey() + ": " + entry.getValue());
                }
            }
        };
    }

    public static void printHelp(PrintStream out) {
        out.println("LUT Usage:");
        out.println("* lut read [file] - Read metadata about an existing solved game lookup-table");
        out.println("* lut train - Generate a new solved game lookup-table, or refine an existing one");
    }

    public static @Nullable CLIHandler routeCLIRequest(CLI cli) throws IOException {
        cli.setHelp(LutCLI::printHelp);
        if (!cli.hasNext())
            return null;

        String command = cli.next();
        if ("train".equalsIgnoreCase(command))
            return handleTrain(cli);
        if ("read".equalsIgnoreCase(command))
            return handleRead(cli);

        throw new CLIBadCommandException("Unknown lut command: " + command);
    }
}
