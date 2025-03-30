package net.royalur;

import net.royalur.cli.*;
import net.royalur.lut.LutCLI;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private Main() {}

    public static void printHelp(PrintStream out) {
        out.println("RoyalUr-Java CLI Usage:");
        out.println("* lut - Commands for generating and managing solved game lookup tables (luts)");
        out.println("    lut train: Generate a new solved game lookup-table, or refine an existing one");
        out.println("    lut read [file]: Read metadata about an existing solved game lookup-table");
        out.println("* stats - Commands to calculate game statistics");
        out.println("    stats count [rulesets]: Count the number of states in rule sets");
    }

    private static @Nullable CLIHandler routeCLIRequest(CLI cli) throws IOException {
        cli.setHelp(Main::printHelp);
        if (!cli.hasNext())
            return null;

        String command = cli.next();
        CLIHandler handler;

        if (command.equalsIgnoreCase("lut"))
            return LutCLI.routeCLIRequest(cli);
        if (command.equalsIgnoreCase("stats"))
            return StatsCLI.routeCLIRequest(cli);

        throw new CLIBadCommandException("Unknown command: " + command);
    }

    public static void main(String[] args) throws IOException {
        CLI cli = null;
        try {
            cli = CLI.parse(args);
            boolean showHelp = cli.readKeywordIsPresent("help");

            CLIHandler handler = null;
            if (showHelp) {
                // Consume any CLI errors, and just print the help that the routing sets.
                try {
                    handler = routeCLIRequest(cli);
                } catch (CLIException e) { /* Ignored */ }
            } else {
                handler = routeCLIRequest(cli);
            }

            // If --help is present, print the help.
            // If the routing returns null, we also print help.
            if (showHelp || handler == null) {
                cli.printHelp(System.out);
                return;
            }

            cli.expectEmpty();
            handler.handle();

        } catch (CLIArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);

        } catch (CLIBadCommandException e) {
            System.err.println(e.getMessage());
            if (cli != null) {
                cli.printHelp(System.err);
            }
            System.exit(1);
        }
    }
}
