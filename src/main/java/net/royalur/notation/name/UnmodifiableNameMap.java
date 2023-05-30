package net.royalur.notation.name;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * A store of named things that does not allow modification.
 * @param <N> The type of names.
 * @param <T> The type of named things.
 */
public class UnmodifiableNameMap<N extends Name, T> implements NameMap<N, T> {

    /**
     * The delegate that actually stores the values.
     */
    private final @Nonnull NameMap<N, T> delegate;

    /**
     * Instantiates a new name map that cannot be modified.
     * @param delegate The delegate that actually stores the named things.
     */
    public UnmodifiableNameMap(@Nonnull NameMap<N, T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void put(@Nonnull N name, @Nonnull T value) {
        throw new UnsupportedOperationException("This map does not allow modification");
    }

    @Override
    public @Nonnull T get(@Nonnull String textName) {
        return delegate.get(textName);
    }

    @Override
    public @Nonnull T get(@Nonnull N name) {
        return delegate.get(name);
    }

    @Override
    public @Nonnull Collection<Entry<N, T>> entries() {
        return delegate.entries();
    }
}
