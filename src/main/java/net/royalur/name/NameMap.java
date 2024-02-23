package net.royalur.name;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A store of named values, where names are stored case-insensitive.
 * @param <N> The type of names.
 * @param <V> The type of values to store.
 */
public interface NameMap<N extends Name, V> {

    /**
     * Adds a new named thing into this store.
     * @param name The name to store the value with.
     * @param value The value to store.
     */
    void put(N name, V value);

    /**
     * Gets a named thing from this store by its text name.
     * @param textName The text name of the thing.
     * @return The thing with the given name.
     * @throws IllegalArgumentException If there is no entry with the given name.
     */
    @Nonnull
    V get(String textName);

    /**
     * Gets a named thing from this store by its integer ID.
     * @param id The integer ID of the thing.
     * @return The thing with the given ID.
     * @throws IllegalArgumentException If there is no entry with the given ID.
     */
    @Nonnull
    V get(int id);

    /**
     * Gets a named thing from this store.
     * @param name The name of the thing.
     * @return The thing with the given name.
     * @throws IllegalArgumentException If there is no entry with the given name.
     */
    @Nonnull
    V get(N name);

    /**
     * Gets all the keys and values in this store.
     * @return All the key/value pairs in this store.
     */
    Collection<Entry<N, V>> entries();

    /**
     * Creates a modifiable copy of {@code this}.
     * @return A modifiable copy of {@code this}.
     */
    default NameMap<N, V> modifiableCopy() {
        return new UniqueNameMap<>(this);
    }

    /**
     * Creates an unmodifiable copy of {@code this}.
     * @return An unmodifiable copy of {@code this}.
     */
    default NameMap<N, V> unmodifiableCopy() {
        return new UnmodifiableNameMap<>(modifiableCopy());
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
        private final N name;

        /**
         * The value.
         */
        private final T value;

        /**
         * Instantiates an entry in a {@link NameMap}.
         * @param name The name of the value.
         * @param value The value.
         */
        public Entry(N name, T value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public N getName() {
            return name;
        }

        /**
         * Gets the value of this entry.
         * @return The value of this entry.
         */
        public T getValue() {
            return value;
        }
    }
}
