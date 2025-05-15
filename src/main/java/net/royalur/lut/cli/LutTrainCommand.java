package net.royalur.lut.cli;

import net.royalur.cli.*;
import net.royalur.lut.GameStateEncoding;
import net.royalur.lut.Lut;
import net.royalur.lut.LutTrainer;
import net.royalur.lut.SimpleGameStateEncoding;
import net.royalur.lut.buffer.ValueType;
import net.royalur.model.GameSettings;
import net.royalur.notation.JsonNotation;
import net.royalur.util.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class LutTrainCommand extends CLICommand {

    public static final String NAME = "train";
    public static final String DESC = "Generate a new solved game lookup-table, or refine an existing one";

    public LutTrainCommand(CLICommand parent) {
        super(parent, NAME, DESC);
        addRequiredArg(
                "--output", CLIArgumentType.FILE,
                "Path to final output file"
        );
        addOptionalArg(
                "--settings", CLIArgumentType.GAME_SETTINGS,
                "Game settings (default finkel)"
        );
        addOptionalArg(
                "--checkpoint", CLIArgumentType.FILE,
                "Path to checkpoint file (default <output>.checkpoint.rgu)"
        );
        addOptionalArg(
                "--input", CLIArgumentType.FILE,
                "Path to input file to train from",
                "(default read from checkpoint, or else start from scratch)"
        );
        addOptionalArg(
                "--precision", CLIArgumentType.NUMBER,
                "The stopping precision to train to (default 0.0001)"
        );
        addOptionalArg(
                "--training-value-type", CLIArgumentType.VALUE_TYPE,
                "Value type to use while training (default f32)"
        );
        addOptionalArg(
                "--output-value-type", CLIArgumentType.VALUE_TYPE,
                "Value type to save the final output using",
                "(default percent16)"
        );
        addOptionalArg(
                "--author", CLIArgumentType.TEXT,
                "Include metadata about who trained the model"
        );
    }

    @Override
    public @Nullable CLIHandler handle(CLI cli) {
        GameSettings settings = cli.readKeywordMap(
                "--settings", CLIConstants.SETTINGS_BY_CLI_NAME, GameSettings.FINKEL
        );
        String settingsCLIName = CLIConstants.getCLIName(settings);
        File outputFile = cli.readKeywordFile(
                "--output", null
        );
        if (outputFile == null)
            throw new CLIBadCommandException("Missing --output");

        File inputFile = cli.readKeywordFile(
                "--input", null
        );
        File checkpointFile = cli.readKeywordFile(
                "--checkpoint", FileUtils.replaceExtension(outputFile, ".checkpoint.rgu")
        );
        ValueType trainingValueType = cli.readKeywordMap(
                "--training-value-type", CLIConstants.VALUE_TYPE_BY_CLI_NAME, ValueType.FLOAT32
        );
        ValueType outputValueType = cli.readKeywordMap(
                "--output-value-type", CLIConstants.VALUE_TYPE_BY_CLI_NAME, ValueType.PERCENT16
        );
        double precision = cli.readKeywordDouble("--precision", 0.0001d);
        String author = cli.readKeywordOrNull("--author");

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
            Lut lut;
            if (readFromFile != null) {
                lut = Lut.read(readFromFile);
            } else {
                System.out.println("Populating new map...");
                long populateStart = System.nanoTime();
                lut = trainer.populateNewLut();
                double populateDurationMs = (System.nanoTime() - populateStart) / 1e6;
                System.out.println(
                        "Populating new map took "
                        + CLI.MS_DURATION.format(populateDurationMs) + " ms"
                );
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
}
