package net.royalur.cli;

import javax.annotation.Nullable;

public record CLIArgumentType(String name) {
    public static final CLIArgumentType NONE = new CLIArgumentType("None");
    public static final CLIArgumentType UNKNOWN = new CLIArgumentType("Unknown");
    public static final CLIArgumentType INTEGER = new CLIArgumentType("Integer");
    public static final CLIArgumentType NUMBER = new CLIArgumentType("Number");
    public static final CLIArgumentType FILE = new CLIArgumentType("File");
    public static final CLIArgumentType TEXT = new CLIArgumentType("Text");
    public static final CLIArgumentType SUBCOMMAND = new CLIArgumentType("Sub-Command");
    public static final CLIArgumentType GAME_SETTINGS = new CLIArgumentType("Game Settings");
    public static final CLIArgumentType VALUE_TYPE = new CLIArgumentType("Value Type");

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (!getClass().equals(obj.getClass()))
            return false;

        CLIArgumentType other = (CLIArgumentType) obj;
        return name.equals(other.name);
    }
}
