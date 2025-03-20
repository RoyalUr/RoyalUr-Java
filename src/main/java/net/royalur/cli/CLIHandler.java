package net.royalur.cli;

import java.io.IOException;

@FunctionalInterface
public interface CLIHandler {
    void handle() throws IOException;
}

