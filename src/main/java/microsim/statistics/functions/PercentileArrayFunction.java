package microsim.statistics.functions;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import microsim.statistics.DoubleArraySource;
import microsim.statistics.DoubleSource;
import microsim.statistics.FloatArraySource;
import microsim.statistics.IntArraySource;
import microsim.statistics.LongArraySource;

/**
 * This function calculates percentiles (p1,p5,p10-p90,p95,p99) for a given cross-section of data.
 * Input currently must be doubleArray double[].
 */

public class PercentileArrayFunction extends AbstractArrayFunction implements DoubleSource {
	
	public enum Variables {
		/**	Represent the function arguments for the getDoubleValue method. */
		P1,
		P5,
		P10,
		P20,
		P30,
		P40,
		P50,
		P60,
		P70,
		P80,
		P90,
		P95,
		P99;
	}
	
	/** Create a percentile function on a float array source.
	 * @param source The data source.
	 */
	public PercentileArrayFunction(FloatArraySource source) {
		super(source);
	}

	/** Create a percentile function on an integer array source.
	 * @param source The data source.
	 */
	public PercentileArrayFunction(IntArraySource source) {
		super(source);
	}

	/** Create a percentile function on a long array source.
	 * @param source The data source.
	 */
	public PercentileArrayFunction(LongArraySource source) {
		super(source);
	}

	/** Create a percentile function on a double array source.
	 * @param source The data source.
	 */
	public PercentileArrayFunction(DoubleArraySource source) {
		super(source);
	}

	protected double p1, p5, p10, p20, p30, p40, p50, p60, p70, p80, p90, p95, p99;
	
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(double[])
	 */
	public void apply(double[] data) {
		DescriptiveStatistics stats = new DescriptiveStatistics(data);
		
		p1 = stats.getPercentile(1);
		p5 = stats.getPercentile(5);
		p10 = stats.getPercentile(10);
		p20 = stats.getPercentile(20);
		p30 = stats.getPercentile(30);
		p40 = stats.getPercentile(40);
		p50 = stats.getPercentile(50);
		p60 = stats.getPercentile(60);
		p70 = stats.getPercentile(70);
		p80 = stats.getPercentile(80);
		p90 = stats.getPercentile(90);
		p95 = stats.getPercentile(95);
		p99 = stats.getPercentile(99);

	}
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(float[])
	 */
	public void apply(float[] data) {
		
	}
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(int[])
	 */
	public void apply(int[] data) {

	}
	
	/* (non-Javadoc)
	 * @see jas.statistics.functions.IArrayFunction#apply(long[])
	 */
	public void apply(long[] data) {
		
	}
	
	public double getDoubleValue(Enum<?> variableID) {
		return switch ((Variables) variableID) {
			case P1 -> p1;
			case P5 -> p5;
			case P10 -> p10;
			case P20 -> p20;
			case P30 -> p30;
			case P40 -> p40;
			case P50 -> p50;
			case P60 -> p60;
			case P70 -> p70;
			case P80 -> p80;
			case P90 -> p90;
			case P95 -> p95;
			case P99 -> p99;
			default ->
					throw new UnsupportedOperationException("The function result with id " + variableID + " is not supported.");
		};
	}
}
