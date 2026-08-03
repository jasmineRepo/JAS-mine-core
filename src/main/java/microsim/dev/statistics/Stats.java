package microsim.dev.statistics;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import microsim.caching.Once;

public class Stats {
    private List<Double> values;
    private Once<Double> min = new Once<>(() -> Collections.min(this.values));
    private Once<Double> max = new Once<>(() -> Collections.max(this.values));
    private Once<Double> sum = new Once<>(() -> this.doubles().sum());
    private Once<Double> sumSquare = new Once<>(() -> this.values.stream().mapToDouble(v -> v * v).sum());
    private Once<DescriptiveStatistics> descrStats = new Once<>(
            () -> new DescriptiveStatistics(this.doubles().toArray()));

    public Stats(List<? extends Number> values) {
        this.values = values.stream().map(Number::doubleValue).collect(Collectors.toUnmodifiableList());
    }

    private DoubleStream doubles() {
        return this.values.stream().mapToDouble(Double::doubleValue);
    }

    public int count() {
        return this.values.size();
    }

    public double min() {
        return this.min.get();
    }

    public double max() {
        return this.max.get();
    }

    public double sum() {
        return this.sum.get();
    }

    public double mean() {
        return this.sum() / this.count();
    }

    public double variance() {
        return (this.sumSquare.get() - this.mean() * this.sum()) / this.count();
    }

    public double averageLast(int window) {
        int n = java.lang.Math.min(this.count(), window);
        int firstIdx = this.count() - n;
        return this.values.subList(firstIdx, n).stream().mapToDouble(Double::doubleValue).sum() / n;
    }

    public DescriptiveStatistics descrStats() {
        return this.descrStats.get();
    }
}
