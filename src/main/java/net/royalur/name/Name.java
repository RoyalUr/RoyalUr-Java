package net.royalur.name;

import javax.annotation.Nonnull;

/**
 * A name given to a type of thing to differentiate it
 * when serialising, de-serialising, or logging.
 */
public interface Name {

    /**
     * Gets the text name.
     * @return The text name.
     */
    @Nonnull String getTextName();

    /**
     * Gets whether this name has an associated integer ID.
     * @return Whether this name has an associated integer ID.
     */
    boolean hasID();

    /**
     * Gets the integer ID associated with this name.
     * @return The integer ID associated with this name.
     * @throws UnsupportedOperationException if this name does not have an associated ID.
     */
    int getID();

    /**
     * Instantiates a new text-based name.
     * @param text The text name.
     * @return A new text-based name.
     */
    static @Nonnull Name of(@Nonnull String text) {
        return new TextName(text);
    }
}
