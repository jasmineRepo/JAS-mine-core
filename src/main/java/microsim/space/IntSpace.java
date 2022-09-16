package microsim.space;

import lombok.NonNull;
import lombok.val;
import microsim.engine.SimulationEngine;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * A bi-dimensional grid containing integer values.
 */
public class IntSpace extends AbstractSpace<Integer> {
    private final int size;
    protected int[] m;

    /**
     * Create a grid of given size.
     *
     * @param xSize The width of the grid.
     * @param ySize The height of the grid.
     */
    public IntSpace(final int xSize, final int ySize) {
        super(xSize, ySize);
        m = new int[xSize * ySize];
        size = xSize * ySize;
    }

    /**
     * Create a copy of the given grid.
     *
     * @param grid The source grid.
     */
    public IntSpace(final @NonNull IntSpace grid) {
        super(grid.getXSize(), grid.getYSize());
        m = new int[xSize * ySize];
        size = xSize * ySize;
        for (int x = 0; x < xSize; x++)
            for (int y = 0; y < ySize; y++) this.setInt(x, y, grid.getInt(x, y));
    }

    protected int at(final int x, final int y) {
        return y * xSize + x;
    }

    public void randomPopulate(final double splitProbability, final int probableValue, final int defaultValue) {
        for (int i = 0; i < getXSize(); i++)
            for (int j = 0; j < getYSize(); j++)
                setInt(i, j, SimulationEngine.getRnd().nextDouble() < splitProbability ? probableValue : defaultValue);
    }

    /**
     * Return an Integer object containing the value at given position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The Integer wrapper for value stored at {@code x,y} position of the grid.
     */
    public @NonNull Integer get(final int x, final int y) {
        return m[at(x, y)];
    }

    /**
     * Return the value at given position.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The value stored at {@code x,y} position of the grid.
     */
    public int getInt(final int x, final int y) {
        return m[at(x, y)];
    }

    /**
     * Set the given value at given position.
     *
     * @param x   The {@code x} coordinate. WARNING: No bounds checking for fast access.
     * @param y   The {@code y} coordinate. WARNING: No bounds checking for fast access.
     * @param obj An object wrapper for a number class. It is possible to pass Integer, Double, or Long values.
     */
    public void set(final int x, final int y, @Nullable Object obj) {
        if (obj == null) {
            m[at(x, y)] = 0;
            return;
        }

        if (!(obj instanceof Number)) throw new ClassCastException("Passed object is not a number of any kind.");

        m[at(x, y)] = ((Number) obj).intValue();
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
     * @param x     The {@code x} coordinate. WARNING: No bounds checking for fast access.
     * @param y     The {@code y} coordinate. WARNING: No bounds checking for fast access.
     * @param value An integer value to put at {@code x,y} position.
     */
    public void setInt(final int x, final int y, final int value) {
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
     * Set all cells to 0 value.
     */
    public void clear() {
        Arrays.fill(m, 0);
    }

    /**
     * Set all cells to the given value.
     *
     * @param initValue The value to put into each cell.
     */
    public void resetTo(final int initValue) {
        Arrays.fill(m, initValue);
    }

    /**
     * Sum the given value to the value of each cell.
     *
     * @param arg The value to be added.
     */
    public void add(final int arg) {
        IntStream.range(0, size).forEach(i -> m[i] += arg);
    }

    /**
     * Multiply the given value to the value of each cell.
     *
     * @param arg The value to be multiplied.
     */
    public void multiply(final int arg) {
        IntStream.range(0, size).forEach(i -> m[i] *= arg);
    }

    /**
     * Get the minimum value stored into the grid.
     *
     * @return The minimum value of the grid.
     */
    public int min() {
        int minimum = m[0];
        for (int i = 0; i < size; i++) if (m[i] < minimum) minimum = m[i];

        return minimum;
    }

    /**
     * Get the maximum value stored into the grid.
     *
     * @return The maximum value of the grid.
     */
    public int max() {
        int maximum = m[0];
        for (int i = 0; i < size; i++) if (m[i] > maximum) maximum = m[i];

        return maximum;
    }

    /**
     * Sum the value of each cell.
     *
     * @return The sum the value of each cell.
     */
    public int sum() {
        return Arrays.stream(m, 0, size).sum();
    }

    /**
     * Compute the sample mean value of the values stored in the grid.
     *
     * @return The mean value.
     */
    public double mean() {
        return (double) sum() / (xSize * ySize);
    }

    /**
     * Compute the sample variance value of the values stored in the grid.
     *
     * @return The variance value.
     */
    public double variance() { // fixme
        double s_squared = 0;
        double mn = mean();
        for (int row = 0; row < xSize; row++)
            for (int col = 0; col < ySize; col++) {
                double temp = m[at(row, col)] - mn;
                s_squared += (temp * temp);
            }

        return s_squared / (xSize * ySize);
    }

    /**
     * Return the matrix of values representing the grid.
     *
     * @return A matrix of integer with the same dimensions of the grid.
     */
    public int @NonNull [] getMatrix() {
        return m;
    }

    /**
     * Copies the given IntGrid content in this grid.
     *
     * @param dm The source IntGrid to be copied.
     */
    public void copyGridTo(final @NonNull IntSpace dm) {
        int[] aMatrix = new int[xSize * ySize];
        System.arraycopy(m, 0, aMatrix, 0, xSize * ySize);
        dm.m = aMatrix;
    }

    /**
     * Copies the int[] matrix content in this grid.
     *
     * @param dm The source matrix to be copied.
     */
    public void copyGridTo(final int @NonNull [] dm) {
        System.arraycopy(m, 0, dm, 0, xSize * ySize);
    }

    /**
     * Return the value contained by the given cell.
     *
     * @param x The {@code x} coordinate.
     * @param y The {@code y} coordinate.
     * @return The int value contained by the ({@code x,y}) cell.
     */
    public int countObjectsAt(final int x, final int y) {
        return m[at(x, y)];
    }
}
