package microsim.statistics.functions;

import microsim.event.CommonEventType;
import microsim.exception.SimulationRuntimeException;
import microsim.statistics.FloatSource;
import microsim.statistics.DoubleSource;
import microsim.statistics.IntSource;
import microsim.statistics.LongSource;

/**
 *  This class computes the average of the last values collected from a data source.
 * The number of values used to compute the average value is specified in the constructor. 
 * The mean function return always double values, so it implements only the 
 * <i>DoubleSource</i> interface.
 */
public class MovingAverageTraceFunction extends AbstractFunction implements DoubleSource  {
			
	protected static final int TYPE_DBL = 0;
	protected static final int TYPE_FLT = 1;
	protected static final int TYPE_INT = 2;
	protected static final int TYPE_LNG = 3;

	protected DoubleSource dblSource;
	protected FloatSource fltSource;
	protected IntSource intSource;
	protected LongSource lngSource;

	protected int type;
	protected Enum<?> valueID;
					
	protected int len = 0;
	protected double[] values;
	protected double average;
	
	protected int valueCount = 0;
	
	/** Create a basic statistic probe on a IDoubleSource object.
	 *  @param source The IDoubleSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(DoubleSource source, Enum<?> valueID, int windowSize) {
		super();
		dblSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}

	/** Create a basic statistic probe on a FloatSource object.
	 *  @param source The FloatSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(FloatSource source, Enum<?> valueID, int windowSize) {
		super();
		fltSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}
	
	/** Create a basic statistic probe on a LongSource object.
	 *  @param source The LongSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(LongSource source, Enum<?> valueID, int windowSize) {
		super();
		lngSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}
	
	/** Create a basic statistic probe on a IntSource object.
	 *  @param source The IntSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(IntSource source, Enum<?> valueID, int windowSize) {
		super();
		intSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}

	/** Collect a value from the source. 
	 * 
	 * @author Ross Richardson
	 * 
	 * */
	public void applyFunction() 
	{
		if(valueCount < len) {		//Slower calculation at startup as average is calculated directly by summing all entries in the values array
			valueCount++;			//First time this method is called, valueCount is incremented to 1.
			
			average = 0.;				//Reset value
			
			for (int i = len - valueCount; i < len - 1; i++) {			//First time this method is called, skips for loop.  When valueCount == len, i starts from 0.
				values[i] = values[i + 1];			//Thus, values[0] is oldest value, values[values.length] is latest value
				average += values[i];
			}

			switch (type) {
				case TYPE_DBL -> values[len - 1] = dblSource.getDoubleValue(valueID);
				case TYPE_FLT -> values[len - 1] = fltSource.getFloatValue(valueID);
				case TYPE_LNG -> values[len - 1] = lngSource.getLongValue(valueID);
				case TYPE_INT -> values[len - 1] = intSource.getIntValue(valueID);
			}
			 
			average += values[len - 1]; 	
			
			average = average / ((double)valueCount);		//Divide by number of values included in calculation, instead of window length (len) which would give moving average values biased towards zero.
			
		} else {			//Faster calculation takes advantage of previously calculated average
		
			//No need to run through the whole array of values to update the average
			average *= len;
			average -= values[0];
			
			//Still update the values array (though not the average value)
			//Thus, values[0] is oldest value, values[values.length] is latest value
			if (len - 1 >= 0) System.arraycopy(values, 1, values, 0, len - 1);

			switch (type) {
				case TYPE_DBL -> values[len - 1] = dblSource.getDoubleValue(valueID);
				case TYPE_FLT -> values[len - 1] = fltSource.getFloatValue(valueID);
				case TYPE_LNG -> values[len - 1] = lngSource.getLongValue(valueID);
				case TYPE_INT -> values[len - 1] = intSource.getIntValue(valueID);
			}
			 
			average += values[len - 1]; 	
			
			average = average / ((double)len);
		}
	}

		
	/** Return the result of a given statistic.
			*  @param valueID One of the F_ constants representing available statistics.
			*  @return The computed value.
			*  @throws UnsupportedOperationException If the given valueID is not supported.*/
	public double getDoubleValue(Enum<?> valueID) {
		return average;
	}
		
	/**
	 * ISimEventListener callback function. It supports only jas.engine.Sim.EVENT_UPDATE event.
	 * @param type The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
	 * @throws UnsupportedOperationException If actionType is not supported.
	 */	
	@Override
	public void onEvent(Enum<?> type) {
		if (type.equals(CommonEventType.Update))
			updateSource();
		else
			throw new SimulationRuntimeException("The SimpleStatistics object does not support " + type + " operation.");
	}
	
}