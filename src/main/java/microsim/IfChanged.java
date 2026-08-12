package microsim;

import java.util.function.Supplier;

import microsim.engine.SimulationEngine;

/// Watch the ouput of a [Supplier], checking whether the returned value changed
/// since the last invocation.
///
/// This uses [Object#equals] to perform the comparison.
public class IfChanged<T> implements Supplier<Boolean> {
    private T lastValue;
    private Supplier<? extends T> source;

    /// Wrap the provided [Supplier].
    public IfChanged(Supplier<? extends T> source) {
        this.lastValue = null;
        this.source = source;
    }

    /// Build an [IfChanged] that checks whether the simulation time changed.
    public static IfChanged<Double> time(SimulationEngine engine) {
        return new IfChanged<>(engine::getTime);
    }

    /// Check whether the value changed since the last invocation.
    @Override
    public Boolean get() {
        var value = this.source.get();
        if (!value.equals(lastValue)) {
            this.lastValue = value;
            return true;
        }
        return false;
    }
}
