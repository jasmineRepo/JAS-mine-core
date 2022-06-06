package microsim.collection;

import lombok.Getter;
import org.apache.commons.collections4.Closure;

public abstract class AverageClosure<T> implements Closure<T> {

	@Getter protected double sum = 0.0;

	@Getter protected int count = 0;
	
	public double getAverage() {
		return sum / (double) count;
	}

	public void add(double value) {
		sum += value;
		count++;// fixme redesign - bad sum
	}
}
