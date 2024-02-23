package net.royalur.util;

import javax.annotation.Nonnull;

/**
 * A utility class to avoid warnings.
 */
public class Cast {

    /**
     * Can be used to cast between generic types when the cast is
     * known to be safe, to avoid type warnings.
     * @param from The original value.
     * @return The original value, cast to type T.
     * @param <F> The type of the original value.
     * @param <T> The type to cast the original value into.
     */
    @SuppressWarnings("unchecked")
    public static <F, T> T unsafeCast(F from) {
        return (T) from;
    }
}
