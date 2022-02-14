package microsim.collection;

import org.apache.commons.collections4.Closure;

public abstract class AverageClosure<T> implements Closure<T> {

	protected double sum = 0.0;

	protected int count = 0;
	
	public double getAverage() {
		return sum / (double) count;
	}
	
	public double getSum() {
		return sum;
	}
	
	public int getCount() {
		return count;
	}

	public void add(double value) {
		sum += value;
		count++;
	}
}
