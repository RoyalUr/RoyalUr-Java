package net.royalur.model;

import javax.annotation.Nullable;

/**
 * Represents the players of a game.
 */
public enum PlayerType {

    /**
     * The light player. Following chess, the light player moves first.
     */
    LIGHT('L', "Light"),

    /**
     * The dark player. Following chess, the dark player moves second.
     */
    DARK('D', "Dark");

    /**
     * An English name for this player, capitalised.
     */
    private final String name;

    /**
     * A constant character representing the player.
     * This character will never change.
     */
    private final char character;

    /**
     * A constant character representing the player,
     * stored in a string.
     */
    private final String characterString;

    /**
     * Instantiates a type of player.
     * @param character A fixed character to represent this player.
     * @param name An English name for this player.
     */
    PlayerType(char character, String name) {
        this.character = character;
        this.characterString = Character.toString(character);
        this.name = name;
    }

    /**
     * Gets a constant character representing the player.
     * This character will never change.
     * @return A constant character representing the player.
     */
    public char getChar() {
        return character;
    }

    /**
     * Gets a constant character representing the player.
     * This character will never change.
     * @return A constant character representing the player.
     */
    public String getCharStr() {
        return characterString;
    }

    /**
     * Gets the name of this player type.
     * @return The name of this player type.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the PlayerType representing the other player.
     * @return The PlayerType representing the other player.
     */
    public PlayerType getOtherPlayer() {
        return switch (this) {
            case LIGHT -> DARK;
            case DARK -> LIGHT;
        };
    }

    /**
     * Converts {@code player} to a single character that can be used
     * to textually represent a piece.
     * @param player The player or {@code null} to convert to a character.
     * @return The character representing {@code player}.
     */
    public static char toChar(@Nullable PlayerType player) {
        return player != null ? player.character : '.';
    }

    /**
     * Finds the player associated with the given character.
     * @param character The character associated with a player.
     * @return The player associated with the given character.
     */
    public static PlayerType getByChar(char character) {
        for (PlayerType player : values()) {
            if (player.character == character)
                return player;
        }
        throw new IllegalArgumentException("Unknown player character '" + character + "'");
    }
}
