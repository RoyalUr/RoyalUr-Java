package net.royalur.notation.name;

import javax.annotation.Nonnull;

/**
 * A name that is backed by a String.
 */
public class TextName implements Name {

    /**
     * The text name.
     */
    private final @Nonnull String name;

    /**
     * Instantiates a text name.
     * @param name The text name.
     */
    public TextName(@Nonnull String name) {
        this.name = name;
    }

    @Override
    public @Nonnull String getTextName() {
        return name;
    }
}
