package microsim.gui.shell;

import java.awt.Container;
import java.awt.Rectangle;

/**
 * SimWindow keeps preferred dimensions of a simulation windows.
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
 */
public class SimulationWindow {
    private Container window;
    private String key;
    private String model;
    private Rectangle defaultPosition;

    /**
     * Create a new window container with the given parameters.
     * 
     * @param model  The id of the model owner
     * @param key    The key used to store the element in the HashMap
     * @param window The window to be managed
     */
    public SimulationWindow(String model, String key, Container window) {
        this.window = window;
        this.model = model;
        this.key = key;
    }

    /**
     * Return the dimension of the managed window. If the window is not yet created
     * the method
     * returns the default bounds.
     * 
     * @return The window dimensions if present or the default ones if not.
     */
    public Rectangle getBounds() {
        if (window == null)
            return getDefaultPosition();
        else
            return window.getBounds();
    }

    /**
     * Return the key of the SimWindow object
     * 
     * @return The key value.
     */
    public String getKey() {
        return key;
    }

    /**
     * The owner model id
     * 
     * @return A string representing the model id.
     */
    public String getModel() {
        return model;
    }

    /**
     * Attach a window to the SimWindow container
     * 
     * @param container A container window object
     */
    public void setWindow(Container container) {
        window = container;
    }

    /**
     * Return the the SimWindow container
     */
    public Container getWindow() {
        return window;
    }

    /**
     * Return the default bounds for the window
     * 
     * @return The default position of the window
     */
    public Rectangle getDefaultPosition() {
        return defaultPosition;
    }

    /**
     * Set the default dimensions
     * 
     * @param rectangle The new default bounds of the window
     */
    public void setDefaultPosition(Rectangle rectangle) {
        defaultPosition = rectangle;
    }

    /**
     * 
     */
    public String toString() {
        return key;
    }

}
