package microsim.space;

/**
 * An abstract class representing bidimensional grid container. The different
 * implementations of this class will manage the type of the storable values. It
 * extends the standard jdk java.util.AbstractCollection to give each grid
 * implementation a collection behaviour. So each grid can be iterated like an
 * ArrayList. The specific GridIterator has been implemented to iterate elements
 * storing their position on the grid.
 *
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002 Michele Sonnessa
 * </p>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 *         <p>
 */
public abstract class AbstractSpace<E> {
	protected int xSize, ySize;
	protected transient int modCount = 0;

	/**
	 * Create a grid of given size.
	 *
	 * @param xSize
	 *            The width of the grid.
	 * @param ySize
	 *            The height of the grid.
	 */
	public AbstractSpace(int xSize, int ySize) {
		if (xSize <= 0 || ySize <= 0)
			throw new IllegalArgumentException(
					"Grid must have positive dimensions");

		this.xSize = xSize;
		this.ySize = ySize;
	}

	/**
	 * Return the object contained at given position. The type depends on the
	 * specific implementation of the Grid.
	 *
	 * @param x
	 *            The x coordinate. WARNING: No bounds checking for fast access.
	 * @param y
	 *            The y coordinate. WARNING: No bounds checking for fast access.
	 * @return The Double wrapper for value stored at x,y position of the grid.
	 */
	public abstract E get(int x, int y);

	/**
	 * Set the given Object at given position.
	 *
	 * @param x
	 *            The x coordinate. WARNING: No bounds checking for fast access.
	 * @param y
	 *            The y coordinate. WARNING: No bounds checking for fast access.
	 * @param obj
	 *            An object to be stored. The type depends on the specific
	 *            implementation of the Grid.
	 */
	public abstract void set(int x, int y, Object obj);

	/**
	 * Swap the content of the (x1, y1) and (x2, y2) cells of the grid.
	 *
	 * @param x1
	 *            The x coordinate for the first cell.
	 * @param y1
	 *            The y coordinate for the first cell.
	 * @param x2
	 *            The x coordinate for the second cell.
	 * @param y2
	 *            The y coordinate for the second cell.
	 */
	public abstract void swapPositions(int x1, int y1, int x2, int y2);

	/**
	 * Return the width of the grid.
	 *
	 * @return The width.
	 */
	public int getXSize() {
		return xSize;
	}

	/**
	 * Return the height of the grid.
	 *
	 * @return The height.
	 */
	public int getYSize() {
		return ySize;
	}

	/**
	 * Check the given x coordinate. If it is out of grid bounds the returning
	 * value is truncated to the bound. For instance, if the grid is (100, 100)
	 * the instruction <i>boundX(130)</i> returns 100.
	 *
	 * @param x
	 *            The x coordinate to be tested.
	 * @return A safe value representing the x value if inbound, the bound
	 *         otherwise.
	 */
	public int boundX(int x) {
		if (x < 0)
			return 0;
		if (x >= xSize)
			return xSize - 1;
		return x;
	}

	/**
	 * Gets the extended Moore neighbors of the specified coordinate. Points are
	 * returned by row starting with the "NW corner" and ending with the
	 * "SE corner." The Point at x,y is not returned.
	 *
	 * @param x
	 *            the x coordinate of the object
	 * @param y
	 *            the y coordinate of the object
	 * @return an array of Point ordered by row starting with the "NW corner"
	 *         and ending with the "SE corner."
	 */

	public SpacePosition[] getMooreNeighborsPositions(int x, int y) {
		SpacePosition[] array = new SpacePosition[8];
		int index = 0;

		for (int j = y - 1; j <= y + 1; j++)
			for (int i = x - 1; i <= x + 1; i++)
				if (!(j == y && i == x))
					array[index++] = new SpacePosition(torusX(i), torusY(j));

		return array;
	}

	/**
	 * Gets the extended von Neumann neighbors of the specified coordinate.
	 * Points are returned in west, east, north, south order with the most
	 * distant object first. The Point at x,y is not returned.
	 *
	 * @param x
	 *            the x coordinate of the object
	 * @param y
	 *            the y coordinate of the object
	 * @return an array of Point in west, east, north, south order with the most
	 *         distant object first.
	 */

	public SpacePosition[] getVonNeumannNeighborsPositions(int x, int y) {
		SpacePosition[] array = new SpacePosition[4];
		int index = 0;

		int normX = torusX(x);
		int normY = torusX(y);

		for (int i = x - 1; i < x; i++)
			array[index++] = new SpacePosition(torusX(i), normY);

		for (int i = x + 1; i > x; i--)
			array[index++] = new SpacePosition(torusX(i), normY);

		for (int i = y - 1; i < y; i++)
			array[index++] = new SpacePosition(normX, torusY(i));

		for (int i = y + 1; i > y; i--)
			array[index++] = new SpacePosition(normX, torusY(i));

		return array;
	}

