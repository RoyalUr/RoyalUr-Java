package net.royalur.cli;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

/**
 * Parses CLI arguments.
 */
public class CLI {

    private final String[] positionalArgs;
    private int positionalArgsIndex = 0;

    private final Map<String, String> remainingKeywordArgs;

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

    public boolean hasNext() {
        return positionalArgsIndex < positionalArgs.length;
    }

    public String next() {
        if (positionalArgsIndex >= positionalArgs.length)
            throw new IllegalStateException("No positional args remaining");

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

    public @Nullable String readKeywordOrNull(String keyword) {
        return remainingKeywordArgs.remove(keyword);
    }

    public String readKeyword(String keyword, String defaultValue) {
        String value = readKeywordOrNull(keyword);
        return (value != null ? value : defaultValue);
    }

    public <T> T readKeywordMap(String keyword, Map<String, T> map, T defaultValue) {
        String valueKey = readKeywordOrNull(keyword);
        if (valueKey == null)
            return defaultValue;

        T value = map.get(valueKey);
        if (value == null)
            throw new CLIException("Unknown " + keyword + " value: " + valueKey);

        return value;
    }

    public double readKeywordDouble(String keyword, double defaultValue) {
        String value = readKeywordOrNull(keyword);
        return (value != null ? Double.parseDouble(value) : defaultValue);
    }

    public File readKeywordFile(String keyword, File defaultValue) {
        String value = readKeywordOrNull(keyword);
        return (value != null ? new File(value) : defaultValue);
    }

    public File readKeywordExistingFile(String keyword, File defaultValue) {
        String value = readKeywordOrNull(keyword);
        return (value != null ? parseExistingFile(value) : defaultValue);
    }

    public File readKeywordExistingDirectory(String keyword, File defaultValue) {
        String value = readKeywordOrNull(keyword);
        return (value != null ? parseExistingDirectory(value) : defaultValue);
    }

    public void clear() {
        positionalArgsIndex = positionalArgs.length;
        remainingKeywordArgs.clear();
    }

    public void expectEmpty() {
        if (positionalArgsIndex < positionalArgs.length) {
            throw new CLIException(
                    "Unrecognised positional args: "
                    + List.of(Arrays.copyOf(positionalArgs, positionalArgsIndex))
            );
        }
        if (!remainingKeywordArgs.isEmpty()) {
            throw new CLIException(
                    "Unrecognised keyword args: "
                    + List.copyOf(remainingKeywordArgs.keySet())
            );
        }
    }

    private static File parseExistingFile(String filename) {
        File file = new File(filename);
        if (!file.exists())
            throw new CLIException("File does not exist: " + filename);
        if (!file.isFile())
            throw new CLIException("Not a file: " + filename);

        return file;
    }

    private static File parseExistingDirectory(String filename) {
        File file = new File(filename);
        if (!file.exists())
            throw new CLIException("File does not exist: " + filename);
        if (!file.isDirectory())
            throw new CLIException("Not a directory: " + filename);

        return file;
    }
}
