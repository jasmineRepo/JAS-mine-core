package microsim.dev.statistics;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import microsim.caching.Once;

/// Statistics over a collection of data.
///
/// Note that results are cached, for example calling
/// ```java
/// stats.sum();
/// stats.sum();
/// stats.mean();
/// ```
/// calculates the sum of elements only once and reuses the results
/// in subsequent calls and to compute the mean.
/// ```
public class Stats {
    private List<Double> values;
    private Once<Double> min = new Once<>(() -> Collections.min(this.values));
    private Once<Double> max = new Once<>(() -> Collections.max(this.values));
    private Once<Double> sum = new Once<>(() -> this.doubles().sum());
    private Once<Double> sumSquare = new Once<>(() -> this.values.stream().mapToDouble(v -> v * v).sum());
    private Once<DescriptiveStatistics> descrStats = new Once<>(
            () -> new DescriptiveStatistics(this.doubles().toArray()));

    /// Build a [Stats] object for the given list of values.
    ///
    /// Each value is converted to a [Double] internally.
    public Stats(List<? extends Number> values) {
        this.values = values.stream().map(Number::doubleValue).collect(Collectors.toUnmodifiableList());
    }

    /// Convenience method to work with the [Supplier] API.
    public static Supplier<Stats> supplier(Supplier<? extends List<? extends Number>> supplier) {
        return () -> new Stats(supplier.get());
    }

    private DoubleStream doubles() {
        return this.values.stream().mapToDouble(Double::doubleValue);
    }

    /// The number of values.
    public int count() {
        return this.values.size();
    }

    /// The minimum value.
    public double min() {
        return this.min.get();
    }

    /// The maximum value.
    public double max() {
        return this.max.get();
    }

    /// The sum of all values.
    public double sum() {
        return this.sum.get();
    }

    /// The average of all values.
    public double mean() {
        return this.sum() / this.count();
    }

    /// The variance of all values (this is the "biased" empirical variance).
    public double variance() {
        return (this.sumSquare.get() - this.mean() * this.sum()) / this.count();
    }

    /// Average of the last `window` terms.
    public double averageLast(int window) {
        int n = java.lang.Math.min(this.count(), window);
        int firstIdx = this.count() - n;
        return this.values.subList(firstIdx, n).stream().mapToDouble(Double::doubleValue).sum() / n;
    }

    /// Create a [DescriptiveStatistics] object, use this if you need to access more
    /// statistical quantities (e.g. percentiles) than the one directly provided
    /// by [Stats].
    public DescriptiveStatistics descrStats() {
        return this.descrStats.get();
    }
}
