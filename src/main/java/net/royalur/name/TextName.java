package net.royalur.name;

import javax.annotation.Nonnull;

/**
 * A name that is backed by a String.
 */
public class TextName implements Name {

    /**
     * The text name.
     */
    private final String name;

    /**
     * Instantiates a text name.
     * @param name The text name.
     */
    public TextName(String name) {
        this.name = name;
    }

    @Override
    public String getTextName() {
        return name;
    }

    @Override
    public boolean hasID() {
        return false;
    }

    @Override
    public int getID() {
        throw new UnsupportedOperationException("No associated ID");
    }
}
