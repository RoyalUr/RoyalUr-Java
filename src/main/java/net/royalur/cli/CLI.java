package net.royalur.cli;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Parses CLI arguments.
 */
public class CLI {

    public static final DecimalFormat MS_DURATION = new DecimalFormat("#,###");
    public static final int MIN_COMMAND_WIDTH = 24;
    public static final int MIN_ARGUMENT_WIDTH = 34;

    private final String[] positionalArgs;
    private int positionalArgsIndex = 0;

    private final Map<String, String> remainingKeywordArgs;
    private @Nullable CLICommand currentCommand;

    public CLI(String[] positionalArgs, Map<String, String> keywordArgs) {
        this.positionalArgs = positionalArgs;
        this.remainingKeywordArgs = keywordArgs;
    }

    /**
     * Used for things like printing help, or for help in error reporting.
     */
    public void setCurrentCommand(@Nonnull CLICommand currentCommand) {
        this.currentCommand = currentCommand;
    }

    public static CLI parse(String[] args) {
        List<String> positionalArgs = new ArrayList<>();
        Map<String, String> keywordArgs = new HashMap<>();

        int index = 0;
        boolean endOfOptions = false;

        for (; index < args.length; index++) {
            String tok = args[index];

            // All arguments after -- should be treated as positional.
            if (!endOfOptions && "--".equals(tok)) {
                endOfOptions = true;
                continue;
            }

            // Long options (e.g., --key=value, or --key value).
            if (!endOfOptions && tok.startsWith("--") && tok.length() > 2) {
                int eq = tok.indexOf('=');

                // Inline "--key=value".
                if (eq >= 0) {
                    String key = tok.substring(0, eq);
                    String val = tok.substring(eq + 1);
                    keywordArgs.put(key, val);
                    continue;
                }

                // Separate "--key value" or bare flag "--key".
                String key = tok;
                String val = "";
                if (index + 1 < args.length && !args[index + 1].startsWith("-")) {
                    val = args[++index]; // consume the value
                }
                keywordArgs.put(key, val);
                continue;
            }

            // Short options (e.g., -a, -b, or -ab)
            if (!endOfOptions && tok.startsWith("-") && !tok.startsWith("--") && tok.length() > 1) {
                String flags = tok.substring(1);
                for (int j = 0; j < flags.length(); j++) {
                    String key = "-" + flags.charAt(j);
                    String val = "";
                    // last flag in bundle may take a value if next token isn't an option
                    if (j == flags.length() - 1 && index + 1 < args.length && !args[index + 1].startsWith("-")) {
                        val = args[++index];
                    }
                    keywordArgs.put(key, val);
                }
                continue;
            }

            // Everything else is positional.
            positionalArgs.add(tok);
        }
        return new CLI(positionalArgs.toArray(new String[0]), keywordArgs);
    }

    public void printHelp(PrintStream out) {
        if (currentCommand != null) {
            currentCommand.printHelp(out);
        }
    }

    public boolean hasNext() {
        return positionalArgsIndex < positionalArgs.length;
    }

    public String next() {
        if (positionalArgsIndex >= positionalArgs.length)
            throw new CLINoRemainingArgumentsException();

        return positionalArgs[positionalArgsIndex++];
    }

    public File nextFile() {
        return new File(next());
    }

    public File nextExistingFile() {
        return parseExistingFile(next());
    }

    public File nextExistingDirectory() {
        return parseExistingDirectory(next());
    }

    private void assertNotEmpty(String keyword, String value) {
        if (value.isEmpty()) {
            throw new CLIArgumentException(
                    "Value of " + keyword + " is empty, expected " + keyword + "=value"
            );
        }
    }

    public boolean readKeywordIsPresent(String keyword) {
        return remainingKeywordArgs.remove(keyword) != null;
    }

    public @Nullable String readKeywordOrNull(String keyword) {
        return remainingKeywordArgs.remove(keyword);
    }

    public @Nullable String readNonEmptyKeywordOrNull(String keyword) {
        String value = remainingKeywordArgs.remove(keyword);
        if (value != null) {
            assertNotEmpty(keyword, value);
        }
        return value;
    }

    public String readKeyword(String keyword, String defaultValue) {
        String value = readKeywordOrNull(keyword);
        return (value != null ? value : defaultValue);
    }

    public <T> T readKeywordMap(String keyword, Map<String, T> map, T defaultValue) {
        String valueKey = readNonEmptyKeywordOrNull(keyword);
        if (valueKey == null)
            return defaultValue;

        T value = map.get(valueKey);
        if (value == null)
            throw new CLIArgumentException("Unknown --" + keyword + " value: " + valueKey);

        return value;
    }

    public double readKeywordDouble(String keyword, double defaultValue) {
        String value = readNonEmptyKeywordOrNull(keyword);
        try {
            return (value != null ? Double.parseDouble(value) : defaultValue);
        } catch (NumberFormatException e) {
            throw new CLIArgumentException("Value of --" + keyword + " is not a valid number");
        }
    }

    public File readKeywordFile(String keyword, File defaultValue) {
        String value = readNonEmptyKeywordOrNull(keyword);
        return (value != null ? new File(value) : defaultValue);
    }

    public File readKeywordExistingFile(String keyword, File defaultValue) {
        String value = readNonEmptyKeywordOrNull(keyword);
        return (value != null ? parseExistingFile(value) : defaultValue);
    }

    public File readKeywordExistingDirectory(String keyword, File defaultValue) {
        String value = readNonEmptyKeywordOrNull(keyword);
        return (value != null ? parseExistingDirectory(value) : defaultValue);
    }

    public void clear() {
        positionalArgsIndex = positionalArgs.length;
        remainingKeywordArgs.clear();
    }

    public void expectEmpty() {
        if (positionalArgsIndex < positionalArgs.length) {
            throw new CLIBadCommandException(
                    "Unrecognised positional args: "
                    + List.of(Arrays.copyOf(positionalArgs, positionalArgsIndex))
            );
        }
        if (!remainingKeywordArgs.isEmpty()) {
            throw new CLIBadCommandException(
                    "Unrecognised keyword args: "
                    + List.copyOf(remainingKeywordArgs.keySet())
            );
        }
    }

    private static File parseExistingFile(String filename) {
        File file = new File(filename);
        if (!file.exists())
            throw new CLIArgumentException("File does not exist: " + filename);
        if (!file.isFile())
            throw new CLIArgumentException("Not a file: " + filename);

        return file;
    }

    private static File parseExistingDirectory(String filename) {
        File file = new File(filename);
        if (!file.exists())
            throw new CLIArgumentException("File does not exist: " + filename);
        if (!file.isDirectory())
            throw new CLIArgumentException("Not a directory: " + filename);

        return file;
    }

    public static String rightPad(String input, int minLength) {
        if (input.length() >= minLength)
            return input;

        StringBuilder builder = new StringBuilder(minLength);
        builder.append(input);
        while (builder.length() < minLength) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String rightPadCommand(String command) {
        return rightPad(command, MIN_COMMAND_WIDTH);
    }

    public static String rightPadArgument(String argument) {
        return rightPad(argument, MIN_ARGUMENT_WIDTH);
    }
}
