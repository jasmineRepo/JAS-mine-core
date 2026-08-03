
package microsim.dev.statistics;

import java.util.List;
import java.util.stream.Collectors;

import microsim.caching.Once;

public class WeightedStats {
    private List<Double> values;
    private List<Double> weights;
    private Once<Double> weightedSum = new Once<>(() -> wsum(this.values, this.weights));
    private Once<Double> weightSum = new Once<>(() -> this.weights.stream().mapToDouble(Double::doubleValue).sum());

    public WeightedStats(List<? extends Number> values, List<? extends Number> weights) {
        this.values = values.stream().map(Number::doubleValue).collect(Collectors.toUnmodifiableList());
        this.weights = weights.stream().map(Number::doubleValue).collect(Collectors.toUnmodifiableList());
        if (this.values.size() != this.weights.size()) {
            throw new RuntimeException("WeightedStats: values and weights should have same length");
        }
    }

    private static double wsum(List<Double> values, List<Double> weights) {
        double wsum = 0;
        int n = values.size();
        for (var i = 0; i < n; i++) {
            wsum += values.get(i) * weights.get(i);
        }
        return wsum;
    }

    public int count() {
        return this.values.size();
    }

    public double sum() {
        return this.weightedSum.get();
    }

    public double weightSum() {
        return this.weightSum.get();
    }

    public double mean() {
        return this.sum() / this.weightSum();
    }
}
