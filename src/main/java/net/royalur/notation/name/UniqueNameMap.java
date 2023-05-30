package net.royalur.notation.name;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * A store of named things that requires uniqueness of names.
 * @param <N> The type of names.
 * @param <T> The type of named things.
 */
public class UniqueNameMap<N extends Name, T> implements NameMap<N, T> {

    /**
     * The key/value pairs stored in this map.
     */
    private final @Nonnull List<Entry<N, T>> entries;

    /**
     * The entries stored by their name.
     */
    private final @Nonnull Map<String, Entry<N, T>> byLowercaseName;

    /**
     * Instantiates an empty store.
     */
    public UniqueNameMap() {
        this.entries = new ArrayList<>();
        this.byLowercaseName = new HashMap<>();
    }

    /**
     * Instantiates this named store with all entries from {@code original}.
     * @param original The original named store to copy entries from.
     */
    public UniqueNameMap(@Nonnull NameMap<N, T> original) {
        this();
        for (Entry<N, T> entry : original.entries()) {
            put(entry);
        }
    }

    private void put(@Nonnull Entry<N, T> entry) {
        N name = entry.getName();
        String textName = name.getTextName();
        String lowercaseName = textName.toLowerCase();

        if (byLowercaseName.containsKey(lowercaseName))
            throw new IllegalArgumentException("Name \"" + textName + "\" is already included");

        entries.add(entry);
        byLowercaseName.put(lowercaseName, entry);
    }

    @Override
    public void put(@Nonnull N name, @Nonnull T value) {
        put(new Entry<>(name, value));
    }

    @Override
    public @Nonnull T get(@Nonnull String textName) {
        String lowercaseName = textName.toLowerCase();
        Entry<N, T> entry = byLowercaseName.get(lowercaseName);
        if (entry != null)
            return entry.getValue();

        throw new IllegalArgumentException("Unknown name: \"" + textName + "\"");
    }

    @Override
    public @Nonnull T get(@Nonnull N name) {
        return get(name.getTextName());
    }

    @Override
    public @Nonnull Collection<Entry<N, T>> entries() {
        return Collections.unmodifiableList(entries);
    }
}
