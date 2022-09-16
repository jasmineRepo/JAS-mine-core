package microsim.statistics.functions;

import lombok.NonNull;
import microsim.statistics.*;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;

import static jamjam.Sum.sum;

/**
 * This class computes the sum of an array of source values. According to the source data type there are three data-type
 * oriented implementations. Each of them implements always the {@link DoubleSource} interface.
 */
public abstract class SumArrayFunction extends AbstractArrayFunction implements DoubleSource {

    /**
     * Create a sum function on an integer array source.
     *
     * @param source The data source.
     */
    public SumArrayFunction(final @NonNull IntArraySource source) {
        super(source);
    }

    /**
     * Create a sum function on a long array source.
     *
     * @param source The data source.
     */
    public SumArrayFunction(final @NonNull LongArraySource source) {
        super(source);
    }

    /**
     * Create a sum function on a double array source.
     *
     * @param source The data source.
     */
    public SumArrayFunction(final @NonNull DoubleArraySource source) {
        super(source);
    }

    /**
     * SumFunction operating on double source values.
     */
    public static class Double extends SumArrayFunction implements DoubleSource {
        protected double dsum;

        /**
         * Create a sum function on a double array source.
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
            dsum = sum(data);
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> id) {
            return dsum;
        }
    }

    /**
     * SumFunction operating on long source values.
     */
    public static class Long extends SumArrayFunction implements LongSource {
        protected long lsum;

        /**
         * Create a sum function on a long array source.
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
            lsum = 0;
            for (long datum : data) lsum += datum;

        }

        /**
         * {@inheritDoc}
         */
        public long getLongValue(final @NonNull Enum<?> id) {
            return lsum;
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return lsum;
        }
    }

    /**
     * SumFunction operating on integer source values.
     */
    public static class Integer extends SumArrayFunction implements IntSource {
        protected int isum;

        /**
         * Create a sum function on an integer array source.
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
            isum = 0;
            for (int datum : data) isum += datum;
        }

        /**
         * {@inheritDoc}
         */
        public int getIntValue(final @NonNull Enum<?> id) {
            return isum;
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return isum;
        }
    }
}
