package net.royalur.name;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A store of named things that does not allow modification.
 * @param <N> The type of names.
 * @param <V> The type of named things.
 */
public class UnmodifiableNameMap<N extends Name, V> implements NameMap<N, V> {

    /**
     * The delegate that actually stores the values.
     */
    private final NameMap<N, V> delegate;

    /**
     * Instantiates a new name map that cannot be modified.
     * @param delegate The delegate that actually stores the named things.
     */
    public UnmodifiableNameMap(NameMap<N, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(N name, V value) {
        throw new UnsupportedOperationException("This map does not allow modification");
    }

    @Override
    public V get(int id) {
        return delegate.get(id);
    }

    @Override
    public V get(String textName) {
        return delegate.get(textName);
    }

    @Override
    public V get(N name) {
        return delegate.get(name);
    }

    @Override
    public Collection<Entry<N, V>> entries() {
        return delegate.entries();
    }
}
