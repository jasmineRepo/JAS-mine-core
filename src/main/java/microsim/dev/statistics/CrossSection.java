package microsim.dev.statistics;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/// A cross section is a collection of values; each of them representing the
/// status of a given variable of an element of a collection of agents.
public class CrossSection<A, T> implements Supplier<List<T>> {
    private final Supplier<? extends Collection<? extends A>> source;
    private final Function<? super A, ? extends T> getObservable;

    public CrossSection(Supplier<? extends Collection<? extends A>> source,
            Function<? super A, ? extends T> getObservable) {
        this.source = source;
        this.getObservable = getObservable;
    }

    public List<T> get() {
        return this.source.get().stream().map(this.getObservable).collect(Collectors.toUnmodifiableList());
    }
}
