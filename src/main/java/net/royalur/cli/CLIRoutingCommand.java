package net.royalur.cli;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CLIRoutingCommand extends CLICommand {

    private final List<CLICommand> subCommands = new ArrayList<>();

    public CLIRoutingCommand(@Nullable CLICommand parent, String name, String description) {
        super(parent, name, description);
        addRequiredArg("<sub-command>", CLIArgumentType.SUBCOMMAND, "The sub-command to run");
    }

    public void addSubCommand(CLICommand subCommand) {
        subCommands.add(subCommand);
    }

    @Override
    public String getCommandForHelp() {
        // We don't want to include args in help for routing commands.
        return getCommandForHelp(false);
    }

    @Override
    public void printHelp(PrintStream out) {
        String command = getCommandNoArgs();
        if (!command.isEmpty()) {
            out.println(command);
            out.println("  " + description);
        } else {
            out.println(description);
        }

        out.println();
        out.println("Usage:");
        boolean first = true;
        for (CLICommand subCommand : subCommands) {
            if (!first && subCommand instanceof CLIRoutingCommand) {
                out.println();
            }
            first = false;

            String subCommandUsage = CLI.rightPadCommand(subCommand.getCommandForHelp());
            out.println("  " + subCommandUsage + "  " + subCommand.description);
            if (subCommand instanceof CLIRoutingCommand routingSubCommand) {
                for (CLICommand subSubCommand : routingSubCommand.subCommands) {
                    String subSubCommandUsage = CLI.rightPadCommand(subSubCommand.getCommandForHelp());
                    out.println("    " + subSubCommandUsage + "  " + subSubCommand.description);
                }
            }
        }
        if (first) {
            out.println("  No sub-commands found");
        } else {
            out.println();
        }
    }

    @Override
    public @Nullable CLIHandler handle(CLI cli) throws Exception {
        cli.setCurrentCommand(this);
        if (!cli.hasNext())
            return null;

        String subCommandName = cli.next();
        for (CLICommand subCommand : subCommands) {
            if (subCommand.name.equalsIgnoreCase(subCommandName)) {
                cli.setCurrentCommand(subCommand);
                return subCommand.handle(cli);
            }
        }

        String type = getCommandNoArgs();
        if (!type.isEmpty()) {
            type += " sub-command";
        } else {
            type = "command";
        }
        throw new CLIBadCommandException("Unknown " + type + ": " + subCommandName);
    }
}
