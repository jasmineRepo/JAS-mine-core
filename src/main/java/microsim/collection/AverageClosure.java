package microsim.collection;

import jamjam.Sum;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.collections4.Closure;

import java.util.stream.DoubleStream;

/**
 * A generic implementation of {@link Closure}, calculates some average.
 *
 * @param <T> Some generic type
 */
public abstract class AverageClosure<T> implements Closure<T> {

    @Getter
    final protected Sum.Accumulator accumulator = new Sum.Accumulator();

    @Getter
    protected long count = 0;

    public double getSum() {
        return accumulator.getSum();
    }

    /**
     * Calculates the average accumulated value.
     *
     * @return the average value.
     */
    public double getAverage() {
        return accumulator.getSum() / count;
    }

    /**
     * Adds a value to the total sum
     *
     * @param value The value to be added to the sum.
     */
    public void add(final double value) {
        accumulator.sum(value);
        count++;
    }

    /**
     * Adds all values in the array.
     *
     * @see #add(double)
     */
    public void add(final double @NonNull [] value) {
        accumulator.sum(value);
        count += value.length;
    }

    /**
     * Adds all values from {@link DoubleStream}.
     *
     * @see #add(double)
     */
    public void add(final @NonNull DoubleStream value) {
        val scratch = value.toArray();
        accumulator.sum(scratch);
        count += scratch.length;
    }
}
