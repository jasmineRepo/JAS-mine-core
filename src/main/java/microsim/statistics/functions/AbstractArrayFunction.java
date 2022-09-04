package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.FloatArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;
import microsim.statistics.UpdatableSource;

/**
 * This class represents the skeleton for all the function which operate on array of native data type values.
 * Each inheriting class automatically implements the <i>UpdatableSource</i> and the <i>ISimEventListener</i>,
 * which are managed by the AbstractArrayFunction.
 */
public abstract class AbstractArrayFunction extends AbstractFunction {

    protected static final int TYPE_DBL = 0;
    protected static final int TYPE_FLT = 1;
    protected static final int TYPE_INT = 2;
    protected static final int TYPE_LNG = 3;

    protected DoubleArraySource dblSource;
    protected FloatArraySource fltSource;
    protected IntArraySource intSource;
    protected LongArraySource lngSource;
    protected int type;


    /**
     * Create a function on a double array source.
     *
     * @param source The data source.
     */
    public AbstractArrayFunction(DoubleArraySource source) {
        super();
        type = TYPE_DBL;
        dblSource = source;
    }

    /**
     * Create a function on a float array source.
     *
     * @param source The data source.
     */
    public AbstractArrayFunction(FloatArraySource source) {
        super();
        type = TYPE_FLT;
        fltSource = source;
    }

    /**
     * Create a function on an integer array source.
     *
     * @param source The data source.
     */
    public AbstractArrayFunction(IntArraySource source) {
        super();
        type = TYPE_INT;
        intSource = source;
    }

    /**
     * Create a function on a long array source.
     *
     * @param source The data source.
     */
    public AbstractArrayFunction(LongArraySource source) {
        super();
        type = TYPE_LNG;
        lngSource = source;
    }

    /**
     * Force the function to update itself. If the data source implements the <i>UpdatableSource</i>
     * interface it is updated before reading data.
     */
    public void applyFunction() {
        switch (type) {
            case TYPE_DBL -> {
                if (dblSource instanceof UpdatableSource)
                    ((UpdatableSource) dblSource).updateSource();
                apply(dblSource.getDoubleArray());
            }
            case TYPE_FLT -> {
                if (fltSource instanceof UpdatableSource)
                    ((UpdatableSource) fltSource).updateSource();
                apply(fltSource.getFloatArray());
            }
            case TYPE_INT -> {
                if (intSource instanceof UpdatableSource)
                    ((UpdatableSource) intSource).updateSource();
                apply(intSource.getIntArray());
            }
            case TYPE_LNG -> {
                if (lngSource instanceof UpdatableSource)
                    ((UpdatableSource) lngSource).updateSource();
                apply(lngSource.getLongArray());
            }
        }
    }


    /**
     * Apply the function on a the given array of double values.
     *
     * @param data A source array of values.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(double[] data) {
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of double values.");
    }

    /**
     * Apply the function on a the given array of float values.
     *
     * @param data A source array of values.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(float[] data) {
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of float values.");
    }

    /**
     * Apply the function on a the given array of integer values.
     *
     * @param data A source array of values.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(int[] data) {
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of integer values.");
    }

    /**
     * Apply the function on a the given array of long values.
     *
     * @param data A source array of values.
     * @throws UnsupportedOperationException If the function is not able to work on double data type.
     */
    public void apply(long[] data) {
        throw new UnsupportedOperationException("This function class cannot be applied to arrays of long values.");
    }
}
