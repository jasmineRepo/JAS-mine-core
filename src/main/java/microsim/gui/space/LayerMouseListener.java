package microsim.gui.space;

/**
 * A generic implementation of the ILayerMouseListener interface.
 * If you want to manage only one or two mouse events, you can extend
 * this class, overriding only the useful methods. The methods not overridden
 * return always false.
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
public class LayerMouseListener implements ILayerMouseListener {
    public boolean performDblClickActionAt(int atX, int atY) {
        return false;
    }

    public boolean performRightClickActionAt(int atX, int atY) {
        return false;
    }

    public boolean performMouseMovedFromTo(int fromX, int fromY, int toX, int toY) {
        return false;
    }
}
