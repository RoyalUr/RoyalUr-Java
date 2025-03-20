package net.royalur;

import net.royalur.lut.LutCLI;
import net.royalur.cli.CLI;
import net.royalur.cli.CLIException;
import net.royalur.cli.CLIHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private Main() {}

    public static void printHelp() {
        System.err.println("Usage:");
        System.err.println("* lut - Commands related to the solved maps");
        System.err.println("* stats - Commands to calculate game statistics");
    }

    public static void main(String[] args) throws IOException {
        try {
            CLI cli = CLI.parse(args);
            if (!cli.hasNext()) {
                printHelp();
                System.exit(1);
                return;
            }

            String command = cli.next();
            CLIHandler handler;

            if (command.equalsIgnoreCase("lut")) {
                handler = LutCLI.routeRequest(cli);

            } else if (command.equalsIgnoreCase("stats")) {
                handler = RGUStatistics.routeRequest(cli);

            } else {
                cli.clear();
                handler = () -> {
                    System.err.println("Unknown command: " + command);
                    printHelp();
                    System.exit(1);
                };
            }

            cli.expectEmpty();
            handler.handle();

        } catch (CLIException e) {
            System.err.println(e.getMessage());
        }
    }
}