	/**
	 * Returns the number of objects allocated in cell (x,y).
	 *
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @retrun the number of "entities" contained in the specified cell. See
	 *         specific semantic in API of each extending class.
	 */
	public abstract int countObjectsAt(int x, int y);

	/**
	 * Check the given y coordinate. If it is out of grid bounds the returning
	 * value is truncated to the bound. For instance, if the grid is (100, 100)
	 * the instruction <i>boundY(150)</i> returns 100.
	 *
	 * @param y
	 *            The y coordinate to be tested.
	 * @return A safe value representing the y value if inbound, the bound
	 *         itself otherwise.
	 */
	public int boundY(int y) {
		if (y < 0)
			return 0;
		if (y >= ySize)
			return ySize - 1;
		return y;
	}

	/**
	 * Check the given x coordinate considering the grid as a toroid. If x is
	 * out of grid bounds the returning coordinate is computed starting from the
	 * opposite bound. For instance, if the grid is (100, 100) the instruction
	 * <i>torusX(130)</i> returns 30, because over 100 the counter starts again
	 * from 0.
	 *
	 * @param x
	 *            The x coordinate to be tested.
	 * @return A safe value representing the x value on the toroid.
	 */
	public int torusX(int x) {
		while (x < 0)
			x += xSize;
		while (x >= xSize)
			x -= xSize;
		return x;
	}

	/**
	 * Check the given y coordinate considering the grid as a toroid. If y is
	 * out of grid bounds the returning coordinate is computed starting from the
	 * opposite bound. For instance, if the grid is (100, 100) the instruction
	 * <i>torusY(150)</i> returns 50, because over 100 the counter starts again
	 * from 0.
	 *
	 * @param y
	 *            The y coordinate to be tested.
	 * @return A safe value representing the y value on the toroid.
	 */
	public int torusY(int y) {
		while (y < 0)
			y += ySize;
		while (y >= ySize)
			y -= ySize;
		return y;
	}

	/**
	 * Check the given x coordinate considering the grid as a walled space. If x
	 * goes out of grid bounds the returning coordinate is computed mirroring
	 * the path in front of the bound. For instance, if the grid is (100, 100)
	 * the instruction <i>reflectX(130)</i> returns 70, because over 100 the
	 * path bounce on the wall and comes back.
	 *
	 * @param x
	 *            The x coordinate to be tested.
	 * @return A safe value representing the x value on the walled grid.
	 */
	public int reflectX(int x) {
		if (x < 0)
			x = -x;
		while (x >= xSize)
			x -= xSize;

		return x;
	}

	/**
	 * Check the given y coordinate considering the grid as a walled space. If y
	 * goes out of grid bounds the returning coordinate is computed mirroring
	 * the path in front of the bound. For instance, if the grid is (100, 100)
	 * the instruction <i>reflectY(140)</i> returns 60, because over 100 the
	 * path bounce on the wall and comes back.
	 *
	 * @param y
	 *            The y coordinate to be tested.
	 * @return A safe value representing the y value on the walled grid.
	 */
	public int reflectY(int y) {
		if (y < 0)
			y = -y;
		while (y >= ySize)
			y -= ySize;
		return y;
	}

	/**
	 * Return the size of the grid. It is width * height.
	 *
	 * @return The number of cells in the grid.
	 */
	public abstract int size();

	/** Empty the content of the grid. */
	public abstract void clear();

	/**
	 * Test if the passed object is equal. The comparison is made at pointer
	 * level. WARNING If grids contains exactly the same values they are not
	 * equals. Only the same implementation of the grid is considered equals to
	 * itself.
	 *
	 * @param o
	 *            The object to be compared.
	 * @return True if objects are the same, false otherwise.
	 */
	public boolean equals(Object o) {
		return (this == o);
	}

	/**
	 * Return a string representing the content of the grid.
	 *
	 * @return The string representation of the grid like [elem1, elem2, .. ].
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("[");

		for (int i = 0; i < getXSize(); i++) {
			for (int j = 0; j < getYSize(); j++) {
				Object o = get(i, j);
				buf.append(o == this ? "(this Collection)" : String.valueOf(o));
				if (i != getXSize() && j != getYSize())
					buf.append(", ");
			}
		}

		buf.append("]");
		return buf.toString();
	}
}
