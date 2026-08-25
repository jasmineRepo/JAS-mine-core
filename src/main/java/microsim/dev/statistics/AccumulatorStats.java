package microsim.dev.statistics;

import java.util.function.Consumer;
import java.util.function.Supplier;

import microsim.SideEffect;

/// Sampler that keeps statistical information about the samples.
///
/// This is an efficient way to compute simple statistics, e.g. over time, with a
/// small constant memory usage.
public class AccumulatorStats {
    private Supplier<? extends Number> source;
    private double lastValue;
    private double min;
    private double max;
    private double sum;
    private double sumSquare;
    private int count;

    /// Build an accumulator that samples from the given [Supplier], registered via
    /// the `sampleHook`.
    ///
    /// A common usage would be to collect a statistic at the end of every time step,
    /// leveraging [microsim.engine.SimulationEngine#hookStepEnd].
    ///
    /// For instance, to monitor the size of a population returned by `popSize()`:
    /// ```java
    /// var popSizeStats = new AccumulatorStats(popSize, engine::hookStepEnd);
    /// ```
    ///
    /// @param source     The supplier for each sampled value. It will internally be
    ///                   converted to a `double`.
    /// @param sampleHook The hook specifying when sampling is done.
    public AccumulatorStats(Supplier<? extends Number> source, Consumer<SideEffect> sampleHook) {
        this.source = source;
        this.lastValue = 0.0;
        this.min = Double.MAX_VALUE;
        this.max = -Double.MAX_VALUE;
        this.sum = 0.0;
        this.sumSquare = 0.0;
        this.count = 0;
        sampleHook.accept(this::sample);
    }

    private void sample() {
        var val = this.source.get().doubleValue();
        this.lastValue = val;
        this.min = Double.min(this.min, val);
        this.max = Double.max(this.max, val);
        this.sum += val;
        this.sumSquare += val * val;
        this.count += 1;
    }

    /// Last sampled value.
    public double lastValue() {
        return this.lastValue;
    }

    /// Sample minimum.
    public double min() {
        return this.min;
    }

    /// Sample maximum.
    public double max() {
        return this.max;
    }

    /// Sample sum.
    public double sum() {
        return this.sum;
    }

    /// Sample counts.
    public int count() {
        return this.count;
    }

    /// Sample average.
    public double mean() {
        if (this.count > 0) {
            return this.sum / this.count;
        }
        return 0.0;
    }

    /// Running unbiased variance (i.e. using Bessel's correction).
    public double unbiasedVariance() {
        if (this.count > 1) {
            return (this.sumSquare - this.mean() * this.sum) / (this.count - 1);
        }
        return 0.0;
    }
}
