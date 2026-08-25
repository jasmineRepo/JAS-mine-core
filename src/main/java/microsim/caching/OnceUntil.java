package microsim.caching;

import java.util.Objects;
import java.util.function.Supplier;

import microsim.IfChanged;
import microsim.engine.SimulationEngine;

/// Cache the result of a [Supplier] so that it is only called only once until
/// the condition becomes true.
///
/// This requires the wrapped [Supplier] to return a non-null object. This is
/// lazy in the sense that the wrapped [Supplier] is only called when the value
/// is requested (either for the first time or when the simulation time
/// has changed).
public class OnceUntil<T> implements Supplier<T> {
    private final Supplier<? extends T> supplier;
    private T value;
    private Supplier<Boolean> condition;

    /// Build a cache wrapping the given [Supplier], using the provided `condition`
    /// to know when the inner value should be updated.
    public OnceUntil(Supplier<? extends T> supplier, Supplier<Boolean> condition) {
        this.supplier = supplier;
        this.value = null;
        this.condition = condition;
    }

    /// Build a cache that updates once per simulation tick.
    ///
    /// This is provided for convenience as this is a common caching condition.
    public static <T> OnceUntil<T> timeChanges(Supplier<? extends T> supplier, SimulationEngine engine) {
        return new OnceUntil<>(supplier, IfChanged.time(engine));
    }

    /// Get the value.
    ///
    /// @throws NullPointerException if the wrapped [Supplier] returns `null`.
    @Override
    public T get() {
        if (this.condition.get() || this.value == null) {
            this.value = Objects.requireNonNull(this.supplier.get());
        }
        return this.value;
    }
}
