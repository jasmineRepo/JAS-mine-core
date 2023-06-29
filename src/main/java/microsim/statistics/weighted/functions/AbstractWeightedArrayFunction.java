package microsim.statistics.weighted.functions;

import lombok.NonNull;
import microsim.statistics.UpdatableSource;
import microsim.statistics.weighted.WeightedDoubleArraySource;
import microsim.statistics.weighted.WeightedIntArraySource;
import microsim.statistics.weighted.WeightedLongArraySource;
import microsim.statistics.functions.AbstractFunction;

/**
 * This class represents the skeleton for all the function which operate on array of native data type values,
 * appropriately weighted by weights specified in a corresponding array of doubles. Each inheriting class automatically
 * implements the {@link UpdatableSource} and the {@link microsim.event.EventListener}, which are managed by the
 * AbstractWeightedArrayFunction.
 */
public abstract class AbstractWeightedArrayFunction extends AbstractFunction {

    protected static final int TYPE_DBL = 0;
    protected static final int TYPE_INT = 1;
    protected static final int TYPE_LNG = 2;

    protected WeightedDoubleArraySource dblSource;
    protected WeightedIntArraySource intSource;
    protected WeightedLongArraySource lngSource;
    protected int type;


    /**
     * Create a function on a double array source.
     *
     * @param source The data source.
     */
    public AbstractWeightedArrayFunction(final @NonNull WeightedDoubleArraySource source) {
        super();
        type = TYPE_DBL;
        dblSource = source;
    }

    /**
     * Create a function on an integer array source.
     *
     * @param source The data source.
     */
    public AbstractWeightedArrayFunction(final @NonNull WeightedIntArraySource source) {
        super();
        type = TYPE_INT;
        intSource = source;
    }

    /**
     * Create a function on a long array source.
     *
     * @param source The data source.
     */
    public AbstractWeightedArrayFunction(final @NonNull WeightedLongArraySource source) {
        super();
        type = TYPE_LNG;
        lngSource = source;
    }

    /**
     * Force the function to update itself. If the data source implements the {@link UpdatableSource} interface it is
     * updated before reading data.
     */
    public void applyFunction() {
        switch (type) {
            case TYPE_DBL -> {
                if (dblSource instanceof UpdatableSource)
                    ((UpdatableSource) dblSource).updateSource();
                apply(dblSource.getDoubleArray(), dblSource.getWeights());
            }
            case TYPE_INT -> {
                if (intSource instanceof UpdatableSource)
                    ((UpdatableSource) intSource).updateSource();
                apply(intSource.getIntArray(), intSource.getWeights());
            }
            case TYPE_LNG -> {
                if (lngSource instanceof UpdatableSource)
                    ((UpdatableSource) lngSource).updateSource();
                apply(lngSource.getLongArray(), lngSource.getWeights());
            }
        }
    }


    /**
     * Apply the function to the given array of double values.
     *
     * @param data    A source array of values.
     * @param weights An array of weights.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(final double @NonNull [] data, final double @NonNull [] weights) {
        if (data.length != weights.length) {
            throw new IllegalArgumentException("Error: length of data array ( = " + data.length +
                " and length of weights array ( = " + weights.length + " do not match!");
        }
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of double values.");
    }

    /**
     * Apply the function to the given array of integer values.
     *
     * @param data    A source array of values.
     * @param weights An array of weights.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(final int @NonNull [] data, final double @NonNull [] weights) {
        if (data.length != weights.length) {
            throw new IllegalArgumentException("Error: length of data array ( = " + data.length +
                " and length of weights array ( = " + weights.length + " do not match!");
        }
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of integer values.");
    }

    /**
     * Apply the function to the given array of long values.
     *
     * @param data    A source array of values.
     * @param weights An array of weights.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(final long @NonNull [] data, final double @NonNull [] weights) {
        if (data.length != weights.length) {
            throw new IllegalArgumentException("Error: length of data array ( = " + data.length +
                " and length of weights array ( = " + weights.length + " do not match!");
        }
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of long values.");
    }
}
