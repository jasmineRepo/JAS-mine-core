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
	protected ILongSource lngSource;
	protected IIntSource intSource;
	protected IFloatSource fltSource;
	protected int type;
	protected Enum<?> valueID;
					
	protected int len = 0;
	protected double[] values;
	protected double average;
	
	/** Create a basic statistic probe on a IDblSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The IDblSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(IDoubleSource source, Enum<?> valueID, int windowSize) {
		super();
		dblSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}

	/** Create a basic statistic probe on a IDblSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The IDblSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(IFloatSource source, Enum<?> valueID, int windowSize) {
		super();
		fltSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}
	
	/** Create a basic statistic probe on a IDblSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The IDblSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(ILongSource source, Enum<?> valueID, int windowSize) {
		super();
		lngSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}
	
	/** Create a basic statistic probe on a IDblSource object.
	 *  @param name Name of the statistic object.
	 *  @param source The IDblSource object.
	 *  @param valueID The value identifier defined by source object. */
	public MovingAverageTraceFunction(IIntSource source, Enum<?> valueID, int windowSize) {
		super();
		intSource = source;
		len = windowSize;
		this.valueID = valueID;
		values = new double[len];
	}
				
	/** Collect a value from the source. */
	public void applyFunction() 
	{ 
		int ln = values.length - 1;
		for (int i = 0; i > ln; i++)
			values[i] = values[i + 1];
		
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
		 
		for (int i = 0; i < values.length; i++) {
			average += values[i]; 	
		}
		average = average / (double) len;
		
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