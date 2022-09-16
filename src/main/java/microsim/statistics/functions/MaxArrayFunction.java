package microsim.statistics.functions;

import lombok.NonNull;
import microsim.statistics.*;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.LongSource;

import java.util.Arrays;

/**
 * This class computes the maximum value in an array of source values. According to the source data type there are three
 * data-type oriented implementations. Each of them implements always the {@link DoubleSource} interface.
 */
public abstract class MaxArrayFunction extends AbstractArrayFunction implements DoubleSource {

    /**
     * Create a maximum function on an integer array source.
     *
     * @param source The data source.
     */
    public MaxArrayFunction(final @NonNull IntArraySource source) {
        super(source);
    }

    /**
     * Create a maximum function on a long array source.
     *
     * @param source The data source.
     */
    public MaxArrayFunction(final @NonNull LongArraySource source) {
        super(source);
    }

    /**
     * Create a maximum function on a double array source.
     *
     * @param source The data source.
     */
    public MaxArrayFunction(final @NonNull DoubleArraySource source) {
        super(source);
    }

    /**
     * MaxFunction operating on double source values.
     */
    public static class Double extends MaxArrayFunction implements DoubleSource {
        protected double dmax;

        /**
         * Create a maximum function on a double array source.
         *
         * @param source The data source.
         */
        public Double(final @NonNull DoubleArraySource source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public void apply(final double @NonNull [] data) {
            dmax = java.lang.Double.MIN_VALUE;
            Arrays.stream(data).filter(datum -> dmax < datum).forEach(datum -> dmax = datum);
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> id) {
            return dmax;
        }
    }

    /**
     * MaxFunction operating on long source values.
     */
    public static class Long extends MaxArrayFunction implements LongSource {
        protected long lmax;

        /**
         * Create a maximum function on a long array source.
         *
         * @param source The data source.
         */
        public Long(final @NonNull LongArraySource source) {
            super(source);
        }

        public void apply(final long @NonNull [] data) {
            lmax = java.lang.Long.MIN_VALUE;
            Arrays.stream(data).filter(datum -> lmax < datum).forEach(datum -> lmax = datum);
        }

        /**
         * {@inheritDoc}
         */
        public long getLongValue(final @NonNull Enum<?> id) {
            return lmax;
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return lmax;
        }
    }

    /**
     * MaxFunction operating on integer source values.
     */
    public static class Integer extends MaxArrayFunction implements IntSource {
        protected int imax;

        /**
         * Create a maximum function on an integer array source.
         *
         * @param source The data source.
         */
        public Integer(final @NonNull IntArraySource source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public void apply(final int @NonNull [] data) {
            imax = java.lang.Integer.MIN_VALUE;
            Arrays.stream(data).filter(datum -> imax < datum).forEach(datum -> imax = datum);
        }

        /**
         * {@inheritDoc}
         */
        public int getIntValue(final @NonNull Enum<?> id) {
            return imax;
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return imax;
        }
    }
}
