package net.royalur.cli;

/**
 * An error with how the user tried to use a CLI command.
 * Help will be printed after this error is printed.
 */
public class CLIBadCommandException extends CLIException {

    public CLIBadCommandException(String message) {
        super(message);
    }
}
