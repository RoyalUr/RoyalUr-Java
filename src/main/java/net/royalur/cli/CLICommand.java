package net.royalur.cli;

import javax.annotation.Nullable;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CLICommand {

    public final @Nullable CLICommand parent;
    public final String name;
    public final String description;
    public final List<CLIArgument> args;

    public CLICommand(@Nullable CLICommand parent, String name, String description) {
        this.parent = parent;
        this.name = name;
        this.description = description;
        this.args = new ArrayList<>();
    }

    public void addRequiredArg(String name, CLIArgumentType type, String... descriptionLines) {
        this.args.add(new CLIArgument(name, type, descriptionLines, true));
    }

    public void addOptionalArg(String name, CLIArgumentType type, String... descriptionLines) {
        this.args.add(new CLIArgument(name, type, descriptionLines, false));
    }

    public int countPositionalArgs() {
        int count = 0;
        for (CLIArgument arg : args) {
            if (arg.isPositionalArg()) {
                count += 1;
            }
        }
        return count;
    }

    public int countKeywordArgs() {
        int count = 0;
        for (CLIArgument arg : args) {
            if (arg.isKeywordArg()) {
                count += 1;
            }
        }
        return count;
    }

    public CLICommand[] getCommandStack() {
        List<CLICommand> commands = new ArrayList<>();
        CLICommand current = this;
        while (current != null) {
            commands.add(current);
            current = current.parent;
        }
        Collections.reverse(commands);
        return commands.toArray(new CLICommand[commands.size()]);
    }

    protected void appendCommandNoArgs(StringBuilder builder) {
        boolean first = true;
        for (CLICommand command : getCommandStack()) {
            if (command.name.isEmpty())
                continue;

            if (!first) {
                builder.append(" ");
            } else {
                first = false;
            }
            builder.append(command.name);
        }
    }

    protected void appendPositionalArgs(StringBuilder builder, boolean leadingSpace) {
        boolean appendSpace = leadingSpace;
        for (CLIArgument arg : args) {
            if (!arg.isPositionalArg())
                continue;

            if (appendSpace) {
                builder.append(" ");
            } else {
                appendSpace = true;
            }
            builder.append(arg.name());
        }
    }

    protected String getCommandForHelp(boolean includeArgs) {
        StringBuilder builder = new StringBuilder();
        appendCommandNoArgs(builder);
        if (includeArgs) {
            appendPositionalArgs(builder, !builder.isEmpty());
        }
        return builder.toString();
    }

    public String getCommandForHelp() {
        return getCommandForHelp(true);
    }

    public String getCommandNoArgs() {
        StringBuilder builder = new StringBuilder();
        appendCommandNoArgs(builder);
        return builder.toString();
    }

    public String getCommand() {
        StringBuilder builder = new StringBuilder();
        appendCommandNoArgs(builder);
        appendPositionalArgs(builder, !builder.isEmpty());

        boolean anyOptionalKeywordArgs = false;
        for (CLIArgument arg : args) {
            if (!arg.isKeywordArg())
                continue;

            if (arg.required()) {
                if (!builder.isEmpty()) {
                    builder.append(" ");
                }
                builder.append(arg.getNameAndType());
            } else {
                anyOptionalKeywordArgs = true;
            }
        }
        if (anyOptionalKeywordArgs) {
            if (!builder.isEmpty()) {
                builder.append(" ");
            }
            builder.append("[--<key>=<value>...]");
        }
        return builder.toString();
    }

    public void printHelp(PrintStream out) {
        out.println(getCommandNoArgs());
        out.println("  " + description);

        out.println();
        out.println("Usage:");
        out.println("  " + getCommand());

        if (!args.isEmpty()) {
            out.println();
            out.println("Options:");
            for (CLIArgument arg : args) {
                String[] argDesc = arg.descriptionLines();
                if  (argDesc.length == 0) {
                    out.println("  " + arg);
                } else {
                    out.println("  " + CLI.rightPadArgument(arg.toString()) + " " + argDesc[0]);
                }
                if (argDesc.length > 1) {
                    String spaces = "  " + CLI.rightPadArgument("") + " ";
                    for (int index = 1; index < argDesc.length; index++) {
                        String line = argDesc[index];
                        out.println(spaces + line);
                    }
                }
            }
        }
        out.println();
    }

    public abstract @Nullable CLIHandler handle(CLI cli) throws Exception;
}
