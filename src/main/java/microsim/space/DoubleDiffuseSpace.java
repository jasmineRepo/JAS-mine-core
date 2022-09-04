package microsim.space;

import java.util.Arrays;

import microsim.event.EventListener;
import microsim.exception.SimulationRuntimeException;

/**
 * Discrete 2nd order approximation of 2d diffusion with evaporation. Essentialy
 * a java implementation of Diffuse2d in the <a
 * href="http://www.santafe.edu/projects/swarm"> Swarm</a> simulation toolkit.
 * Toroidal in shape and works with number values. This space simulates
 * concurency through the use of a read and write matrix. Any writes to the
 * space, write to the write matrix, and any reads to the read matrix. The
 * diffuse() method then diffuses the write matrix and copies the new values
 * into the read matrix.
 * <p>
 *
 * For an example of a DblDiffuseTorusGrid space see the heatBugs example. See
 * {@link #diffuse() diffuse} for a brief explanation of the diffusion
 * algorithm. <b>Note that this space doesn't seem to work correctly when run
 * with hotspot 1.01</b>
 *
 * @author Nick Collier
 * @version $Revision: 1.2 $ $Date: 2004/08/11 09:05:30 $
 */

public class DoubleDiffuseSpace extends DoubleSpace implements EventListener {

	public enum Verbs {
		Diffuse, Update;
	};

	public static final int EVENT_DIFFUSE = 9999;

	public static final int VON_NEUMANN = 0;
	public static final int MOORE = 1;

	public static final long MAX = 0x7FFF;

	private double diffCon;
	private double evapRate;
	private DoubleSpace readMatrix;
	// private DblGrid writeMatrix;

	private int x, prevX, nextX;
	private int y, prevY, nextY;

	/**
	 * Constructs a DblDiffuseTorusGrid space with the specificed dimensions
	 *
	 * @param xSize
	 *            size of the x dimension
	 * @param ySize
	 *            size of the y dimension
	 */
	public DoubleDiffuseSpace(int xSize, int ySize) {
		this(xSize, ySize, 1.0, 1.0);
	}

	/**
	 * Constructs a DblDiffuseTorusGrid space with the specified diffusion
	 * constant, evaporation rate, and dimensions
	 *
	 * @param diffusionConstant
	 *            the diffusion constant
	 * @param evaporationRate
	 *            the evaporation rate
	 * @param xSize
	 *            size of the x dimension
	 * @param ySize
	 *            size of the y dimension
	 */
	public DoubleDiffuseSpace(int xSize, int ySize, double diffusionConstant,
			double evaporationRate) {
		super(xSize, ySize);
		diffCon = diffusionConstant;
		evapRate = evaporationRate;
		readMatrix = new DoubleSpace(xSize, ySize);
		// writeMatrix = new DblGrid(xSize, ySize);
	}

	/**
	 * Sets the diffusion constant for this DblDiffuseTorusGrid space
	 */
	public void setDiffusionConstant(double diffusionConstant) {
		diffCon = diffusionConstant;
	}

	/**
	 * Sets the evaporation rate for this DblDiffuseTorusGrid space
	 */
	public void setEvaporationRate(double rate) {
		evapRate = rate;
	}

	private void computeRow() {
		int endX = xSize - 1;
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
		long val = (long) readMatrix.getDbl(x, y);
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

		double delta = (double) sum / 20.0;

		double d = val + delta * diffCon;
		d *= evapRate;

		// do the rounding a la Swarm Diffuse2d code.
		long newState = d < 0 ? 0L : d >= MAX ? MAX : (long) d;

		// writeMatrix.setDbl(x, y, newState);
		setDbl(x, y, newState);
	}

	/**
	 * Runs the diffusion with the current rates and values. Following the Swarm
	 * class, it is roughly newValue = evap(ownValue + diffusionConstant *
	 * (nghAvg - ownValue)) where nghAvg is the weighted average of a cells
	 * eight neighbors, and ownValue is the current value for the current cell.
	 * <p>
	 *
	 * Values from the readMatrix are used to calculate diffusion. This value is
	 * then written to the writeMatrix. When this has been done for every cell
	 * in the grid, the writeMatrix is copied to the readMatrix.
	 */
	public void diffuse() {
		int endY = ySize - 1;

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

		// copies the writeMatrix into the readMatrix
		// writeMatrix.copyGridTo(m);
		copyGridTo(readMatrix);
	}

