package net.royalur.cli;

public abstract class CLIException extends RuntimeException {

    public CLIException(String message) {
        super(message);
    }
}
