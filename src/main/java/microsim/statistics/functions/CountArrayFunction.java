package microsim.statistics.functions;

import lombok.NonNull;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.IntArraySource;
import microsim.statistics.IntSource;
import microsim.statistics.LongArraySource;

/**
 * This class computes the number of values in an array taken from a data source. The mean function return always an int
 * value, so it implements the {@link IntSource} interface and the standard {@link DoubleSource} one.
 */
public class CountArrayFunction extends AbstractArrayFunction implements DoubleSource, IntSource {

    protected int count;

    /**
     * Create a count function on an integer array source.
     *
     * @param source The data source.
     */
    public CountArrayFunction(final @NonNull IntArraySource source) {
        super(source);
    }

    /**
     * Create a count function on a long array source.
     *
     * @param source The data source.
     */
    public CountArrayFunction(final @NonNull LongArraySource source) {
        super(source);
    }

    /**
     * Create a count function on a double array source.
     *
     * @param source The data source.
     */
    public CountArrayFunction(final @NonNull DoubleArraySource source) {
        super(source);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final double @NonNull [] data) {
        count = data.length;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final int @NonNull [] data) {
        count = data.length;
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final long @NonNull [] data) {
        count = data.length;
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleValue(final @NonNull Enum<?> variableID) {
        return count;
    }

    /**
     * {@inheritDoc}
     */
    public int getIntValue(final @NonNull Enum<?> id) {
        return count;
    }
}
