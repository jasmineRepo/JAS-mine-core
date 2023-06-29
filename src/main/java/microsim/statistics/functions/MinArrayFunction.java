package microsim.statistics.functions;

import lombok.NonNull;
import microsim.statistics.*;

import java.util.Arrays;

/**
 * This class computes the minimum value in an array of source values. According to the source data type there are three
 * data-type oriented implementations. Each of them implements always the {@link DoubleSource} interface.
 */
public abstract class MinArrayFunction extends AbstractArrayFunction implements DoubleSource {

    /**
     * Create a minimum function on an int array source.
     *
     * @param source The data source.
     */
    public MinArrayFunction(final @NonNull IntArraySource source) {
        super(source);
    }

    /**
     * Create a minimum function on a long array source.
     *
     * @param source The data source.
     */
    public MinArrayFunction(final @NonNull LongArraySource source) {
        super(source);
    }

    /**
     * Create a minimum function on a double array source.
     *
     * @param source The data source.
     */
    public MinArrayFunction(final @NonNull DoubleArraySource source) {
        super(source);
    }

    /**
     * MinFunction operating on double source values.
     */
    public static class Double extends MinArrayFunction implements DoubleSource {
        protected double min;

        /**
         * Create a minimum function on a double array source.
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
            min = java.lang.Double.MAX_VALUE;
            Arrays.stream(data).filter(datum -> min > datum).forEach(datum -> min = datum);

        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return min;
        }
    }

    /**
     * MinFunction operating on long source values.
     */
    public static class Long extends MinArrayFunction implements LongSource {
        protected long lmin;

        /**
         * Create a minimum function on a long array source.
         *
         * @param source The data source.
         */
        public Long(final @NonNull LongArraySource source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public void apply(final long @NonNull [] data) {
            lmin = java.lang.Long.MAX_VALUE;
            Arrays.stream(data).filter(datum -> lmin > datum).forEach(datum -> lmin = datum);
        }

        /**
         * {@inheritDoc}
         */
        public long getLongValue(final @NonNull Enum<?> variableID) {
            return lmin;
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return lmin;
        }
    }

    /**
     * MinFunction operating on integer source values.
     */
    public static class Integer extends MinArrayFunction implements IntSource {
        protected int imin;

        /**
         * Create a minimum function on an integer array source.
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
            imin = java.lang.Integer.MAX_VALUE;
            Arrays.stream(data).filter(datum -> imin > datum).forEach(datum -> imin = datum);
        }

        /**
         * {@inheritDoc}
         */
        public int getIntValue(final @NonNull Enum<?> variableID) {
            return imin;
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return imin;
        }
    }
}
