package microsim.gui.space;

import java.awt.Color;
import java.awt.Graphics;
import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import microsim.gui.colormap.ColorMap;
import microsim.space.MultiObjectSpace;

/**
 * It is able to draw objects contained by a MultiObjGrid on a
 * LayeredSurfaceFrame.<br>
 * When on a cell there is at least one object it is represented by a circle.
 * The objects are drawn using one given color.
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
public class LayerMultiObjectGridDrawer implements ILayerDrawer {

    private static final Logger log = LogManager.getLogger(LayerMultiObjectGridDrawer.class);

    MultiObjectSpace space;
    Color c;

    private ColorMap colorMap;
    private String agentProperty;
    private boolean isDisplayed = true;
    private String description;

    private ILayerMouseListener mouseListener = null;

    /**
     * Create a new object drawer based on a given MultiObjGrid object. It plots
     * the objects using the given color.
     * 
     * @param name   The string describing the layer.
     * @param matrix A MultiObjGrid object.
     * @param color  The default color used to plot objects.
     */
    public LayerMultiObjectGridDrawer(String name, MultiObjectSpace matrix, Color color) {
        description = name;
        space = matrix;
        c = color;
    }

    /**
     * Create a new object drawer based on a given MultiObjGrid object. It plots
     * the objects using the color of the first object found on each cell.
     * 
     * @param name   The string describing the layer.
     * @param matrix A MultiObjGrid object.
     */
    public LayerMultiObjectGridDrawer(String name, MultiObjectSpace matrix, String agentProperty, ColorMap map) {
        this(name, matrix, null);
        this.colorMap = map;
        this.agentProperty = agentProperty;
    }

    /**
     * Return if the layer is currently displayed on the LayeredSurfaceFrame.
     * 
     * @return True if it is currently painted, false otherwise.
     */
    public boolean isDisplayed() {
        return isDisplayed;
    }

    /**
     * Decide if layer has to be painted or not.
     * 
     * @param display True if you want the layer to be painted, false otherwise.
     */
    public void setDisplay(boolean display) {
        isDisplayed = display;
    }

    /**
     * Return the name of the layer.
     * 
     * @return The name passed to the constructor.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Draw the layer using the given cell length.
     * 
     * @param g       The graphic context passed by container.
     * @param cellLen The length of a cell in pixels.
     */
    public void paint(Graphics g, int cellLen) {
        if (c != null)
            paintWithColor(g, cellLen);
        else
            try {
                paintWithoutColor(g, cellLen);
            } catch (SecurityException e) {
                log.error(e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            } catch (NoSuchFieldException e) {
                log.error(e.getMessage());
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
    }

    private void paintWithColor(Graphics g, int cellLen) {

        g.setColor(c);

        for (int i = 0; i < space.getXSize(); i++)
            for (int j = 0; j < space.getYSize(); j++)
                if (space.countObjectsAt(i, j) > 0) {
                    int XX = i * cellLen;
                    int YY = j * cellLen;
                    g.fillOval(XX, YY, cellLen, cellLen);
                }
    }

    private Color getColor(Object agent)
            throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = agent.getClass();
        Field field = clazz.getField(agentProperty);
        field.setAccessible(true);
        int index;
        if (field.getType().equals(Double.class))
            index = colorMap.getColorIndex(field.getDouble(agent));
        else
            index = colorMap.getColorIndex(field.getInt(agent));

        int[] components = colorMap.getColorComponents(index);
        return new Color(components[0], components[1], components[2]);

    }

    private void paintWithoutColor(Graphics g, int cellLen)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object[] obj;
        Color cl, currentColor = null;

        for (int i = 0; i < space.getXSize(); i++)
            for (int j = 0; j < space.getYSize(); j++)
                if ((obj = (Object[]) space.get(i, j)) != null)
                    for (int k = 0; k < obj.length; k++)
                        if (obj[k] != null) {
                            cl = getColor(obj[k]);
                            if (cl != currentColor) {
                                currentColor = cl;
                                g.setColor(currentColor);
                            }
                            int XX = i * cellLen;
                            int YY = j * cellLen;
                            g.fillOval(XX, YY, cellLen, cellLen);
                            k = obj.length;
                        }
    }

    /**
     * Set a manager for mouse events. If not defined, mouse events are
     * managed by the class itself.
     * 
     * @param listener A ILayerMouseListener object.
     */
    public void setMouseListener(ILayerMouseListener listener) {
        mouseListener = listener;
    }

    /**
     * If a mouse listener has been defined the double-click event, it is passed
     * to it, otherwise it is shown a CellObjectChooser that allows the user
     * to choose which object to be probed.
     * 
     * @param atX The x coordinate of the clicked cell.
     * @param atY The y coordinate of the clicked cell.
     * @return always true if no mouse listener is defined.
     *         This value is used by caller to know if
     *         the layer wants to manage the event.
     */
    public boolean performDblClickActionAt(int atX, int atY) {
        if (mouseListener != null)
            return mouseListener.performDblClickActionAt(atX, atY);

        if (space.get(atX, atY) == null)
            return false;

        Object[] p = (Object[]) space.get(atX, atY);
        CellObjectChooser chooser = new CellObjectChooser(p, null, "Objects at " + atX
                + ", " + atY, true);
        chooser.setVisible(true);
        return true;
    }

    /**
     * If a mouse listener has been defined the right-click event, it is passed
     * to it, otherwise it is returned false.
     * 
     * @param atX The x coordinate of the clicked cell.
     * @param atY The y coordinate of the clicked cell.
     * @return always false if no mouse listener is defined.
     *         This value is used by caller to know if
     *         the layer wants to manage the event.
     */
    public boolean performRightClickActionAt(int atX, int atY) {
        if (mouseListener != null)
            return mouseListener.performRightClickActionAt(atX, atY);

        return false;
    }

    /**
     * If a mouse listener has been defined the mouse dragging event, it is passed
     * to it, otherwise it is returned false.
     * 
     * @param fromX The x coordinate of the starting cell.
     * @param fromY The y coordinate of the starting cell.
     * @param toX   The x coordinate of the last dragged cell.
     * @param toY   The y coordinate of the last dragged cell.
     * @return always false if no mouse listener is defined.
     *         This value is used by caller to know if
     *         the layer wants to manage the event.
     */
    public boolean performMouseMovedFromTo(int fromX, int fromY, int toX, int toY) {
        if (mouseListener != null)
            return mouseListener.performMouseMovedFromTo(fromX, fromY, toX, toY);

        return false;
    }
}
