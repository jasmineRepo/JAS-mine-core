package microsim.space;

import java.util.Iterator;

/**
 * A specific iterator for grids.
 * For each value read it is possible to know what is
 * its position on the grid with <i>currentX()</i> and
 * <i>currentY()</i> methods.
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
public interface ObjectSpaceIterator<E> extends Iterator<E>
{
  /** Return the current position.
   *  @return A IGridPosition object containing the coordinates.*/
  public SpacePosition getGridPosition();
  
  public SpacePosition nextGridPosition();
  
  /** Return the current x position.
   *  @return The x coordinate of the last read.*/
  public int currentX();
  /** Return the current y position.
   *  @return The y coordinate of the last read.*/
  public int currentY();
}