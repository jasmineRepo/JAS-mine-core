package microsim;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/// Lazy collection filtering.
///
/// This lazily applies a [Predicate] as filter to a collection, acting as a
/// [Supplier] for the filtered collection. This composes well with caching
/// mechanisms offered in [microsim.caching].
public class FilteredCollection<A> implements Supplier<List<A>> {
    private final Supplier<? extends Collection<? extends A>> source;
    private final Predicate<? super A> predicate;

    /// Create a lazy filtered collection.
    public FilteredCollection(Supplier<? extends Collection<? extends A>> source, Predicate<? super A> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    /// Create a new filtered collection derived from this one by `and`-ing
    /// both predicates.
    public FilteredCollection<A> and(Predicate<? super A> predicate) {
        // Note: cannot use `Predicate::and` here because of contravariance handling.
        return new FilteredCollection<>(this.source, a -> this.predicate.test(a) && predicate.test(a));
    }

    /// Create a new filtered collection derived from this one by `or`-ing
    /// both predicates.
    public FilteredCollection<A> or(Predicate<? super A> predicate) {
        // Note: cannot use `Predicate::or` here because of contravariance handling.
        return new FilteredCollection<>(this.source, a -> this.predicate.test(a) || predicate.test(a));
    }

    /// Create a new filtered collection derived from this one by negating
    /// the predicate.
    public FilteredCollection<A> negate() {
        return new FilteredCollection<>(this.source, this.predicate.negate());
    }

    /// Apply the filtering.
    @Override
    public List<A> get() {
        return this.source.get().stream().filter(this.predicate).collect(Collectors.toUnmodifiableList());
    }
}
