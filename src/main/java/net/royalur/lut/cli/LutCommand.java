package net.royalur.lut.cli;

import net.royalur.cli.*;

public class LutCommand extends CLIRoutingCommand {
    public static final String NAME = "lut";
    public static final String DESC = "Work with solved game lookup tables (luts)";

    public LutCommand(CLICommand parent) {
        super(parent, NAME, DESC);
        addSubCommand(new LutReadCommand(this));
        addSubCommand(new LutMoveStatsCommand(this));
        addSubCommand(new LutTrainCommand(this));
    }
}
