package microsim.statistics.functions;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.FloatArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;

/**
 * This class computes the average of the last given number of values in an array taken from a data source. 
 * The mean function return always a double value, so it implements the 
 * <i>DoubleSource</i> interface and the standard  <i>DoubleSource</i> one.
 */
public class MovingAverageArrayFunction extends AbstractArrayFunction implements DoubleSource {

	protected double mean;
	protected int window;
	
	/** Create a count function on a float array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(FloatArraySource source, int window) {
		super(source);
		this.window = window;
	}

	/** Create a count function on an integer array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(IntArraySource source, int window) {
		super(source);
		this.window = window;
	}

	/** Create a count function on a long array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(LongArraySource source, int window) {
		super(source);
		this.window = window;
	}

	/** Create a count function on a double array source.
	 * @param source The data source.
	 */
	public MovingAverageArrayFunction(DoubleArraySource source, int window) {
		super(source);
		this.window = window;
	}
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = firstElement; i < data.length ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(float[])
	 */
	public void apply(float[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = firstElement; i < data.length ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = firstElement; i < data.length ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {
		int firstElement = data.length - window;
		if (firstElement < 0)
			firstElement = 0;
		double vals = window;
		if (data.length < window)
			vals = data.length;
		
		double sum = 0.0;
		for (int i = firstElement; i < data.length ; i++) {
			sum += data[i];
		}
		mean = sum / vals;
	}

	/* (non-Javadoc)
	 * @see jas.statistics.IDoubleSource#getDoubleValue(int)
	 */
	public double getDoubleValue(Enum<?> variableID) {
		return mean;
	}
	
}
