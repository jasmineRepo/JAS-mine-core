package microsim.statistics.functions;

import lombok.NonNull;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;

import java.util.Arrays;

import static jamjam.Mean.mean;
import static jamjam.Variance.unweightedBiasedVariance;

/**
 * This class computes the average and variance value of an array of values taken from a data source. The mean function
 * always returns double values, so it implements only the {@link DoubleSource} interface. <BR>
 * In order to retrieve the mean pass the {@link MeanVarianceArrayFunction.Variables#Mean} argument to the
 * {@link #getDoubleValue(Enum)}  function, while for the variance the
 * {@link MeanVarianceArrayFunction.Variables#Variance} one.
 */
public class MeanVarianceArrayFunction extends AbstractArrayFunction implements DoubleSource {


    protected double mean, variance;

    /**
     * Create a mean function on an integer array source.
     *
     * @param source The data source.
     */
    public MeanVarianceArrayFunction(final @NonNull IntArraySource source) {
        super(source);
    }

    /**
     * Create a mean function on a long array source.
     *
     * @param source The data source.
     */
    public MeanVarianceArrayFunction(final @NonNull LongArraySource source) {
        super(source);
    }

    /**
     * Create a mean function on a double array source.
     *
     * @param source The data source.
     */
    public MeanVarianceArrayFunction(final @NonNull DoubleArraySource source) {
        super(source);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final double @NonNull [] data) {
        mean = mean(data);
        variance = unweightedBiasedVariance(data, mean);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final int @NonNull [] data) {
        mean = (double) Arrays.stream(data).asLongStream().sum() / data.length;
        variance = unweightedBiasedVariance(data, mean);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final long @NonNull [] data) {
        mean = (double) Arrays.stream(data).sum() / data.length;
        variance = unweightedBiasedVariance(data, mean);
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleValue(final @NonNull Enum<?> variableID) {
        return switch ((Variables) variableID) {
            case Mean -> mean;
            case Variance -> variance;
        };
    }

    public enum Variables {
        /**
         * Represent the mean function argument for the {@link #getDoubleValue(Enum)} method.
         */
        Mean,
        /**
         * Represent the variance function argument for the {@link #getDoubleValue(Enum)} method.
         */
        Variance
    }
}
