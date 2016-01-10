package microsim.space;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A sparse bidimensional grid containing one object per cell.
 * This object must be used when the ratio between the size
 * of the grid and its average fullness is high.
 * For instance, if you have a grid of 1000 x 1000, with 1000000
 * of cells and it has to contain 1000 objects, the better
 * solution for memory occupation is to use an ObjVirtualGrid.
 * Obviuosly the ObjVirtualGrid is slower than ObjGrid, but it
 * is much cheaper in memory occupation.
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
public class SparseObjectSpace extends AbstractSpace<Object> implements ObjectSpace 
{
  protected HashMap<HashKey, Object> m;

  /** Create a copy of the given grid.
   *  @param grid The source grid. */
  public SparseObjectSpace(AbstractSpace<Object> grid)
  {
    super(grid.getXSize(), grid.getYSize());
    m = new HashMap<HashKey, Object>();
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
  public SparseObjectSpace(int xSize, int ySize)
  {
    super(xSize, ySize);
    m = new HashMap<HashKey, Object>();
  }

  /** Return the object stored at the given position.
   *  @param x The x coordinate.
   *  @param y The y coordinate.
   *  @return The requested object.*/
  public Object get(int x, int y)
  {
    return m.get(getKey(x, y));
  }

  /** Put the given object at the given position.
   *  @param x The x coordinate.
   *  @param y The y coordinate.
   *  @param obj The object to be stored at the (x, y) cell.*/
  public void set(int x, int y, Object obj)
  {
    m.put(getKey(x, y), obj);
  }

  /** Swap the content of the (x1, y1) and (x2, y2) cells of the grid.
   *  @param x1 The x coordinate for the first cell.
   *  @param y1 The y coordinate for the first cell.
   *  @param x2 The x coordinate for the second cell.
   *  @param y2 The y coordinate for the second cell.*/
  public void swapPositions(int x1, int y1, int x2, int y2)
  {
    HashKey k1 = getKey(x1, y1);
    HashKey k2 = getKey(x2, y2);

    Object o1 = m.remove(k1);
    Object o2 = m.remove(k2);

    m.put(k1, o2);
    m.put(k2, o1);
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
  
   /** Move a IGridPosition object from its current position to the
	*  specified destination, only if the destination cell is empty.
	*  @param destinationX The x destination coordinate.
	*  @param destinationY The y destination coordinate.
	*  @param object An object implementing IGridPosition interface.
	*  @return true if object has been moved, false otherwise.*/
   public boolean moveGridPosition(SpacePosition position, int destinationX, int destinationY)
   {
	 if (position == null)
		 return false;
  		
	 int x = position.getX();
	 int y = position.getY();
	 m.remove(getKey(x, y));
	 
	m.put(getKey(destinationX, destinationY), position);
	
	 modCount++;
	
	 return true;
   }
   
  private HashKey getKey(int x, int y)
  {
    return new HashKey(x, y);
  }

  /** Return a GridIterator storing the position of the read value.
   *  @return A GridIterator that scrolls grid from the top-left corner to
   *          the bottom-right, reading each line from left to right.
   *          It remember the position of the last read value.*/
  public ObjectSpaceIterator<Object> gridIterator()
  {
    return new Itr();
  }

  //COLLECTION
  /** Return the number of objects stored into the grid.
   *  @return The sum of cells containing an object.*/
  public int size() { return m.size(); }

  /** Test if given object is contained into the grid.
   *  @param o The object to be tested.
   *  @return True if object is present into the grid.*/
  public boolean contains(Object o) { return m.containsValue(o); }

  /*  @return A iterator that scrolls grid from the top-left corner to
   *          the bottom-right, reading each line from left to right.*/
  public Iterator<Object> iterator() { return m.values().iterator(); }

  /** Return an array of objects stored into the grid.
   *  @return A vector containing only the objects stored into the grid.
   *          The empty cells are ignored.*/
  public Object[] toArray() { return m.values().toArray(); }

  /** Add an object implementing IGridPosition interface to the grid.
   *  If object implements IGridPosition it stored in the right position
   *  of the grid.
   *  @param o The IGridPosition object to be added.
   *  @return True if object was added. If o does not implement IGridPosition
   *          interface it will not be added and method will return false.*/
  public boolean add(Object o)
  {
    if (!(o instanceof SpacePosition))
      //throw new ClassCastException("Method add() accepts only GridPositionInterface objects.");
      return false;
    SpacePosition p = (SpacePosition) o;
    m.put(getKey(p.getX(), p.getY()), o);
    return true;
  }

  /** Remove the given object from the grid.
   *  @param o The object to be removed.
   *  @return True if object was found and removed, false otherwise.
   *  @throws ClassCastException If o does not implement IGridPosition interface.*/
  public boolean remove(Object o)
  {
    if (!(o instanceof SpacePosition))
      throw new ClassCastException("Method remove() accepts only GridPositionInterface objects.");
    SpacePosition p = (SpacePosition) o;
    m.remove(getKey(p.getX(), p.getY()));
    return true;
  }

  /** Set all cells to the null value.*/
  public void clear() { m.clear(); }

  /** Test if o is the same of this. There is no content comparing.
   *  @param o The object to be compared.
   *  @return True o is <b>this</b> object.*/
  public boolean equals(Object o) { return (this == o); }

  private class HashKey
  {
    public HashKey(int x, int y)
    {
      this.x = x;
      this.y = y;
    }
    public int x, y;
    public int hashCode()
    {
      return (y * xSize + x);
    }
    public boolean equals(Object o)
    {
      HashKey k = (HashKey) o;
      return (x == k.x && y == k.y);
    }
  }

    private class Itr implements ObjectSpaceIterator<Object>
    {
      Iterator<Map.Entry<SparseObjectSpace.HashKey, Object>> it = m.entrySet().iterator();
      HashKey currentKey = new HashKey(0, 0);

      public SpacePosition getGridPosition()
      {
        return new SpacePosition(currentKey.x, currentKey.y);
      }

      public int currentX() { return currentKey.x; }
      public int currentY() { return currentKey.y; }

      public boolean hasNext() { return it.hasNext(); }

      public Object next()
      {
        Map.Entry<SparseObjectSpace.HashKey, Object> entry = it.next();
        currentKey = (HashKey) entry.getKey();
        return entry.getValue();
      }

      public void remove() { m.remove(currentKey); }
      
		public SpacePosition nextGridPosition() {
			next();
			return getGridPosition();
		}
      
    }

	/** Add an object implementing IGridPosition interface to the grid.
	  *  If the destination cell is already occupied the method return false and the object
	  *  is not added.
	  *  @param object The IGridPosition object to be added.
	  *  @return True if object has been added. False if destination cell is already occupied or
	  * 		 if argument object is null.*/
	public boolean addGridPosition(SpacePosition object) 
	{
		if (object == null)
			return false;
			
		int x = object.getX();
		int y = object.getY();
		
		if (get(x, y) != null)
			return false;
			
		set(x, y, object);		

		return true;
	}

/** Remove the IGridPosition object from the grid.
  *  @param object The IGridPosition object to be removed.
  *  @return true if object has been removed. False if object is null or is not present on
  * 	the grid.*/
	public boolean removeGridPosition(SpacePosition object) 
	{
		if (object == null)
			return false;
		
		int x = object.getX();
		int y = object.getY();
		
		if (get(x, y) != null)
			return false;
			
		m.remove(getKey(x, y));
		return true;   
	}


}