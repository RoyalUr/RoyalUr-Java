package net.royalur.stats.cli;

import net.royalur.cli.*;

/**
 * This file intends to hold tests that can be performed
 * to different sets of game rules and paths to compare them.
 */
public class StatsCommand extends CLIRoutingCommand {

    public static final String NAME = "stats";
    public static final String DESC = "Commands to calculate game statistics";

    public StatsCommand(CLICommand parent) {
        super(parent, NAME, DESC);
        addSubCommand(new StatsCountCommand(this));
    }
}
