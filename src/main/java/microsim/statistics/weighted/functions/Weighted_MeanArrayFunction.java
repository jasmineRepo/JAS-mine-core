package microsim.statistics.weighted.functions;

import jamjam.Mean;
import lombok.NonNull;
import microsim.statistics.DoubleSource;
import microsim.statistics.weighted.WeightedDoubleArraySource;
import microsim.statistics.weighted.WeightedIntArraySource;
import microsim.statistics.weighted.WeightedLongArraySource;

/**
 * This class computes the (weighted) average (mean) value of an array of values taken from a data source,
 * weighted by corresponding weights:
 * weighted mean = sum (values * weights) / sum (weights)
 * Note that the array of weights must have the same length as the array of values, otherwise an exception will be
 * thrown. The mean function return always double values, so it implements only the {@link DoubleSource} interface.
 */
public class Weighted_MeanArrayFunction extends AbstractWeightedArrayFunction implements DoubleSource {

    protected double weightedMean;

    /**
     * Create a mean function on an integer array source.
     *
     * @param source The data source.
     */
    public Weighted_MeanArrayFunction(final @NonNull WeightedIntArraySource source) {
        super(source);
    }

    /**
     * Create a mean function on a long array source.
     *
     * @param source The data source.
     */
    public Weighted_MeanArrayFunction(final @NonNull WeightedLongArraySource source) {
        super(source);
    }

    /**
     * Create a mean function on a (weighted) double array source.
     *
     * @param source The weighted data source.
     */
    public Weighted_MeanArrayFunction(final @NonNull WeightedDoubleArraySource source) {
        super(source);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final double @NonNull [] data, final double @NonNull [] weights) {
        weightedMean = Mean.weightedMean(data, weights);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final int @NonNull [] data, final double @NonNull [] weights) {
        weightedMean = Mean.weightedMean(data, weights);
    }

    /**
     * {@inheritDoc}
     */
    public void apply(final long @NonNull [] data, final double @NonNull [] weights) {
        weightedMean = Mean.weightedMean(data, weights);
    }

    /**
     * {@inheritDoc}
     */
    public double getDoubleValue(final @NonNull Enum<?> variableID) {
        return weightedMean;
    }


}
