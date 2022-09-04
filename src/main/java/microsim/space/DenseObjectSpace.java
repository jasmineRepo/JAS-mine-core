package microsim.space;

/**
 * A bidimensional grid containing one object per cell.
 *
 * <p>Title: JAS</p>
 * <p>Description: Java Agent-based Simulation library</p>
 * <p>Copyright (C) 2002 Michele Sonnessa</p>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 * <p>
 */
public class DenseObjectSpace extends AbstractSpace<Object> implements ObjectSpace
{
  protected Object[][] m;
  protected int objects = 0;

  /** Create a copy of the given grid.
   *  @param grid The source grid. */
  public DenseObjectSpace(AbstractSpace<Object> grid)
  {
    super(grid.getXSize(), grid.getYSize());
    m = new Object[xSize][ySize];
    for (int i = 0; i < grid.getXSize(); i++) {
		for (int j = 0; j < grid.getYSize(); j++) {
	      Object o = grid.get(i, j);
	      this.set(i, j, o);
	    }
    }
  }

  /** Create a grid with given size.
   *  @param xSize The width of the grid.
   *  @param ySize The height of the  grid. */
  public DenseObjectSpace(int xSize, int ySize)
  {
    super(xSize, ySize);
    m = new Object[xSize][ySize];
  }

  /** Return the object stored at the given position.
   *  @param x The x coordinate.
   *  @param y The y coordinate.
   *  @return The requested object.*/
  public Object get(int x, int y)
  {
    return m[x][y];
  }

  /** Add an object implementing IGridPosition interface to the grid.
   *  If object implements IGridPosition it stored in the right position
   *  of the grid.
   *  @param o The IGridPosition object to be added.
   *  @return True if object was added. If o does not implement IGridPosition
   *          interface it will not be added and method will return false.*/
  public boolean add(Object o)
  {
    if (!(o instanceof SpacePosition))
      //throw new ClassCastException("only GridPositionInterface can be added to ObjGrid");
      return false;
    SpacePosition p = (SpacePosition) o;
    set (p.getX(), p.getY(), o);
    modCount++;
    return true;
  }

  /** Swap the content of the (x1, y1) and (x2, y2) cells of the grid.
   *  @param x1 The x coordinate for the first cell.
   *  @param y1 The y coordinate for the first cell.
   *  @param x2 The x coordinate for the second cell.
   *  @param y2 The y coordinate for the second cell.*/
  public void swapPositions(int x1, int y1, int x2, int y2)
  {
    Object o1 = get(x1, y1);
    Object o2 = get(x2, y2);
    set(x1, y1, o2);
    set(x2, y1, o1);

    modCount++;
  }

  /** Put the given object at the given position.
   *  @param x The x coordinate.
   *  @param y The y coordinate.
   *  @param obj The object to be stored at the (x, y) cell.*/
  public void set(int x, int y, Object obj)
  {
    if (m[x][y] == null && obj != null)
      objects++;
    m[x][y] = obj;
    modCount++;
  }

  /**
   * Gets the Moore neighbors of the specified coordinate. doubles are returned by
   * row starting with the "NW corner" and ending with the "SE corner."
   * The double at x, y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @return an array of doubles ordered by row starting
   * with the "NW corner" and ending with the "SE corner."
   */

  public Object[] getMooreNeighbors(int x, int y)
  {
	Object[] array = new Object[8];
	int index = 0;

	for (int j = y - 1; j <= y + 1; j++)
	  for (int i = x - 1; i <= x + 1; i++)
			if (!(j == y && i == x))
			  array[index++] = get(torusX(i), torusY(j));

	return array;

//	return getMooreNeighbors( x, y, 1, 1);
  }

  /**
   * Gets the extended Moore neighbors of the specified coordinate. The
   * extension in the x and y direction are specified by xExtent and yExtent.
   * doubles are returned by row starting with the "NW corner" and ending with
   * the "SE corner." The double at x,y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @param xExtent the extension of the neighborhood in the x direction
   * @param yExtent the extension of the neighborhood in the y direction
   * @return an array of doubles ordered by row starting
   * with the "NW corner" and ending with the "SE corner."
   */

  public Object[] getMooreNeighbors(int x, int y, int xExtent, int yExtent)
  {
		Object[] array = new Object[xExtent * yExtent * 4 + (xExtent * 2) + (yExtent * 2)];
		int index = 0;

		for (int j = y - yExtent; j <= y + yExtent; j++)
		  for (int i = x - xExtent; i <= x + xExtent; i++)
			if (!(j == y && i == x))
			  array[index++] = get(boundX(i), boundY(j));

		return array;
  }

