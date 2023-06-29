package microsim.space;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import microsim.event.EventListener;
import microsim.exception.SimulationRuntimeException;

import java.util.Arrays;

import static microsim.space.DoubleDiffuseSpace.Verbs.Diffuse;
import static microsim.space.DoubleDiffuseSpace.Verbs.Update;

/**
 * Discrete 2nd order approximation of 2d diffusion with evaporation.
 */
public class DoubleDiffuseSpace extends DoubleSpace implements EventListener {

    public static final long MAX = 0x7FFF;
    private final DoubleSpace readMatrix;
    @Setter
    private double diffusionConstant;
    @Setter
    private double evaporationRate;
    private int x, prevX, nextX;
    private int y, prevY, nextY;

    /**
     * Constructs a space with the specified dimensions.
     *
     * @param xSize The size of the {@code x} dimension.
     * @param ySize The size of the {@code y} dimension.
     */
    public DoubleDiffuseSpace(final int xSize, final int ySize) {
        this(xSize, ySize, 1.0, 1.0);
    }

    /**
     * Constructs a space with the specified diffusion constant, evaporation rate, and dimensions.
     *
     * @param diffusionConstant The diffusion constant.
     * @param evaporationRate   The evaporation rate.
     * @param xSize             The size of the {@code x} dimension.
     * @param ySize             The size of the {@code y} dimension.
     */
    public DoubleDiffuseSpace(final int xSize, final int ySize, final double diffusionConstant,
                              final double evaporationRate) {
        super(xSize, ySize);
        this.diffusionConstant = diffusionConstant;
        this.evaporationRate = evaporationRate;
        readMatrix = new DoubleSpace(xSize, ySize);
    }

    private void computeRow() {
        val endX = xSize - 1;
        prevX = endX;
        x = 0;

        while (x < endX) {
            nextX = x + 1;
            computeVal();

            prevX = x;
            x = nextX;
        }
        nextX = 0;
        computeVal();
    }

    private void computeVal() {
        val val = (long) readMatrix.getDbl(x, y);
        long sum = 0;
        sum += (long) readMatrix.getDbl(prevX, prevY);
        sum += 4 * (long) readMatrix.getDbl(x, prevY);
        sum += (long) readMatrix.getDbl(nextX, prevY);
        sum += 4 * (long) readMatrix.getDbl(prevX, y);
        sum += 4 * (long) readMatrix.getDbl(nextX, y);
        sum += (long) readMatrix.getDbl(prevX, nextY);
        sum += 4 * (long) readMatrix.getDbl(x, nextY);
        sum += (long) readMatrix.getDbl(nextX, nextY);
        sum -= 20 * val;

        val delta = (double) sum / 20.0;

        double d = val + delta * diffusionConstant;
        d *= evaporationRate;

        val newState = d < 0 ? 0L : d >= MAX ? MAX : (long) d;
        setDbl(x, y, newState);
    }

    /**
     * Runs the diffusion with the current rates and values. Roughly follows this:
     * {@code newValue = evap(ownValue + diffusionConstant * (nghAvg - ownValue))} where {@code nghAvg} is the weighted
     * average of a cells eight neighbors, and ownValue is the current value for the current cell.
     */
    public void diffuse() {
        val endY = ySize - 1;

        prevY = endY;
        y = 0;
        while (y < endY) {
            nextY = y + 1;

            computeRow();
            prevY = y;
            y = nextY;
        }
        nextY = 0;
        computeRow();
        copyGridTo(readMatrix);
    }

    /**
     * Copies the writeMatrix to the readMatrix
     */
    public void update() {
        copyGridTo(readMatrix);
    }

