
package microsim.dev.statistics;

import java.util.List;
import java.util.function.Supplier;

import microsim.caching.Once;

/// Weighted statistics over a collection of data.
///
/// Note that results are cached, for example calling
/// ```java
/// stats.sum();
/// stats.sum();
/// stats.mean();
/// ```
/// calculates the weighted sum of elements only once and reuses the results in
/// subsequent calls and to compute the mean.
/// ```
public class WeightedStats {
    private WeightedValues<Double> wvals;
    private Once<Double> weightedSum = new Once<>(() -> wsum(this.wvals));
    private Once<Double> weightSum = new Once<>(
            () -> this.wvals.weights().stream().mapToDouble(Double::doubleValue).sum());

    /// Build a [WeightedStats] object for the given lists of values and weights.
    ///
    /// Each value and weight is converted to a [Double] internally.
    public WeightedStats(List<? extends Number> values, List<? extends Number> weights) {
        var vals = values.stream().map(Number::doubleValue).toList();
        var wgts = weights.stream().map(Number::doubleValue).toList();
        this.wvals = new WeightedValues<>(vals, wgts);
    }

    /// Build a [Stats] object for the given list of weighted values.
    ///
    /// Each value is converted to a [Double] internally.
    public WeightedStats(WeightedValues<? extends Number> wvals) {
        var values = wvals.values().stream().map(Number::doubleValue).toList();
        this.wvals = new WeightedValues<>(values, wvals.weights());
    }

    /// Convenience method to work with the [Supplier] API.
    public static Supplier<WeightedStats> supplier(Supplier<? extends WeightedValues<? extends Number>> supplier) {
        return () -> new WeightedStats(supplier.get());
    }

    private static double wsum(WeightedValues<Double> wvals) {
        double wsum = 0;
        var values = wvals.values();
        var weights = wvals.weights();
        int n = values.size();
        for (var i = 0; i < n; i++) {
            wsum += values.get(i) * weights.get(i);
        }
        return wsum;
    }

    /// The number of values.
    public int count() {
        return this.wvals.values().size();
    }

    /// The weighted sum of values.
    public double sum() {
        return this.weightedSum.get();
    }

    /// The sum of all weights.
    public double weightSum() {
        return this.weightSum.get();
    }

    /// The weighted average of values.
    public double mean() {
        return this.sum() / this.weightSum();
    }
}
