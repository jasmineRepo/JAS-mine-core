package microsim.space;

/**
 * A bidimensional grid containing double values.
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
public class DoubleSpace extends AbstractSpace<Double>
{
  protected double[] m;
  private int size;

  /** Create a grid of given size.
   *  @param xSize The width of the grid.
   *  @param ySize The height of the grid.*/
  public DoubleSpace(int xSize, int ySize)
  {
    super(xSize, ySize);
    m = new double[xSize * ySize];
    size = xSize * ySize;
  }

  /** Create a copy of the given grid.
   *  @param grid The source grid.*/
  public DoubleSpace(DoubleSpace grid)
  {
    super(grid.getXSize(), grid.getYSize());
    m = new double[xSize * ySize];
    size = xSize * ySize;
    for (int x = 0; x < xSize; x++)
      for (int y = 0; y < ySize; y++)
        this.setDbl(x, y, grid.getDbl(x, y));
  }

  protected int at(int x, int y)
  {
    return y * xSize + x;
  }

  /** Return a Double object containing the value at given position.
   *  @param x The x coordinate. WARNING: No bounds checking for fast access.
   *  @param y The y coordinate. WARNING: No bounds checking for fast access.
   *  @return The Double wrapper for value stored at x,y position of the grid. */
  public Double get(int x, int y)
  {
    return new Double(m[at(x, y)]);
  }

  /** Return the value at given position.
   *  @param x The x coordinate. WARNING: No bounds checking for fast access.
   *  @param y The y coordinate. WARNING: No bounds checking for fast access.
   *  @return The value stored at x,y position of the grid. */
  public double getDbl(int x, int y)
  {
    return m[at(x, y)];
  }

  /** Set the given value at given position.
   *  @param x The x coordinate. WARNING: No bounds checking for fast access.
   *  @param y The y coordinate. WARNING: No bounds checking for fast access.
   *  @param obj An object wrapper for a number class.
   *          It is possible to pass Interger, Double, Float or Long values.
   */
  public void set(int x, int y, Object obj)
  {
    if (obj == null)
    {
      m[at(x, y)] = 0;
      return;
    }

    if (!(obj instanceof Number))
      throw new ClassCastException();

    m[at(x, y)] = ((Number)obj).doubleValue();
  }

  /** Swap the content of the (x1, y1) and (x2, y2) cells of the grid.
   *  @param x1 The x coordinate for the first cell.
   *  @param y1 The y coordinate for the first cell.
   *  @param x2 The x coordinate for the second cell.
   *  @param y2 The y coordinate for the second cell.*/
  public void swapPositions(int x1, int y1, int x2, int y2)
  {
    double d = m[at(x1, y1)];
    m[at(x1, y1)] = m[at(x2, y2)];
    m[at(x2, y2)] = d;
  }

  /** Set the given value at given position.
   *  @param x The x coordinate. WARNING: No bounds checking for fast access.
   *  @param y The y coordinate. WARNING: No bounds checking for fast access.
   *  @param value A double value to put at x,y position.
   */
  public void setDbl(int x, int y, double value)
  {
    m[at(x, y)] = value;
  }

  /** Return the size of the grid. It is width * height.
   *  @return The number of cells in the grid.*/
  public int size()
  {
    return size;
  }

  /** Set all cells to 0.0 value.*/
  public void clear()
  {
    for (int i = 0; i < xSize; i++)
      for (int j = 0; j < ySize; j++)
        m[at(i, j)] = 0.0;
  }

  /** Set all cells to the given value.
   *  @param initValue The value to put into each cell.*/
  public void resetTo(double initValue)
  {
    for (int x = 0; x < xSize; x++)
      for (int y = 0; y < ySize; y++)
        m[at(x, y)] = initValue;
  }

  /** Sum the given value to the value of each cell.
   *  @param arg The value to be added.*/
  public void add(double arg)
  {
    for (int i=0; i < xSize; i++)
      for (int j = 0; j < ySize; j++)
        m[at(i, j)] += arg;
  }

  /** Multiply the given value to the value of each cell.
   *  @param arg The value to be multiplyed.*/
  public void multiply(double arg)
  {
    for (int i=0; i < xSize; i++)
      for (int j = 0; j < ySize; j++)
        m[at(i, j)] *= arg;
  }

  /** Get the minimum value stored into the grid.
   *  @return The minimum value of the grid.*/
  public double min()
  {
    double minimum = m[0];
    for (int row = 0; row < xSize; row++)
      for (int col = 0; col < ySize; col++)
        if (m[at(row, col)] < minimum)
          minimum = m[at(row, col)];

    return minimum;
  }

  /** Get the maximum value stored into the grid.
   *  @return The maximum value of the grid.*/
  public double max() {
    double maximum = m[0];
    for (int row = 0; row < xSize; row++)
      for (int col = 0; col < ySize; col++)
        if (m[at(row, col)] > maximum)
          maximum = m[at(row, col)];

    return maximum;
  }

  /** Sum the value of each cell.
   *  @return The sum the value of each cell.*/
  public double sum() {
    double total = 0;
    for (int row = 0; row < xSize; row++)
      for (int col = 0; col < ySize; col++)
        total += m[at(row, col)];

    return total;
  }

  /** Compute the sample mean value of the values stored in the grid.
   *  @return The mean value.*/
  public double mean() {
    double sum = 0;
    for (int row = 0; row < xSize; row++)
      for (int col = 0; col < ySize; col++)
        sum += m[at(row, col)];

    return sum / (xSize * ySize);
  }

  /** Compute the sample variance value of the values stored in the grid.
   *  @return The variance value.*/
  public double variance() {
    double s_squared = 0;
    double mn = mean();
    for (int row = 0; row < xSize; row++)
      for (int col = 0; col < ySize; col++)
      {
        double temp = m[at(row, col)] - mn;
        s_squared += (temp * temp);
      }

    return s_squared / (xSize * ySize);
  }


  /** Return the matrix of values representing the grid.
   *  @return A matrix of double with the same dimensions of the grid.*/
  public double[] getMatrix() { return m; }

  /** Copies the given DblGrid content in this grid.
   *  @param dm The source DblGrid to be copied.
   */
  public void copyGridTo(DoubleSpace dm)
  {
    copyGridTo(dm.m);
  }

  /** Copies the double[] matrix content in this grid.
   *  @param dm The source matrix to be copied.
   */
  public void copyGridTo(double[] dm)
  {
     System.arraycopy(m, 0, dm, 0, xSize * ySize);
  }

/* Return the value contained by the given cell casted to int.
 * @param x The x coordinate.
 * @param y The y coordinate.
 * @return The int value contained by the (x,y) cell.
 */
public int countObjectsAt(int x, int y) 
{
	return (int) m[at(x, y)];
}
}