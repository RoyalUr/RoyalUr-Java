package net.royalur;

import net.royalur.cli.*;
import net.royalur.lut.cli.LutCommand;
import net.royalur.stats.cli.StatsCommand;

import java.io.PrintStream;
import java.util.logging.Logger;

public class Main extends CLIRoutingCommand {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private Main() {
        super(null, "", "RoyalUr-Java CLI");
        addSubCommand(new LutCommand(this));
        addSubCommand(new StatsCommand(this));
    }

    private void printHelp(CLI cli, PrintStream out) {
        cli.printHelp(out);
        out.println("Global Options:");
        out.println("  --help    Show this help message");
        out.println();
    }

    public void handle(String[] args) throws Exception {
        CLI cli = null;
        try {
            cli = CLI.parse(args);
            boolean showHelp = cli.readKeywordIsPresent("--help");

            CLIHandler handler = null;
            if (showHelp) {
                // Consume any CLI errors, and just print the help that the routing sets.
                try {
                    handler = handle(cli);
                } catch (CLIException e) { /* Ignored */ }
            } else {
                handler = handle(cli);
            }

            // If --help is present, print the help.
            // If the routing returns null, we also print help.
            if (showHelp || handler == null) {
                printHelp(cli, System.out);
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
                System.err.println();
                printHelp(cli, System.err);
            }
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.handle(args);
    }
}
