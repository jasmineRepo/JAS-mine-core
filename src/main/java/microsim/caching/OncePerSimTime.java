package microsim.caching;

import java.util.Objects;
import java.util.function.Supplier;

import microsim.engine.SimulationEngine;

/// Cache the result of a [Supplier] so that it is only called once per
/// simulation tick.
///
/// This requires the wrapped [Supplier] to return a non-null object. This is
/// lazy in the sense that the wrapped [Supplier] is only called when the value
/// is requested (either for the first time or when the simulation time
/// has changed).
public class OncePerSimTime<T> implements Supplier<T> {
    private final Supplier<? extends T> supplier;
    private T value;
    private final SimulationEngine engine;
    private double lastUpdateTime;

    /// Build a cache wrapping the given [Supplier].
    ///
    /// Note that wrapping a [OncePerSimTime] connected to the same
    /// [SimulationEngine] automatically flattens it.
    public OncePerSimTime(SimulationEngine engine, Supplier<? extends T> supplier) {
        if (supplier instanceof OncePerSimTime<? extends T> opst && opst.engine == engine) {
            this.supplier = opst.supplier;
            this.value = opst.value;
            this.engine = engine;
            this.lastUpdateTime = opst.lastUpdateTime;
        } else {
            this.supplier = supplier;
            this.value = null;
            this.engine = engine;
            this.lastUpdateTime = 0.0;
        }
    }

    /// Get the value.
    ///
    /// @throws NullPointerException if the wrapped [Supplier] returns `null`.
    @Override
    public T get() {
        double time;
        if ((time = this.engine.getTime()) != this.lastUpdateTime || this.value == null) {
            this.value = Objects.requireNonNull(this.supplier.get());
            this.lastUpdateTime = time;
        }
        return this.value;
    }
}
