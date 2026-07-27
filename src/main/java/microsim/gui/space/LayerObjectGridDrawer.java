package microsim.gui.space;

import java.awt.Color;
import java.awt.Graphics;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import microsim.gui.colormap.ColorMap;
import microsim.gui.probe.ProbeFrame;
import microsim.reflection.ReflectionUtils;
import microsim.space.ObjectSpace;
import microsim.statistics.reflectors.DoubleInvoker;
import microsim.statistics.reflectors.IntegerInvoker;

/**
 * It is able to draw objects contained by an ObjGrid on a
 * LayeredSurfaceFrame.<br>
 * An object is represented by a circle. The objects could be drawn using one
 * given color or, implementing the IColored interface inside them, each object
 * return to the LayerObjGridDrawer which color to use.
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
public class LayerObjectGridDrawer implements ILayerDrawer {

    private static final Logger log = LogManager.getLogger(LayerObjectGridDrawer.class);

    ObjectSpace space;
    Color c;

    private ColorMap colorMap;
    private boolean isDisplayed = true;
    private String description;

    private Object invoker = null;

    private ILayerMouseListener mouseListener = null;

    /**
     * Create a new object drawer based on a given Grid object. It plots the
     * objects using the given color. NOTICE that the matrix parameter accepts a
     * generic Grid, so also classes like IntGrid could be drawn.
     * 
     * @param name
     *               The string describing the layer.
     * @param matrix
     *               A generic Grid object.
     * @param color
     *               The default color used to plot objects.
     */
    public LayerObjectGridDrawer(String name, ObjectSpace matrix, Color color) {
        description = name;
        space = matrix;
        c = color;
    }

    /**
     * Create a new object drawer based on a given Grid object. It plots the
     * objects using the color they return. NOTICE that the matrix parameter
     * accepts a generic Grid, so also classes like IntGrid could be drawn.
     * 
     * @param name
     *               The string describing the layer.
     * @param matrix
     *               A generic Grid object containing IColored objects.
     */
    public LayerObjectGridDrawer(String name, ObjectSpace matrix, Class<?> targetClass, String variableName,
            boolean isMethod, ColorMap map) {
        this(name, matrix, null);
        this.colorMap = map;
        if (ReflectionUtils.isDoubleSource(targetClass, variableName, isMethod))
            invoker = new DoubleInvoker(targetClass, variableName, isMethod);
        else if (ReflectionUtils.isIntSource(targetClass, variableName, isMethod))
            invoker = new IntegerInvoker(targetClass, variableName, isMethod);
        else
            throw new IllegalArgumentException("Supported field type: double, int");
    }

    // Implementing LayerDrawerInterface interface

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

    private Color getColor(Object agent) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException,
            IllegalAccessException {

        int level;
        if (invoker instanceof DoubleInvoker)
            level = (int) ((DoubleInvoker) invoker).getDouble(agent);
        else
            level = ((IntegerInvoker) invoker).getInt(agent);

        int index = colorMap.getColorIndex(level);

        int[] components = colorMap.getColorComponents(index);
        return new Color(components[0], components[1], components[2]);

    }

    private void paintWithoutColor(Graphics g, int cellLen)
            throws SecurityException, IllegalArgumentException,
            NoSuchFieldException, IllegalAccessException {
        Object obj;
        Color cl, currentColor = null;

        for (int i = 0; i < space.getXSize(); i++)
            for (int j = 0; j < space.getYSize(); j++)
                if ((obj = space.get(i, j)) != null) {
                    cl = getColor(obj);
                    if (cl != currentColor) {
                        currentColor = cl;
                        g.setColor(currentColor);
                    }
                    int XX = i * cellLen;
                    int YY = j * cellLen;
                    g.fillOval(XX, YY, cellLen, cellLen);
                }
    }

    /**
     * Draw the layer using the given cell length.
     * 
     * @param g
     *                The graphic context passed by container.
     * @param cellLen
     *                The length of a cell in pixels.
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
     * @param display
     *                True if you want the layer to be painted, false otherwise.
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
     * Set a manager for mouse events. If not defined, mouse events are managed
     * by the class itself.
     * 
     * @param listener
     *                 A ILayerMouseListener object.
     */
    public void setMouseListener(ILayerMouseListener listener) {
        mouseListener = listener;
    }

    /**
     * If a mouse listener has been defined the double-click event, it is passed
     * to it, otherwise it is shown a message box with the value contained by
     * the clicked cell.
     * 
     * @param atX
     *            The x coordinate of the clicked cell.
     * @param atY
     *            The y coordinate of the clicked cell.
     * @return always true if no mouse listener is defined. This value is used
     *         by caller to know if the layer wants to manage the event.
     */
    public boolean performDblClickActionAt(int atX, int atY) {
        if (mouseListener != null)
            return mouseListener.performDblClickActionAt(atX, atY);

        if (space.get(atX, atY) == null)
            return false;

        Object p = space.get(atX, atY);
        ProbeFrame pf = new ProbeFrame(p, p.toString());
        pf.setVisible(true);
        return true;
    }

    /**
     * If a mouse listener has been defined the right-click event, it is passed
     * to it, otherwise it is returned false.
     * 
     * @param atX
     *            The x coordinate of the clicked cell.
     * @param atY
     *            The y coordinate of the clicked cell.
     * @return always false if no mouse listener is defined. This value is used
     *         by caller to know if the layer wants to manage the event.
     */
    public boolean performRightClickActionAt(int atX, int atY) {
        if (mouseListener != null)
            return mouseListener.performRightClickActionAt(atX, atY);

        return false;
    }

    /**
     * If a mouse listener has been defined the mouse dragging event, it is
     * passed to it, otherwise it is returned false.
     * 
     * @param fromX
     *              The x coordinate of the starting cell.
     * @param fromY
     *              The y coordinate of the starting cell.
     * @param toX
     *              The x coordinate of the last dragged cell.
     * @param toY
     *              The y coordinate of the last dragged cell.
     * @return always false if no mouse listener is defined. This value is used
     *         by caller to know if the layer wants to manage the event.
     */
    public boolean performMouseMovedFromTo(int fromX, int fromY, int toX,
            int toY) {
        if (mouseListener != null)
            return mouseListener
                    .performMouseMovedFromTo(fromX, fromY, toX, toY);

        return false;
    }

}
