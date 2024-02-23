package net.royalur.name;

import java.util.*;

/**
 * A store of named things that requires uniqueness of names.
 * @param <N> The type of names.
 * @param <V> The type of named things.
 */
public class UniqueNameMap<N extends Name, V> implements NameMap<N, V> {

    /**
     * The key/value pairs stored in this map.
     */
    private final List<Entry<N, V>> entries;

    /**
     * The entries stored by their name.
     */
    private final Map<String, Entry<N, V>> byLowercaseName;

    /**
     * The entries stored by their ID.
     */
    private final Map<Integer, Entry<N, V>> byID;

    /**
     * Instantiates an empty store.
     */
    public UniqueNameMap() {
        this.entries = new ArrayList<>();
        this.byLowercaseName = new HashMap<>();
        this.byID = new HashMap<>();
    }

    /**
     * Instantiates this named store with all entries from {@code original}.
     * @param original The original named store to copy entries from.
     */
    public UniqueNameMap(NameMap<N, V> original) {
        this();
        for (Entry<N, V> entry : original.entries()) {
            put(entry);
        }
    }

    private void put(Entry<N, V> entry) {
        N name = entry.getName();
        String textName = name.getTextName();
        String lowercaseName = textName.toLowerCase();

        if (byLowercaseName.containsKey(lowercaseName))
            throw new IllegalArgumentException("Name is already included: \"" + textName + "\"");
        if (name.hasID() && byID.containsKey(name.getID()))
            throw new IllegalArgumentException("ID is already included: " + name.getID());

        entries.add(entry);
        byLowercaseName.put(lowercaseName, entry);
        if (name.hasID()) {
            byID.put(name.getID(), entry);
        }
    }

    @Override
    public void put(N name, V value) {
        put(new Entry<>(name, value));
    }

    @Override
    public V get(int id) {
        Entry<N, V> entry = byID.get(id);
        if (entry != null)
            return entry.getValue();

        throw new IllegalArgumentException("Unknown ID: " + id);
    }

    @Override
    public V get(String textName) {
        String lowercaseName = textName.toLowerCase();
        Entry<N, V> entry = byLowercaseName.get(lowercaseName);
        if (entry != null)
            return entry.getValue();

        throw new IllegalArgumentException("Unknown name: \"" + textName + "\"");
    }

    @Override
    public V get(N name) {
        return get(name.getTextName());
    }

    @Override
    public Collection<Entry<N, V>> entries() {
        return Collections.unmodifiableList(entries);
    }
}
