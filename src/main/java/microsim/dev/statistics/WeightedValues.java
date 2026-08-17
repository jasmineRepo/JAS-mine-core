package microsim.dev.statistics;

import java.util.Collections;
import java.util.List;

/// A bundle of values and their weights.
public class WeightedValues<T> {
    private final List<T> values;
    private final List<Double> weights;

    /// Associate `values` with `weights`.
    public WeightedValues(List<? extends T> values, List<Double> weights) {
        if (values.size() != weights.size()) {
            throw new IllegalArgumentException("values and weights should have the same length");
        }
        this.values = Collections.unmodifiableList(values);
        this.weights = Collections.unmodifiableList(weights);
    }

    /// The list of values.
    public List<T> values() {
        return this.values;
    }

    /// The list of weights.
    public List<Double> weights() {
        return this.weights;
    }
}
