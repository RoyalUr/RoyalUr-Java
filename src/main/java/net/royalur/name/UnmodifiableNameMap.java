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
    private final @Nonnull NameMap<N, V> delegate;

    /**
     * Instantiates a new name map that cannot be modified.
     * @param delegate The delegate that actually stores the named things.
     */
    public UnmodifiableNameMap(@Nonnull NameMap<N, V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(@Nonnull N name, @Nonnull V value) {
        throw new UnsupportedOperationException("This map does not allow modification");
    }

    @Override
    public @Nonnull V get(int id) {
        return delegate.get(id);
    }

    @Override
    public @Nonnull V get(@Nonnull String textName) {
        return delegate.get(textName);
    }

    @Override
    public @Nonnull V get(@Nonnull N name) {
        return delegate.get(name);
    }

    @Override
    public @Nonnull Collection<Entry<N, V>> entries() {
        return delegate.entries();
    }
}
