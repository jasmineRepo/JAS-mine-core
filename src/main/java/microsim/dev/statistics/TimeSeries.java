package microsim.dev.statistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.function.Supplier;

import microsim.engine.SimulationEngine;

/// Extract time series at the end of every step.
///
/// This leverages [SimulationEngine#hookStepEnd].
public class TimeSeries<T> {
    private final SequencedMap<String, Supplier<? extends T>> suppliers;
    private final LinkedHashMap<String, ArrayList<T>> values;
    private final SimulationEngine engine;
    private final ArrayList<Double> times;

    /// Helper to initialise [TimeSeries] incrementally.
    public class Builder implements Supplier<TimeSeries<T>> {
        private final LinkedHashMap<String, Supplier<? extends T>> suppliers;
        private final SimulationEngine engine;

        /// Create a new [Builder] connected to the given [SimulationEngine]. The
        /// [TimeSeries] will register itself via [SimulationEngine#hookStepEnd].
        public Builder(SimulationEngine engine) {
            this.suppliers = new LinkedHashMap<>();
            this.engine = engine;
        }

        /// Add a time series, with a given `name` and [Supplier] for each element.
        public void add(String name, Supplier<? extends T> supplier) {
            this.suppliers.put(name, supplier);
        }

        /// Build the [TimeSeries].
        @Override
        public TimeSeries<T> get() {
            return new TimeSeries<>(Collections.unmodifiableSequencedMap(this.suppliers), this.engine);
        }
    }

    /// Directly build a [TimeSeries] from a map of time series (from name to
    /// supplier). The [TimeSeries] registers itself via
    /// [SimulationEngine#hookStepEnd].
    public TimeSeries(SequencedMap<String, Supplier<? extends T>> suppliers, SimulationEngine engine) {
        this.suppliers = suppliers;
        this.values = new LinkedHashMap<>(suppliers.size());
        for (var name : suppliers.sequencedKeySet()) {
            this.values.put(name, new ArrayList<>());
        }
        this.engine = engine;
        this.times = new ArrayList<>();
        this.engine.hookStepEnd(this::appendAll);
    }

    private void appendAll() {
        this.times.add(engine.getTime());
        for (var entry : this.suppliers.sequencedEntrySet()) {
            var name = entry.getKey();
            var supplier = entry.getValue();
            this.values.get(name).add(supplier.get());
        }
    }

    /// View on times at which the time series where gathered.
    public List<Double> times() {
        return Collections.unmodifiableList(this.times);
    }

    /// View on all the gathered time series.
    public SequencedMap<String, List<T>> values() {
        return Collections.unmodifiableSequencedMap(this.values);
    }
}
