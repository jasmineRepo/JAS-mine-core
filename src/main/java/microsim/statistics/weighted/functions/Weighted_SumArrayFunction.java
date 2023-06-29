package microsim.statistics.weighted.functions;

import jamjam.Sum;
import lombok.NonNull;
import microsim.statistics.weighted.WeightedDoubleArraySource;
import microsim.statistics.weighted.WeightedIntArraySource;
import microsim.statistics.weighted.WeightedLongArraySource;

import microsim.statistics.DoubleSource;

import java.util.Arrays;

/**
 * This class computes the sum of an array of source values, with each element of the array multiplied by the weight of
 * the source (the source must implement the {@link microsim.agent.Weight} interface). According to the source data type
 * there are three data-type oriented implementations. Each of them implements always the {@link DoubleSource}
 * interface.
 */
public abstract class Weighted_SumArrayFunction extends AbstractWeightedArrayFunction implements DoubleSource {

    /**
     * Create a sum function on an integer array weighted-source.
     *
     * @param source The weighted data source.
     */
    public Weighted_SumArrayFunction(final @NonNull WeightedIntArraySource source) {
        super(source);
    }

    /**
     * Create a sum function on a long array weighted-source.
     *
     * @param source The weighted data source.
     */
    public Weighted_SumArrayFunction(final @NonNull WeightedLongArraySource source) {
        super(source);
    }

    /**
     * Create a sum function on a double array weighted-source.
     *
     * @param source The weighted data source.
     */
    public Weighted_SumArrayFunction(final @NonNull WeightedDoubleArraySource source) {
        super(source);
    }

    /**
     * SumFunction operating on weighted double source values.
     */
    public static class Double extends Weighted_SumArrayFunction {
        protected double dsum;

        /**
         * Create a sum function on a weighted double array source.
         *
         * @param source The data source.
         */
        public Double(final @NonNull WeightedDoubleArraySource source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public void apply(final double @NonNull [] data, final double @NonNull [] weights) {
            dsum = Sum.weightedSum(data, weights);
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> id) {
            return dsum;
        }
    }

    /**
     * SumFunction operating on weighted long source values.
     */
    public static class Long extends Weighted_SumArrayFunction {
        protected double lsum;

        /**
         * Create a sum function on a weighted long array source.
         *
         * @param source The weighted data source.
         */
        public Long(final @NonNull WeightedLongArraySource source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public void apply(final long @NonNull [] data, final double @NonNull [] weights) {
            lsum = Sum.weightedSum(Arrays.stream(data).asDoubleStream().toArray(), weights);
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(final @NonNull Enum<?> variableID) {
            return lsum;
        }
    }

    /**
     * SumFunction operating on weighted integer source values.
     */
    public static class Integer extends Weighted_SumArrayFunction {
        protected double isum;

        /**
         * Create a sum function on a weighted integer array source.
         *
         * @param source The weighted data source.
         */
        public Integer(final @NonNull WeightedIntArraySource source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         */
        public void apply(final int @NonNull [] data, final double @NonNull [] weights) {
            isum = Sum.weightedSum(Arrays.stream(data).asDoubleStream().toArray(), weights);
        }

        /**
         * {@inheritDoc}
         */
        public double getDoubleValue(@NonNull Enum<?> variableID) {
            return isum;
        }
    }
}