    public void print(final double @NonNull [] mtr) {
        for (int y = 0; y < getYSize(); y++) {
            for (int x = 0; x < getXSize(); x++)
                System.out.print(mtr[at(x, y)] + " ");
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Gets the von Neumann neighbors of the specified coordinate. Doubles are returned in west, east, north, south
     * order. The double at {@code (x,y)} is not returned.
     *
     * @param x The {@code x} coordinate of the object.
     * @param y The {@code y} coordinate of the object.
     * @return an array of doubles in west, east, north, south order.
     */
    public double @NonNull [] getVonNeumannNeighbors(final int x, final int y) {
        return getVonNeumannNeighbors(x, y, 1, 1);
    }

    /**
     * Gets the extended von Neumann neighbors of the specified coordinate. The extension in the {@code x} and {@code y}
     * direction are specified by {@code xExtent} and {@code yExtent}. Doubles are returned in west, east, north, south
     * order with the most distant object first. The double at {@code (x,y)} is not returned.
     *
     * @param x       The {@code x} coordinate of the object.
     * @param y       The {@code y} coordinate of the object.
     * @param xExtent The extension of the neighborhood in the {@code x} direction.
     * @param yExtent The extension of the neighborhood in the {@code y} direction.
     * @return an array of doubles in west, east, north, south order with the most distant object first.
     */
    public double @NonNull [] getVonNeumannNeighbors(final int x, final int y, final int xExtent, final int yExtent) {
        val array = new double[(xExtent * 2) + (yExtent * 2)];
        int index = 0;

        val normX = boundX(x);
        val normY = boundY(y);

        for (int i = x - xExtent; i < x; i++) array[index++] = readMatrix.getDbl(i, normY);
        for (int i = x + xExtent; i > x; i--) array[index++] = readMatrix.getDbl(i, normY);
        for (int i = y - yExtent; i < y; i++) array[index++] = readMatrix.getDbl(normX, i);
        for (int i = y + yExtent; i > y; i--) array[index++] = readMatrix.getDbl(normX, i);

        return array;
    }

    /**
     * Gets the Moore neighbors of the specified coordinate. Doubles are returned by row starting with the "NW corner"
     * and ending with the "SE corner." The double at {@code (x,y)} is not returned.
     *
     * @param x The {@code x} coordinate of the object.
     * @param y The {@code y} coordinate of the object.
     * @return an array of doubles ordered by row starting with the "NW corner" and ending with the "SE corner".
     */
    public double @NonNull [] getMooreNeighbors(final int x, final int y) {
        return getMooreNeighbors(x, y, 1, 1);
    }

    /**
     * Gets the extended Moore neighbors of the specified coordinate. The extension in the {@code x} and {@code y}
     * direction are specified by {@code xExtent} and {@code yExtent}. Doubles are returned by row starting with the
     * "NW corner" and ending with the "SE corner". The double at {@code (x,y)} is not returned.
     *
     * @param x       The {@code x} coordinate of the object.
     * @param y       The {@code y} coordinate of the object.
     * @param xExtent The extension of the neighborhood in the {@code x} direction.
     * @param yExtent The extension of the neighborhood in the {@code y} direction.
     * @return an array of doubles ordered by row starting with the "NW corner" and ending with the "SE corner".
     */
    public double @NonNull [] getMooreNeighbors(final int x, final int y, final int xExtent, final int yExtent) {
        val array = new double[xExtent * yExtent * 4 + (xExtent * 2) + (yExtent * 2)];
        int index = 0;

        for (int j = y - yExtent; j <= y + yExtent; j++)
            for (int i = x - xExtent; i <= x + xExtent; i++)
                if (!(j == y && i == x)) array[index++] = readMatrix.getDbl(boundX(i), boundY(j));

        return array;
    }

    /**
     * Finds the maximum grid cell value within a specified range from the specified origin coordinate.
     *
     * @param x             The {@code x} origin coordinate.
     * @param y             The {@code y} origin coordinate.
     * @param range         The range out from the coordinate to search.
     * @param includeOrigin Include object at origin in search.
     * @param nt            The type of neighborhood to search. Can be one of {@link NEIGHBOURHOOD_TYPE#VON_NEUMANN}
     *                      or {@link NEIGHBOURHOOD_TYPE#MOORE}.
     * @return the Objects determined to be the maximum.
     */
    public double[] findMaximum(final int x, final int y, final int range, final boolean includeOrigin,
                                final @NonNull NEIGHBOURHOOD_TYPE nt) {
        return compareMax(getNeighbours(x, y, range, includeOrigin, nt));
    }

    /**
     * Finds the minimum grid cell value within a specified range from the specified origin coordinate.
     *
     * @param x             The x origin coordinate.
     * @param y             The y origin coordinate.
     * @param range         The range out from the coordinate to search.
     * @param includeOrigin T include object at origin in search.
     * @param nt            The type of neighborhood to search. Can be one of {@link NEIGHBOURHOOD_TYPE#VON_NEUMANN}
     *                      or {@link NEIGHBOURHOOD_TYPE#MOORE}.
     * @return the Objects determined to be the maximum.
     */
    public double @NonNull [] findMinimum(final int x, final int y, final int range, final boolean includeOrigin,
                                          final @NonNull NEIGHBOURHOOD_TYPE nt) {
        return compareMin(getNeighbours(x, y, range, includeOrigin, nt));
    }

    private double @NonNull [] getNeighbours(final int x, final int y, final int range, final boolean includeOrigin,
                                             final @NonNull NEIGHBOURHOOD_TYPE nt) {
        var dArray = switch (nt) {
            case VON_NEUMANN -> this.getVonNeumannNeighbors(x, y, range, range);
            case MOORE -> this.getMooreNeighbors(x, y, range, range);
        };

        if (includeOrigin) {
            val newArray = new double[dArray.length + 1];
            System.arraycopy(dArray, 0, newArray, 0, dArray.length);
            newArray[newArray.length - 1] = readMatrix.getDbl(x, y);
            dArray = newArray;
        }

        return dArray;
    }

    private double @NonNull [] compareMax(final double @NonNull [] array) {
        if (array.length > 0) {
            Arrays.sort(array);
            val endIndex = array.length - 1;
            val max = array[endIndex];
            double val = array[endIndex - 1];

            int index = 1;

            while (max == val && index < array.length) {
                index++;
                val = endIndex - index;
            }

            val retVal = new double[index];
            System.arraycopy(array, array.length - index, retVal, 0, index);
            return retVal;
        }

        return new double[0];
    }

    private double @NonNull [] compareMin(final double @NonNull [] array) {
        if (array.length > 0) {
            Arrays.sort(array);
            val max = array[0];
            double val = array[1];
            val endIndex = array.length - 1;

            int index = 1;
            while (max == val) {
                index++;
                if (index <= endIndex) val = array[index];
                else break;
            }

            val retVal = new double[index];
            System.arraycopy(array, 0, retVal, 0, index);
            return retVal;
        }

        return new double[0];
    }

    public double @NonNull [] getMatrix() {
        return readMatrix.getMatrix();
    }

    public void onEvent(final @NonNull Enum<?> type) {
        switch ((Verbs) type) {
            case Diffuse -> diffuse();
            case Update -> update();
            default -> throw new SimulationRuntimeException(
                "The %s supports only %s and %s operations".formatted(getClass().getName(), Update, Diffuse));
        }
    }

    public enum NEIGHBOURHOOD_TYPE {
        VON_NEUMANN(0), MOORE(1);

        @Getter
        private final int numericalValue;

        NEIGHBOURHOOD_TYPE(final int numericalValue) {
            this.numericalValue = numericalValue;
        }
    }

    public enum Verbs {
        Diffuse, Update
    }
}
