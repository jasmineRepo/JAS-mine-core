package microsim.gui.space;

/**
 * An interface used by LayerDrawer to manage
 * the mouse events.
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
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 *         <p>
 */
public interface ILayerMouseListener {
    /**
     * Notify a double click event on a specific cell.
     * 
     * @param atX The x coordinate of the clicked cell.
     * @param atY The y coordinate of the clicked cell.
     * @return True if the layer intercepted the event. False if
     *         the notify has been ignored.
     */
    public boolean performDblClickActionAt(int atX, int atY);

    /**
     * Notify a right button click event on a specific cell.
     * 
     * @param atX The x coordinate of the clicked cell.
     * @param atY The y coordinate of the clicked cell.
     * @return True if the layer intercepted the event. False if
     *         the notify has been ignored.
     */
    public boolean performRightClickActionAt(int atX, int atY);

    /**
     * Notify a mouse dragging action.
     * 
     * @param fromX The x coordinate of the starting cell.
     * @param fromY The y coordinate of the starting cell.
     * @param toX   The x coordinate of the target cell.
     * @param toY   The y coordinate of the target cell.
     * @return True if the layer intercepted the event. False if
     *         the notify has been ignored.
     */
    public boolean performMouseMovedFromTo(int fromX, int fromY, int toX, int toY);
}
