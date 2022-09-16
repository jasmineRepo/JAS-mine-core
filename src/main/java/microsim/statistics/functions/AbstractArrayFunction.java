package microsim.statistics.functions;

import lombok.NonNull;
import microsim.statistics.DoubleArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import microsim.statistics.UpdatableSource;

/**
 * This class represents the skeleton for all the function which operate on array of native data type values.
 * Each inheriting class automatically implements the {@link UpdatableSource} and the
 * {@link microsim.event.EventListener} which are managed by the {@link AbstractArrayFunction}.
 */
public abstract class AbstractArrayFunction extends AbstractFunction {

    protected static final int TYPE_DBL = 0;
    protected static final int TYPE_INT = 1;
    protected static final int TYPE_LNG = 2;

    protected DoubleArraySource dblSource;
    protected IntArraySource intSource;
    protected LongArraySource lngSource;
    protected int type;


    /**
     * Creates a function on a double array source.
     *
     * @param source The data source.
     */
    public AbstractArrayFunction(final @NonNull DoubleArraySource source) {
        super();
        type = TYPE_DBL;
        dblSource = source;
    }

    /**
     * Creates a function on an integer array source.
     *
     * @param source The data source.
     */
    public AbstractArrayFunction(final @NonNull IntArraySource source) {
        super();
        type = TYPE_INT;
        intSource = source;
    }

    /**
     * Creates a function on a long array source.
     *
     * @param source The data source.
     */
    public AbstractArrayFunction(final @NonNull LongArraySource source) {
        super();
        type = TYPE_LNG;
        lngSource = source;
    }

    /**
     * Forces the function to update itself. If the data source implements the {@link UpdatableSource} interface it is
     * updated before reading data.
     */
    public void applyFunction() {
        switch (type) {
            case TYPE_DBL -> {
                if (dblSource instanceof UpdatableSource) ((UpdatableSource) dblSource).updateSource();
                apply(dblSource.getDoubleArray());
            }
            case TYPE_INT -> {
                if (intSource instanceof UpdatableSource) ((UpdatableSource) intSource).updateSource();
                apply(intSource.getIntArray());
            }
            case TYPE_LNG -> {
                if (lngSource instanceof UpdatableSource) ((UpdatableSource) lngSource).updateSource();
                apply(lngSource.getLongArray());
            }
        }
    }


    /**
     * Applies the function to the given array of double values.
     *
     * @param data A source array of values.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(final double @NonNull [] data) {
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of double values.");
    }

    /**
     * Applies the function to the given array of integer values.
     *
     * @param data A source array of values.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(final int @NonNull [] data) {
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of integer values.");
    }

    /**
     * Applies the function to the given array of long values.
     *
     * @param data A source array of values.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(final long @NonNull [] data) {
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of long values.");
    }
}
