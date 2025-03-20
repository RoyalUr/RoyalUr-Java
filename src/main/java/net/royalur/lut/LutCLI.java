package net.royalur.lut;

import com.fasterxml.jackson.databind.JsonNode;
import net.royalur.cli.CLIConstants;
import net.royalur.lut.buffer.ValueType;
import net.royalur.model.GameSettings;
import net.royalur.notation.JsonNotation;
import net.royalur.rules.simple.fast.FastSimpleFlags;
import net.royalur.cli.CLI;
import net.royalur.cli.CLIHandler;
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

    public static void trainLut(
            GameSettings settings,
            @Nullable Lut lut,
            File checkpointFile,
            File outputFile,
            double precision,
            ValueType trainingValueType,
            ValueType outputValueType
    ) throws IOException {
        GameStateEncoding encoding = new SimpleGameStateEncoding(settings);
        JsonNotation jsonNotation = new JsonNotation();
        LutTrainer trainer = new LutTrainer(settings, encoding, trainingValueType, jsonNotation);

        if (lut == null) {
            System.out.println("Populating new map...");
            long populateStart = System.nanoTime();
            lut = trainer.populateNewLut();
            double populateDurationMs = (System.nanoTime() - populateStart) / 1e6;
            System.out.println("Populate took " + MS_DURATION.format(populateDurationMs) + " ms");
        }

        trainer.train(lut, checkpointFile, outputValueType, precision);
        Files.move(checkpointFile.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public static CLIHandler handleTrain(CLI cli) {
        GameSettings settings = cli.readKeywordMap(
                "settings", CLIConstants.SETTINGS_BY_CLI_NAME, GameSettings.FINKEL
        );
        String settingsCLIName = CLIConstants.getCLIName(settings);
        File outputFile = cli.readKeywordFile(
                "output", new File("./models/" + settingsCLIName + ".rgu")
        );
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
        if (!outputDir.exists() || !outputDir.isDirectory()) {
            cli.clear();
            return () -> {
                System.err.println("Output directory does not exist: " + outputDir);
                System.exit(1);
            };
        }

        File readFromFile;
        if (inputFile == null && checkpointFile.exists()) {
            readFromFile = checkpointFile;
        } else {
            readFromFile = inputFile;
        }

        return () -> {
            // Read a checkpoint to train from.
            Lut lut = null;
            GameSettings trainSettings = settings;
            if (readFromFile != null) {
                lut = Lut.read(readFromFile);
                trainSettings = lut.getMetadata().getGameSettings();
            }

            // Set metadata.
            if (author != null) {
                lut.getMetadata().addMetadata("author", author);
            }

            // Train!
            trainLut(
                    settings, lut, checkpointFile, outputFile,
                    precision, trainingValueType, outputValueType
            );
        };
    }

    public static CLIHandler handleRead(CLI cli) {
        if (!cli.hasNext()) {
            cli.clear();
            return () -> {
                System.err.println("Missing the LUT file to load");
                printHelp();
                System.exit(1);
            };
        }

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

    public static void printHelp() {
        System.err.println("LUT Usage:");
        System.err.println("* lut read [file] - Read metadata from a solved map");
        System.err.println("* lut train - Train a solved map");
        System.err.println("  --settings:");
        System.err.println("      Game settings (default finkel)");
        System.err.println("  --output:");
        System.err.println("      Path to final output file (default models/<settings>.rgu)");
        System.err.println("  --checkpoint:");
        System.err.println("      Path to checkpoint file (default <output>.checkpoint.rgu)");
        System.err.println("  --input:");
        System.err.println("      Path to input file to train from");
        System.err.println("      (default read from checkpoint, or else start from scratch)");
        System.err.println("  --precision:");
        System.err.println("      The stopping precision to train to (default 0.0001)");
        System.err.println("  --training-value-type:");
        System.err.println("      Value type to use while training (default f32)");
        System.err.println("  --output-value-type:");
        System.err.println("      Value type to save the final output using (default percent16)");
        System.err.println("  --author:");
        System.err.println("      Include metadata about who trained the model");
    }

    public static CLIHandler routeRequest(CLI cli) throws IOException {
        if (!cli.hasNext()) {
            cli.clear();
            return () -> {
                printHelp();
                System.exit(1);
            };
        }

        String command = cli.next();

        if ("train".equalsIgnoreCase(command)) {
            return handleTrain(cli);

        } else if ("read".equalsIgnoreCase(command)) {
            return handleRead(cli);
        }

        cli.clear();
        return () -> {
            System.err.println("Unknown lut command: " + command);
            printHelp();
            System.exit(1);
        };
    }
}
