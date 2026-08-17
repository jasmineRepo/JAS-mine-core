
package microsim.dev.statistics;

import java.util.List;

import microsim.caching.Once;

public class WeightedStats {
    private WeightedValues<Double> wvals;
    private Once<Double> weightedSum = new Once<>(() -> wsum(this.wvals));
    private Once<Double> weightSum = new Once<>(
            () -> this.wvals.weights().stream().mapToDouble(Double::doubleValue).sum());

    public WeightedStats(List<? extends Number> values, List<? extends Number> weights) {
        var vals = values.stream().map(Number::doubleValue).toList();
        var wgts = weights.stream().map(Number::doubleValue).toList();
        this.wvals = new WeightedValues<>(vals, wgts);
    }

    public WeightedStats(WeightedValues<? extends Number> wvals) {
        var values = wvals.values().stream().map(Number::doubleValue).toList();
        this.wvals = new WeightedValues<>(values, wvals.weights());
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

    public int count() {
        return this.wvals.values().size();
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
