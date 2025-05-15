package net.royalur.lut.cli;

import com.fasterxml.jackson.databind.JsonNode;
import net.royalur.cli.*;
import net.royalur.lut.Lut;
import net.royalur.lut.LutMetadata;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;

public class LutReadCommand extends CLICommand {

    public static final String NAME = "read";
    public static final String DESC = "Read metadata about an existing solved game lut";

    public LutReadCommand(CLICommand parent) {
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

            Map<String, JsonNode> otherMetadata = metadata.getAdditionalMetadata();
            if (!otherMetadata.isEmpty()) {
                System.out.println("Other Metadata =");
                for (Map.Entry<String, JsonNode> entry : otherMetadata.entrySet()) {
                    System.out.println("* " + entry.getKey() + ": " + entry.getValue());
                }
            }
        };
    }
}
