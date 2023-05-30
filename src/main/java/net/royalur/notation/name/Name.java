package net.royalur.notation.name;

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
     * Instantiates a new text-based name.
     * @param text The text name.
     * @return A new text-based name.
     */
    static @Nonnull Name of(@Nonnull String text) {
        return new TextName(text);
    }
}
