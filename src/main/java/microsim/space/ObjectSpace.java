/*
 * Created on 5-giu-2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package microsim.space;

/**
 * @author michele
 *
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ObjectSpace {

	public int getXSize();

	public int getYSize();

	public int boundX(int x);

	public int boundY(int y);

	public int reflectX(int x);

	public int reflectY(int y);

	public int torusX(int x);

	public int torusY(int y);

	public int countObjectsAt(int x, int y);

	boolean addGridPosition(SpacePosition position);

	boolean removeGridPosition(SpacePosition position);

	boolean moveGridPosition(SpacePosition object, int destinationX,
			int destinationY);

	public Object get(int x, int y);

	public void set(int x, int y, Object obj);
}
