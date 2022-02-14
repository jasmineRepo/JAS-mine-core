package microsim.space;

/**
 * A bidimensional grid capable of containing many objects into each cell.
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
public class MultiObjectSpace extends DenseObjectSpace {
	private static final int INITIAL_CAPACITY = 3;
	private int initialCapacity;

	/**
	 * Create a copy of the given grid with a given intial capacity.
	 * 
	 * @param grid
	 *            The source grid.
	 * @param cellCapacity
	 *            The initial vector size allocated for each cell. It grows
	 *            automatically.
	 */
	public MultiObjectSpace(AbstractSpace<Object> grid, int cellCapacity) {
		super(grid);
		initialCapacity = cellCapacity;
		if (cellCapacity < 1)
			throw new IllegalArgumentException(
					"Initial cell capacity must be greater than 0");
	}

	/**
	 * Create a grid with given size and a given intial capacity.
	 * 
	 * @param xSize
	 *            The width of the grid.
	 * @param ySize
	 *            The height of the grid.
	 * @param cellCapacity
	 *            The initial vector size allocated for each cell. It grows
	 *            automatically.
	 */
	public MultiObjectSpace(int xSize, int ySize, int cellCapacity) {
		super(xSize, ySize);
		initialCapacity = cellCapacity;
		if (cellCapacity < 1)
			throw new IllegalArgumentException(
					"Initial cell capacity must be greater than 0");
	}

	/**
	 * Create a copy of the given grid with a default intial capacity.
	 * 
	 * @param grid
	 *            The source grid.
	 */
	public MultiObjectSpace(AbstractSpace<Object> grid) {
		this(grid, INITIAL_CAPACITY);
	}

	/**
	 * Create a grid with given size and a default intial capacity.
	 * 
	 * @param xSize
	 *            The width of the grid.
	 * @param ySize
	 *            The height of the grid.
	 */
	public MultiObjectSpace(int xSize, int ySize) {
		this(xSize, ySize, INITIAL_CAPACITY);
	}

	/**
	 * Return the number of objects stored at the given position.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @return The number of objects at (x, y).
	 */
	public int countObjectsAt(int x, int y) {
		if (m[x][y] == null)
			return 0;

		int k = 0;
		Object[] arr = toArrayCell(m[x][y]);
		for (int i = 0; i < arr.length; i++)
			if (arr[i] != null)
				k++;

		return k;
	}

	/**
	 * @deprecated Its name has been changed in countObjectsAt Alias for
	 *             countObjectsAt
	 * */
	public int countAt(int x, int y) {
		return countObjectsAt(x, y);
	}

	/**
	 * Return the object stored at the given tridimensional position.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The index of the vector of object stored at the (x, y) cell.
	 *            WARNING There is no bound checking. Before using this method
	 *            please ensure the object requestes does exists.
	 * @return The requested object.
	 */
	public Object get(int x, int y, int z) {
		if (m[x][y] == null)
			return null;
		else
			return toArrayCell(m[x][y])[z];
	}

	/**
	 * Enqueue the given object at the given position.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param obj
	 *            The object to be stored into the vector at the (x, y) cell.
	 */
	public void set(int x, int y, Object obj) {
		Object[] arr;
		if (m[x][y] == null) {
			arr = new Object[initialCapacity];
			m[x][y] = arr;
		} else
			arr = toArrayCell(m[x][y]);

		int i = 0;
		int last = arr.length - 1;
		// test til last element
		while (arr[i] != null && i < last)
			i++;

		// if last is full grow up array
		if (i == last && arr[i] != null) {
			arr = growArray(arr);
			m[x][y] = arr;
			i++;
		}

		objects++;
		arr[i] = obj;
		modCount++;
	}

	/**
	 * Move a IGridPosition object from its current position to the specified
	 * destination.
	 * 
	 * @param destinationX
	 *            The x destination coordinate.
	 * @param destinationY
	 *            The y destination coordinate.
	 * @param object
	 *            An object implementing IGridPosition interface.
	 * @return true. Only if the argument object is null the return value will
	 *         be false.
	 */
	public boolean moveGridPosition(SpacePosition position, int destinationX,
			int destinationY) {
		if (position == null)
			return false;

		int x = position.getX();
		int y = position.getY();

		if (!removeAt(x, y, position))
			return false;

		set(destinationX, destinationY, position);

		return true;
	}

	/**
	 * Put the given object at the given tridimensional position.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param z
	 *            The index of the vector of object stored at the (x, y) cell.
	 *            If z is out of vector bounds this will be automatically grown.
	 * @param obj
	 *            The object to be stored into the vector at the (x, y) cell.
	 */
	public void set(int x, int y, int z, Object obj) {
		Object[] arr;
		if (m[x][y] == null) {
			arr = new Object[initialCapacity];
			m[x][y] = arr;
		} else
			arr = toArrayCell(m[x][y]);

		while (z >= arr.length) {
			arr = growArray(arr);
			m[x][y] = arr;
		}

		if (obj != null)
			objects++;
		else
			objects--;

		arr[z] = obj;
		modCount++;
	}

	private Object[] growArray(Object[] arr) {
		Object[] a = new Object[arr.length + initialCapacity];
		System.arraycopy(arr, 0, a, 0, arr.length);
		return a;
	}

	private Object[] toArrayCell(Object o) {
		return (Object[]) o;
	}

	/**
	 * Add an object implementing IGridPosition interface to the grid. If object
	 * implements IGridPosition it stored in the right position of the grid.
	 * 
	 * @param o
	 *            The IGridPosition object to be added.
	 * @return True if object was added. If o does not implement IGridPosition
	 *         interface it will not be added and method will return false.
	 */
	public boolean add(Object o) {
		if (o instanceof SpacePosition) {
			SpacePosition p = (SpacePosition) o;
			set(p.getX(), p.getY(), o);
			return true;
		} else
			return false;
	}

	/**
	 * Add an object implementing IGridPosition interface to the grid. If the
	 * destination cell is already occupied the method return false and the
	 * object is not added.
	 * 
	 * @param object
	 *            The IGridPosition object to be added.
	 * @return True if object has been added. False if destination cell is
	 *         already occupied or if argument object is null.
	 */
	public boolean addGridPosition(SpacePosition position) {
		if (position == null)
			return false;

		int x = position.getX();
		int y = position.getY();

		set(x, y, position);

		return true;
	}

	/**
	 * Remove the given object from the grid.
	 * 
	 * @param o
	 *            The object to be removed.
	 * @return True if object was found and removed, false otherwise.
	 */
	public boolean remove(Object o) {
		Object[] arr;
		for (int x = 0; x < xSize; x++)
			for (int y = 0; y < ySize; y++)
				if (m[x][y] != null) {
					arr = toArrayCell(m[x][y]);
					for (int z = 0; z < arr.length; z++)
						if (arr[z] == o) {
							set(x, y, z, null);
							return true;
						}
				}
		return false;
	}

	/**
	 * Remove the IGridPosition object from the grid.
	 * 
	 * @param object
	 *            The IGridPosition object to be removed.
	 * @return true if object has been removed. False if object is null or is
	 *         not present on the grid.
	 */
	public boolean removeGridPosition(SpacePosition position) {
		if (position == null)
			return false;

		int x = position.getX();
		int y = position.getY();

		return removeAt(x, y, position);
	}

	/**
	 * Remove the given object from the grid at given position. If the object
	 * position is known this method is better than than remove() because it
	 * works much faster.
	 * 
	 * @param x
	 *            The x coordinate.
	 * @param y
	 *            The y coordinate.
	 * @param o
	 *            The object to be removed.
	 * @return True if object was found and removed, false otherwise.
	 */
	public boolean removeAt(int x, int y, Object o) {
		Object[] arr = toArrayCell(m[x][y]);
		if (arr == null)
			return false;

		for (int i = 0; i < arr.length; i++)
			if (arr[i] == o) {
				set(x, y, i, null);
				modCount++;
				return true;
			}

		return false;
	}

}