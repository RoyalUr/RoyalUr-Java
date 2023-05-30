package net.royalur.name;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A store of named values, where names are stored case-insensitive.
 * @param <N> The type of names.
 * @param <T> The type of values to store.
 */
public interface NameMap<N extends Name, T> {

    /**
     * Adds a new named thing into this store.
     * @param name The name to store the value with.
     * @param value The value to store.
     */
    void put(@Nonnull N name, @Nonnull T value);

    /**
     * Gets a named thing from this store.
     * @param textName The name of the thing.
     * @return The thing with the given name.
     * @throws IllegalArgumentException If there is no entry with the given name.
     */
    @Nonnull T get(@Nonnull String textName);

    /**
     * Gets a named thing from this store.
     * @param name The name of the thing.
     * @return The thing with the given name.
     * @throws IllegalArgumentException If there is no entry with the given name.
     */
    @Nonnull T get(@Nonnull N name);

    /**
     * Gets all the keys and values in this store.
     * @return All the key/value pairs in this store.
     */
    @Nonnull Collection<Entry<N, T>> entries();

    /**
     * Creates a modifiable copy of {@code this}.
     * @return A modifiable copy of {@code this}.
     */
    default @Nonnull NameMap<N, T> modifiableCopy() {
        return new UniqueNameMap<>(this);
    }

    /**
     * Creates an unmodifiable copy of {@code this}.
     * @return An unmodifiable copy of {@code this}.
     */
    default @Nonnull NameMap<N, T> unmodifiableCopy() {
        return new UnmodifiableNameMap<>(modifiableCopy());
    }

    /**
     * Creates an empty store for named values.
     * @return An empty store for named values.
     * @param <N> The type of names.
     * @param <T> The type of values.
     */
    static @Nonnull <N extends Name, T> NameMap<N, T> create() {
        return new UniqueNameMap<>();
    }

    /**
     * An entry in a name map.
     * @param <N> The type of names.
     * @param <T> The type of values.
     */
    class Entry<N extends Name, T> implements Named<N> {

        /**
         * The name of the value.
         */
        private final @Nonnull N name;

        /**
         * The value.
         */
        private final @Nonnull T value;

        /**
         * Instantiates an entry in a {@link NameMap}.
         * @param name The name of the value.
         * @param value The value.
         */
        public Entry(@Nonnull N name, @Nonnull T value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public @Nonnull N getName() {
            return name;
        }

        /**
         * Gets the value of this entry.
         * @return The value of this entry.
         */
        public @Nonnull T getValue() {
            return value;
        }
    }
}
