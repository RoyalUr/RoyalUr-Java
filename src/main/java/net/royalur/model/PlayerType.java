package net.royalur.model;

import net.royalur.name.Name;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the players of a game.
 */
public enum PlayerType implements Name {

    /**
     * The light player. Following chess, the light player moves first.
     */
    LIGHT(1, "Light", 'L'),

    /**
     * The dark player. Following chess, the dark player moves second.
     */
    DARK(2, "Dark", 'D');

    /**
     * A constant numerical ID representing the player.
     * This ID will never change.
     */
    private final int id;

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
     * @param id A fixed numerical identifier to represent this player.
     * @param name An English name for this player.
     * @param character A fixed character to represent this player.
     */
    PlayerType(int id, String name, char character) {
        this.id = id;
        this.name = name;
        this.character = character;
        this.characterString = Character.toString(character);
    }

    @Override
    public String getTextName() {
        return name;
    }

    @Override
    public boolean hasID() {
        return true;
    }

    @Override
    public int getID() {
        return id;
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
