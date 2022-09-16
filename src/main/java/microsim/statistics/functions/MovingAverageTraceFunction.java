package microsim.statistics.functions;

import lombok.NonNull;
import microsim.event.CommonEventType;
import microsim.exception.SimulationRuntimeException;
import microsim.statistics.DoubleSource;
import microsim.statistics.IntSource;
import microsim.statistics.LongSource;

/**
 * This class computes the average of the last values collected from a data source. The number of values used to compute
 * the average value is specified in the constructor. The mean function return always double values, so it implements
 * only the {@link DoubleSource} interface.
 */
public class MovingAverageTraceFunction extends AbstractFunction implements DoubleSource {

    protected static final int TYPE_DBL = 0;
    protected static final int TYPE_INT = 1;
    protected static final int TYPE_LNG = 2;

    protected DoubleSource dblSource;
    protected IntSource intSource;
    protected LongSource lngSource;

    protected int type;
    protected Enum<?> valueID;

    protected int len;
    protected double[] values;
    protected double average;

    protected int valueCount = 0;

    /**
     * Create a basic statistic probe on a {@link DoubleSource} object.
     *
     * @param source  The {@link DoubleSource} object.
     * @param valueID The value identifier defined by source object.
     */
    public MovingAverageTraceFunction(final @NonNull DoubleSource source, final @NonNull Enum<?> valueID,
                                      final int windowSize) {
        super();
        if (windowSize < 1) throw new IllegalArgumentException("Unacceptable window size");
        dblSource = source;
        len = windowSize;
        this.valueID = valueID;
        values = new double[len];
    }

    /**
     * Create a basic statistic probe on a {@link LongSource} object.
     *
     * @param source  The {@link LongSource} object.
     * @param valueID The value identifier defined by source object.
     */
    public MovingAverageTraceFunction(final @NonNull LongSource source, final @NonNull Enum<?> valueID,
                                      final int windowSize) {
        super();
        if (windowSize < 1) throw new IllegalArgumentException("Unacceptable window size");
        lngSource = source;
        len = windowSize;
        this.valueID = valueID;
        values = new double[len];
    }

    /**
     * Create a basic statistic probe on a {@link IntSource} object.
     *
     * @param source  The {@link IntSource} object.
     * @param valueID The value identifier defined by source object.
     */
    public MovingAverageTraceFunction(final @NonNull IntSource source, final @NonNull Enum<?> valueID,
                                      final int windowSize) {
        super();
        if (windowSize < 1) throw new IllegalArgumentException("Unacceptable window size");
        intSource = source;
        len = windowSize;
        this.valueID = valueID;
        values = new double[len];
    }

    /**
     * Collect a value from the source.
     */
    public void applyFunction() {
        if (valueCount < len) {        //Slower calculation at startup as average is calculated directly by summing all entries in the values array
            valueCount++;            //First time this method is called, valueCount is incremented to 1.

            average = 0.;                //Reset value

            for (int i = len - valueCount; i < len - 1; i++) {            //First time this method is called, skips for loop.  When valueCount == len, i starts from 0.
                values[i] = values[i + 1];            //Thus, values[0] is oldest value, values[values.length] is latest value
                average += values[i];
            }
        } else {            //Faster calculation takes advantage of previously calculated average

            //No need to run through the whole array of values to update the average
            average *= len;
            average -= values[0];

            //Still update the values array (though not the average value)
            //Thus, values[0] is oldest value, values[values.length] is latest value
            if (len - 1 >= 0) System.arraycopy(values, 1, values, 0, len - 1);
        }

        switch (type) {
            case TYPE_DBL -> values[len - 1] = dblSource.getDoubleValue(valueID);
            case TYPE_LNG -> values[len - 1] = lngSource.getLongValue(valueID);
            case TYPE_INT -> values[len - 1] = intSource.getIntValue(valueID);
        }

        average += values[len - 1];
        //Divide by number of values included in calculation, instead of window length (len) which would give
        // moving average values biased towards zero.
        average = average / ((double) (Math.min(valueCount, len)));
    }


    /**
     * Return the result of a given statistic.
     *
     * @param valueID One of the {@link DoubleSource.Variables} constants representing available statistics.
     * @return The computed value.
     * @throws UnsupportedOperationException If the given valueID is not supported.
     */
    public double getDoubleValue(final @NonNull Enum<?> valueID) {
        return average;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(final @NonNull Enum<?> type) {
        if (type.equals(CommonEventType.Update)) updateSource();
        else throw new SimulationRuntimeException("The SimpleStatistics object does not support " + type +
            " operation.");
    }

}
