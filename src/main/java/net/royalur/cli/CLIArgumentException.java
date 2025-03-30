package net.royalur.cli;

/**
 * An error with the arguments that a user provided.
 */
public class CLIArgumentException extends CLIException {

    public CLIArgumentException(String message) {
        super(message);
    }
}
