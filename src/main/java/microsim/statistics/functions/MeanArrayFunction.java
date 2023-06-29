package microsim.statistics.functions;

import lombok.NonNull;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import jamjam.Mean;
import java.util.Arrays;

/**
 * This class computes the average value of an array of values taken from a data source. The mean function return always
 * double values, so it implements only the {@link DoubleSource} interface.
 */
public class MeanArrayFunction extends AbstractArrayFunction implements DoubleSource {

    protected double mean;

    /**
     * Create a mean function on an integer array source.
     *
     * @param source The data source.
     */
    public MeanArrayFunction(final @NonNull IntArraySource source) {
        super(source);
    }

    /**
     * Create a mean function on a long array source.
     *
     * @param source The data source.
     */
    public MeanArrayFunction(final @NonNull LongArraySource source) {
        super(source);
    }

    /**
     * Create a mean function on a double array source.
     *
     * @param source The data source.
     */
    public MeanArrayFunction(final @NonNull DoubleArraySource source) {
        super(source);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final double @NonNull [] data) {
        mean = Mean.mean(data);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final int @NonNull [] data) {
        mean = data.length != 0 ? (double) Arrays.stream(data).asLongStream().sum() / data.length : 0.;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final long @NonNull [] data) {
        mean = data.length != 0 ? (double) Arrays.stream(data).sum() / data.length : 0.;
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleValue(final @NonNull Enum<?> variableID) {
        return mean;
    }
}