  /**
   * Gets the von Neumann neighbors of the specified coordinate.
   * doubles are returned in west, east, north, south order.
   * The double at x, y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @return an array of doubles in west, east, north, south order
   */
  public Object[] getVonNeumannNeighbors(int x, int y)
  {
	Object[] array =  new Object[4];
	int index = 0;

	int normX = torusX(x);
	int normY = torusX(y);

	for (int i = x - 1; i < x ; i++)
	  array[index++] = get(torusX(i), normY);

	for (int i = x + 1; i > x; i--)
	  array[index++] = get(torusX(i), normY);

	for (int i = y - 1; i < y; i++)
	  array[index++] = get(normX, torusY(i));

	for (int i = y + 1; i > y; i--)
	 array[index++] = get(normX, torusY(i));

	return array;

//	return getVonNeumannNeighbors(x, y, 1, 1);
  }

  /**
   * Gets the extended von Neumann neighbors of the specified coordinate. The
   * extension in the x and y direction are specified by xExtent and yExtent.
   * doubles are returned in west, east, north, south order with the
   * most distant object first. The double at x,y is not returned.
   *
   * @param x the x coordinate of the object
   * @param y the y coordinate of the object
   * @param xExtent the extension of the neighborhood in the x direction
   * @param yExtent the extension of the neighborhood in the y direction
   * @return an array of doubles in west, east, north,
   * south order with the most distant object first.
   */

  public Object[] getVonNeumannNeighbors(int x, int y, int xExtent, int yExtent)
  {
		Object[] array =  new Object[(xExtent * 2) + (yExtent * 2)];
		int index = 0;

		int normX = boundX(x);
		int normY = boundY(y);

		for (int i = x - xExtent; i < x ; i++)
		  array[index++] = get(i, normY);

		for (int i = x + xExtent; i > x; i--)
		  array[index++] = get(i, normY);

		for (int i = y - yExtent; i < y; i++)
		  array[index++] = get(normX, i);

		for (int i = y + yExtent; i > y; i--)
		 array[index++] = get(normX, i);

	return array;
  }

  /** Move a IGridPosition object from its current position to the
   *  specified destination, only if the destination cell is empty.
   *  @param destinationX The x destination coordinate.
   *  @param destinationY The y destination coordinate.
   *  @param object An object implementing IGridPosition interface.
   *  @return true if object has been moved, false otherwise.*/
  public boolean moveGridPosition(SpacePosition position, int destinationX, int destinationY)
  {
  	if (m[destinationX][destinationY] != null || position == null)
  		return false;

  	int x = position.getX();
  	int y = position.getY();
  	m[x][y] = null;
	m[destinationX][destinationY] = new SpacePosition(destinationX, destinationY);

	modCount++;

	return true;
  }

  /** Return the matrix of objects representing the grid.
   *  @return A matrix of Object with the same dimensions of the grid.*/
  public Object[][] getMatrix() { return m; }

  //COLLECTION INTERFACE
  /** Return the number of objects stored into the grid.
   *  @return The sum of cells containing an object.*/
  public int size() { return objects; }

  /** Set all cells to the null value.*/
  public void clear()
  {
    for (int i = 0; i < xSize; i++)
      for (int j = 0; j < ySize; j++)
        m[i][j] = null;
    objects = 0;
    modCount++;
  }

  /** Add an object implementing IGridPosition interface to the grid.
   *  If the destination cell is already occupied the method return false and the object
   *  is not added.
   *  @param object The IGridPosition object to be added.
   *  @return True if object has been added. False if destination cell is already occupied or
   * 		 if argument object is null.*/
public boolean addGridPosition(SpacePosition position)
{
	if (position == null)
		return false;

	int x = position.getX();
	int y = position.getY();

	if (m[x][y] != null)
		return false;

	set(x, y, position);
	modCount++;
	return true;
}

/** Remove the IGridPosition object from the grid.
  *  @param object The IGridPosition object to be removed.
  *  @return true if object has been removed. False if object is null or is not present on
  * 	the grid.*/
public boolean removeGridPosition(SpacePosition position)
{
	if (position == null)
		return false;

	int x = position.getX();
	int y = position.getY();

	if (m[x][y] == null)
		return false;

	if (! m[x][y].equals(position))
		return false;

	m[x][y] = null;
	objects--;

   	modCount++;

	return true;
}

/* Return the number of objects currently on the given position.
 * @param x The x coordinate.
 * @param y The y coordinate.
 * @return the number of object in the cell(x,y). If could be only 0 or 1.
 */
public int countObjectsAt(int x, int y)
{
	if (get(x, y) == null)
		return 0;
	else
		return 1;
}

}
