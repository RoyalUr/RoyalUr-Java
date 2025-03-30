package net.royalur.cli;

/**
 * An error that occurs when another argument is expected, but is not provided.
 */
public class CLINoRemainingArgumentsException extends CLIBadCommandException {

    public CLINoRemainingArgumentsException() {
        super("No arguments remaining");
    }
}
