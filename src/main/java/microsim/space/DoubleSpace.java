package microsim.space;

import jamjam.Mean;
import jamjam.Sum;
import jamjam.Variance;
import lombok.NonNull;
import lombok.val;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.IntStream;

import static jamjam.Sum.broadcastAdd;

/**
 * A bi-dimensional grid containing double values.
 */
public class DoubleSpace extends AbstractSpace<Double> {
    private final int size;
    protected double[] m;

    /**
     * Creates a grid of given size.
     *
     * @param xSize The width of the grid.
     * @param ySize The height of the grid.
     */
    public DoubleSpace(final int xSize, final int ySize) {
        super(xSize, ySize);
        m = new double[xSize * ySize];
        size = xSize * ySize;
    }

    /**
     * Creates a copy of the given grid.
     *
     * @param grid The source grid.
     */
    public DoubleSpace(final @NonNull DoubleSpace grid) {
        super(grid.getXSize(), grid.getYSize());
        m = new double[xSize * ySize];
        size = xSize * ySize;
        for (int x = 0; x < xSize; x++)
            for (int y = 0; y < ySize; y++) this.setDbl(x, y, grid.getDbl(x, y));
    }

    protected int at(final int x, final int y) {
        return y * xSize + x;
    }

    /**
     * Return a Double object containing the value at given position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The Double wrapper for value stored at {@code x,y} position of the grid.
     */
    public @NonNull Double get(final int x, final int y) {
        return m[at(x, y)];
    }

    /**
     * Return the value at given position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The value stored at {@code x,y} position of the grid.
     */
    public double getDbl(final int x, final int y) {
        return m[at(x, y)];
    }

    /**
     * Set the given value at given position.
     *
     * @param x   The {@code x} coordinate.
     * @param y   The {@code y} coordinate.
     * @param obj An object wrapper for a number class. It is possible to pass Integer, Double, or Long values.
     */
    public void set(final int x, final int y, @Nullable Object obj) {
        if (obj == null) {
            m[at(x, y)] = 0;
            return;
        }

        if (!(obj instanceof Number)) throw new ClassCastException("Passed object is not a number of any kind.");

        m[at(x, y)] = ((Number) obj).doubleValue();
    }

    /**
     * Swap the content of the {@code (x1,y1)} and {@code (x2,y2)} cells of the grid.
     *
     * @param x1 The {@code x} coordinate for the first cell.
     * @param y1 The {@code y} coordinate for the first cell.
     * @param x2 The {@code x} coordinate for the second cell.
     * @param y2 The {@code y} coordinate for the second cell.
     */
    public void swapPositions(final int x1, final int y1, final int x2, final int y2) {
        val d = m[at(x1, y1)];
        m[at(x1, y1)] = m[at(x2, y2)];
        m[at(x2, y2)] = d;
    }

    /**
     * Set the given value at given position.
     *
     * @param x     The {@code x} coordinate.
     * @param y     The {@code y} coordinate.
     * @param value A double value to put at {@code x,y} position.
     */
    public void setDbl(final int x, final int y, final double value) {
        m[at(x, y)] = value;
    }

    /**
     * Return the size of the grid that is {@code width * height}.
     *
     * @return The number of cells in the grid.
     */
    public int size() {
        return size;
    }

    /**
     * Set all cells to 0.0 value.
     */
    public void clear() {
        Arrays.fill(m, 0.);
    }

    /**
     * Set all cells to the given value.
     *
     * @param initValue The value to put into each cell.
     */
    public void resetTo(final double initValue) {
        Arrays.fill(m, initValue);
    }

    /**
     * Sum the given value to the value of each cell.
     *
     * @param arg The value to be added.
     */
    public void add(final double arg) {
        broadcastAdd(m, arg);
    }

    /**
     * Multiply the given value to the value of each cell.
     *
     * @param arg The value to be multiplied.
     */
    public void multiply(final double arg) {
        IntStream.range(0, size).forEach(i -> m[i] *= arg);//todo improve
    }

    /**
     * Get the minimum value stored into the grid.
     *
     * @return The minimum value of the grid.
     */
    public double min() {
        double minimum = m[0];
        for (int i = 0; i < size; i++) if (m[i] < minimum) minimum = m[i];

        return minimum;
    }

    /**
     * Get the maximum value stored into the grid.
     *
     * @return The maximum value of the grid.
     */
    public double max() {
        double maximum = m[0];
        for (int i = 0; i < size; i++) if (m[i] > maximum) maximum = m[i];

        return maximum;
    }

    /**
     * Sum the value of each cell.
     *
     * @return The sum the value of each cell.
     */
    public double sum() {
        return Sum.sum(m);
    }

    /**
     * Compute the sample mean value of the values stored in the grid.
     *
     * @return The mean value.
     */
    public double mean() {
        return Mean.mean(m);
    }

    /**
     * Compute the sample variance value of the values stored in the grid.
     *
     * @return The variance value.
     */
    public double variance() {
        return Variance.unweightedBiasedVariance(m);
    }

    /**
     * Return the matrix of values representing the grid.
     *
     * @return A matrix of double with the same dimensions of the grid.
     */
    public double @NonNull [] getMatrix() {
        return m;
    }

    /**
     * Copies the given {@link DoubleSpace} content in this grid.
     *
     * @param dm The source {@link DoubleSpace} to be copied.
     */
    public void copyGridTo(final @NonNull DoubleSpace dm) {
        copyGridTo(dm.m);
    }

    /**
     * Copies the {@code double[]} matrix content in this grid.
     *
     * @param dm The source matrix to be copied.
     */
    public void copyGridTo(final double @NonNull [] dm) {
        System.arraycopy(m, 0, dm, 0, xSize * ySize);
    }

    /**
     * Return the value contained by the given cell cast to int.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The int value contained by the {@code (x,y)} cell.
     */
    public int countObjectsAt(final int x, final int y) {
        return (int) m[at(x, y)];
    }
}
