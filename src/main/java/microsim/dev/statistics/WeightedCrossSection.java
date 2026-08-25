package microsim.dev.statistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

import microsim.caching.Once;
import microsim.caching.OnceUntil;
import microsim.engine.SimulationEngine;

/// A weighted cross section is a collection of values; each of them
/// representing the status of a given variable of an element of a collection of
/// agents, along with the weight associated with each agent.
public class WeightedCrossSection<A, T> implements Supplier<WeightedValues<T>> {
    private final Supplier<? extends Collection<? extends A>> source;
    private final Function<? super A, ? extends T> getObservable;
    private final Function<? super A, ? extends Double> getWeight;

    public WeightedCrossSection(Supplier<? extends Collection<? extends A>> source,
            Function<? super A, ? extends T> getObservable, Function<? super A, ? extends Double> getWeight) {
        this.source = source;
        this.getObservable = getObservable;
        this.getWeight = getWeight;
    }

    @Override
    public WeightedValues<T> get() {
        var source = this.source.get();
        var values = new ArrayList<T>(source.size());
        var weights = new ArrayList<Double>(source.size());
        for (var a : source) {
            values.add(this.getObservable.apply(a));
            weights.add(this.getWeight.apply(a));
        }
        return new WeightedValues<>(values, weights);
    }

    /// Wrap the [WeightedCrossSection] in a [Once] cache.
    public Once<WeightedValues<T>> once() {
        return new Once<>(this);
    }

    /// Wrap the [WeightedCrossSection] in a [OnceUntil] cache that checks when the
    /// simulation time changes.
    public OnceUntil<WeightedValues<T>> oncePerSimTime(SimulationEngine engine) {
        return OnceUntil.timeChanges(this, engine);
    }
}