	/**
	 * Copies the writeLattice to the readLattice
	 */
	public void update() {
		// writeMatrix.copyGridTo(m);
		copyGridTo(readMatrix);
		// print(writeMatrix.getMatrix());
		// print(m);
	}

	public void print(double[] mtr) {
		for (int y = 0; y < getYSize(); y++) {
			for (int x = 0; x < getXSize(); x++)
				System.out.print(mtr[at(x, y)] + " ");
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * Gets the von Neumann neighbors of the specified coordinate. doubles are
	 * returned in west, east, north, south order. The double at x, y is not
	 * returned.
	 *
	 * @param x
	 *            the x coordinate of the object
	 * @param y
	 *            the y coordinate of the object
	 * @return an array of doubles in west, east, north, south order
	 */
	public double[] getVonNeumannNeighbors(int x, int y) {
		return getVonNeumannNeighbors(x, y, 1, 1);
	}

	/**
	 * Gets the extended von Neumann neighbors of the specified coordinate. The
	 * extension in the x and y direction are specified by xExtent and yExtent.
	 * doubles are returned in west, east, north, south order with the most
	 * distant object first. The double at x,y is not returned.
	 *
	 * @param x
	 *            the x coordinate of the object
	 * @param y
	 *            the y coordinate of the object
	 * @param xExtent
	 *            the extension of the neighborhood in the x direction
	 * @param yExtent
	 *            the extension of the neighborhood in the y direction
	 * @return an array of doubles in west, east, north, south order with the
	 *         most distant object first.
	 */

	public double[] getVonNeumannNeighbors(int x, int y, int xExtent,
			int yExtent) {
		double[] array = new double[(xExtent * 2) + (yExtent * 2)];
		int index = 0;

		int normX = boundX(x);
		int normY = boundY(y);

		for (int i = x - xExtent; i < x; i++)
			array[index++] = readMatrix.getDbl(i, normY);

		for (int i = x + xExtent; i > x; i--)
			array[index++] = readMatrix.getDbl(i, normY);

		for (int i = y - yExtent; i < y; i++)
			array[index++] = readMatrix.getDbl(normX, i);

		for (int i = y + yExtent; i > y; i--)
			array[index++] = readMatrix.getDbl(normX, i);

		return array;
	}

	/**
	 * Gets the Moore neighbors of the specified coordinate. doubles are
	 * returned by row starting with the "NW corner" and ending with the
	 * "SE corner." The double at x, y is not returned.
	 *
	 * @param x
	 *            the x coordinate of the object
	 * @param y
	 *            the y coordinate of the object
	 * @return an array of doubles ordered by row starting with the "NW corner"
	 *         and ending with the "SE corner."
	 */

	public double[] getMooreNeighbors(int x, int y) {
		return getMooreNeighbors(x, y, 1, 1);
	}

	/**
	 * Gets the extended Moore neighbors of the specified coordinate. The
	 * extension in the x and y direction are specified by xExtent and yExtent.
	 * doubles are returned by row starting with the "NW corner" and ending with
	 * the "SE corner." The double at x,y is not returned.
	 *
	 * @param x
	 *            the x coordinate of the object
	 * @param y
	 *            the y coordinate of the object
	 * @param xExtent
	 *            the extension of the neighborhood in the x direction
	 * @param yExtent
	 *            the extension of the neighborhood in the y direction
	 * @return an array of doubles ordered by row starting with the "NW corner"
	 *         and ending with the "SE corner."
	 */

	public double[] getMooreNeighbors(int x, int y, int xExtent, int yExtent) {
		double[] array = new double[xExtent * yExtent * 4 + (xExtent * 2)
				+ (yExtent * 2)];
		int index = 0;

		for (int j = y - yExtent; j <= y + yExtent; j++)
			for (int i = x - xExtent; i <= x + xExtent; i++)
				if (!(j == y && i == x))
					array[index++] = readMatrix.getDbl(boundX(i), boundY(j));

		return array;
	}

	/**
	 * Finds the maximum grid cell value within a specified range from the
	 * specified origin coordinate.
	 *
	 * @param x
	 *            the x origin coordinate
	 * @param y
	 *            the y origin coordinate
	 * @param range
	 *            the range out from the coordinate to search
	 * @param includeOrigin
	 *            include object at origin in search
	 * @param neighborhoodType
	 *            the type of neighborhood to search. Can be one of
	 *            Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
	 * @return the Objects determined to be the maximum.
	 */
	public double[] findMaximum(int x, int y, int range, boolean includeOrigin,
			int neighborhoodType) {
		double[] dArray;

		if (neighborhoodType == VON_NEUMANN)
			dArray = this.getVonNeumannNeighbors(x, y, range, range);
		else
			dArray = this.getMooreNeighbors(x, y, range, range);

		// need to extend the array here
		if (includeOrigin) {
			double[] newArray = new double[dArray.length + 1];
			System.arraycopy(dArray, 0, newArray, 0, dArray.length);
			newArray[newArray.length - 1] = readMatrix.getDbl(x, y);
			dArray = newArray;
		}

		return compareMax(dArray);
	}

	// need better algorithm for this
	private double[] compareMax(double[] array) {
		if (array.length > 0) {
			Arrays.sort(array);
			int endIndex = array.length - 1;
			double max = array[endIndex];
			double val = array[endIndex - 1];

			int index = 1;

			while (max == val && index < array.length) {
				index++;
				val = endIndex - index;
			}

			double[] retVal = new double[index];
			System.arraycopy(array, array.length - index, retVal, 0, index);
			return retVal;
		}

		return new double[0];
	}

	/**
	 * Finds the minimum grid cell value within a specified range from the
	 * specified origin coordinate.
	 *
	 * @param x
	 *            the x origin coordinate
	 * @param y
	 *            the y origin coordinate
	 * @param range
	 *            the range out from the coordinate to search
	 * @param includeOrigin
	 *            include object at origin in search
	 * @param neighborhoodType
	 *            the type of neighborhood to search. Can be one of
	 *            Discrete2DSpace.VON_NEUMANN or Discrete2DSpace.MOORE.
	 * @return the Objects determined to be the maximum.
	 */
	public double[] findMinimum(int x, int y, int range, boolean includeOrigin,
			int neighborhoodType) {
		double[] dArray;

		if (neighborhoodType == VON_NEUMANN)
			dArray = this.getVonNeumannNeighbors(x, y, range, range);
		else
			dArray = this.getMooreNeighbors(x, y, range, range);

		// need to extend the array here
		if (includeOrigin) {
			double[] newArray = new double[dArray.length + 1];
			System.arraycopy(dArray, 0, newArray, 0, dArray.length);
			newArray[newArray.length - 1] = readMatrix.getDbl(x, y);
			dArray = newArray;
		}

		return compareMin(dArray);
	}

	private double[] compareMin(double[] array) {
		if (array.length > 0) {
			Arrays.sort(array);
			double max = array[0];
			double val = array[1];
			int endIndex = array.length - 1;

			int index = 1;
			while (max == val) {
				index++;
				if (index <= endIndex)
					val = array[index];
				else
					break;
			}

			double[] retVal = new double[index];
			System.arraycopy(array, 0, retVal, 0, index);
			return retVal;
		}

		return new double[0];
	}

	public double[] getMatrix() {
		return readMatrix.getMatrix();
	}

	public void onEvent(Enum<?> type) {
		switch ((Verbs) type) {
		case Diffuse:
			diffuse();
			break;
		case Update:
			update();
			break;
		default:
			throw new SimulationRuntimeException(
					"The DblDiffuseGrid supports only Sim.EVENT_UPDATE and EVENT_DIFFUSE operations");
		}

	}
}
