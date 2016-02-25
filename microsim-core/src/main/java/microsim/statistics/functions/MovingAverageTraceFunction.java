package microsim.statistics.functions;

import microsim.event.CommonEventType;
import microsim.exception.SimulationRuntimeException;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IFloatSource;
import microsim.statistics.IIntSource;
import microsim.statistics.ILongSource;

/**
 *  This class computes the average of the last values collected from a data source.
 * The number of values used to compute the average value is specified in the constructor. 
 * The mean function return always double values, so it implements only the 
 * <i>IDoubleSource</i> interface.
 *
 * <p>Title: JAS</p>
 * <p>Description: Java Agent-based Simulation library</p>
 * <p>Copyright (C) 2002 Michele Sonnessa</p>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 * <p>
 */
public class MovingAverageTraceFunction extends AbstractFunction implements IDoubleSource  {
			
	protected static final int TYPE_DBL = 0;
	protected static final int TYPE_FLT = 1;
	protected static final int TYPE_INT = 2;
	protected static final int TYPE_LNG = 3;

	protected IDoubleSource dblSource;
	protected IFloatSource fltSource;
	protected IIntSource intSource;
	protected ILongSource lngSource;

	protected int type;
	protected Enum<?> valueID;
					
	protected int len = 0;
	protected double[] values;
	protected double average;
	
	protected int valueCount = 0;
	
	/** Create a basic statistic probe on a IDoubleSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The IDoubleSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(IDoubleSource source, Enum<?> valueID, int windowSize) {
		super();
		dblSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}

	/** Create a basic statistic probe on a IFloatSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The IFloatSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(IFloatSource source, Enum<?> valueID, int windowSize) {
		super();
		fltSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}
	
	/** Create a basic statistic probe on a ILongSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The ILongSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(ILongSource source, Enum<?> valueID, int windowSize) {
		super();
		lngSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}
	
	/** Create a basic statistic probe on a IIntSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The IIntSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(IIntSource source, Enum<?> valueID, int windowSize) {
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
			
			switch(type)
			{
				case TYPE_DBL:
					values[len - 1] = dblSource.getDoubleValue(valueID); break;
				case TYPE_FLT:
					values[len - 1] = fltSource.getFloatValue(valueID); break;
				case TYPE_LNG:
					values[len - 1] = lngSource.getLongValue(valueID); break;
				case TYPE_INT:
					values[len - 1] = intSource.getIntValue(valueID); break;				
			}
			 
			average += values[len - 1]; 	
			
			average = average / ((double)valueCount);		//Divide by number of values included in calculation, instead of window length (len) which would give moving average values biased towards zero.
			
		} else {			//Faster calculation takes advantage of previously calculated average
		
			//No need to run through the whole array of values to update the average
			average *= (double)len;
			average -= values[0];
			
			//Still update the values array (though not the average value)
			for (int i = 0; i < len - 1; i++) {
				values[i] = values[i + 1];			//Thus, values[0] is oldest value, values[values.length] is latest value
			}
			
			switch(type)
			{
				case TYPE_DBL:
					values[len - 1] = dblSource.getDoubleValue(valueID); break;
				case TYPE_FLT:
					values[len - 1] = fltSource.getFloatValue(valueID); break;
				case TYPE_LNG:
					values[len - 1] = lngSource.getLongValue(valueID); break;
				case TYPE_INT:
					values[len - 1] = intSource.getIntValue(valueID); break;				
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
	 * @param actionType The action id. Only jas.engine.Sim.EVENT_UPDATE is supported.
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