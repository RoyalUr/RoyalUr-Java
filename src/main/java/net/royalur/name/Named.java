package net.royalur.name;

import javax.annotation.Nonnull;

/**
 * A thing that has a {@link Name}.
 */
public interface Named<N extends Name> {

    /**
     * Get the name of {@code this}.
     * @return The name of {@code this}.
     */
    N getName();
}
