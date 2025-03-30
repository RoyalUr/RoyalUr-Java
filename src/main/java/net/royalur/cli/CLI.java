package net.royalur.cli;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Consumer;

/**
 * Parses CLI arguments.
 */
public class CLI {

    private final String[] positionalArgs;
    private int positionalArgsIndex = 0;

    private final Map<String, String> remainingKeywordArgs;
    private @Nullable Consumer<PrintStream> help;

    public CLI(String[] positionalArgs, Map<String, String> keywordArgs) {
        this.positionalArgs = positionalArgs;
        this.remainingKeywordArgs = keywordArgs;
    }

    public static CLI parse(String[] args) {
        List<String> positionalArgs = new ArrayList<>();
        Map<String, String> keywordArgs = new HashMap<>();

        for (String arg : args) {
            if (arg.startsWith("--")) {
                String key;
                String value;
                int eqIndex = arg.indexOf("=");
                if (eqIndex >= 0) {
                    // Handle --keyword=value.
                    key = arg.substring(2, eqIndex);
                    value = arg.substring(eqIndex + 1);
                } else {
                    // Handle --keyword.
                    key = arg.substring(2);
                    value = "";
                }
                keywordArgs.put(key, value);
            } else {
                positionalArgs.add(arg);
            }
        }
        return new CLI(positionalArgs.toArray(new String[0]), keywordArgs);
    }

    /**
     * As sub-commands are routed, they can update the help that is displayed
     * when an error occurs.
     */
    public void setHelp(@Nonnull Consumer<PrintStream> help) {
        this.help = help;
    }

    public void printHelp(PrintStream out) {
        if (help != null) {
            help.accept(out);
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
                    "Value of --" + keyword + " is empty, expected --" + keyword + "=value"
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
}
