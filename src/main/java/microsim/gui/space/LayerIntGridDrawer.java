package microsim.gui.space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

import microsim.gui.colormap.ColorMap;
import microsim.space.IntSpace;

/**
 * It is able to draw an IntGrid on a LayeredSurfaceFrame using
 * a ColorMap to render the values contained by the cell with
 * a specific color.<br>
 * This class builds an image when created and every time is updated
 * it modifies the parts of the images that are changed.
 * It is very fast when images do not change to frequently.
 * In order to let the painter to go faster it is useful to
 * reduce the number of color gradients in the ColorMap.
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
public class LayerIntGridDrawer implements ILayerDrawer {
    private int[] m;
    private ColorMap color;
    int[] trasparencyColor;
    private int xSize, ySize;
    private int cellSize = 4;

    private boolean isDisplayed = true;
    private String description;

    private int[] stateBuffer;
    private BufferedImage img;

    private ILayerMouseListener mouseListener = null;

    private void buildBufferImage() {
        WritableRaster raster = img.getRaster();
        stateBuffer = new int[xSize * ySize];

        int colorDepth;
        if (trasparencyColor == null)
            colorDepth = 3;
        else
            colorDepth = 4;

        int[] pixels = new int[colorDepth * cellSize * cellSize];

        int[] currColor = null;
        int currIndex = -1;

        int k = 0;
        for (int j = 0; j < ySize; j++)
            for (int i = 0; i < xSize; i++) {
                currIndex = color.getColorIndex(m[k]);
                currColor = color.getColorComponents(currIndex);
                stateBuffer[k] = currIndex;
                int XX = i * cellSize;
                int YY = j * cellSize;

                for (int z = 0; z < pixels.length; z += colorDepth) {
                    pixels[z] = currColor[0];
                    pixels[z + 1] = currColor[1];
                    pixels[z + 2] = currColor[2];
                    if (colorDepth == 4)
                        if (Arrays.equals(currColor, trasparencyColor))
                            pixels[z + 3] = 0;
                        else
                            pixels[z + 3] = 255;
                }

                raster.setPixels(XX, YY, cellSize, cellSize, pixels);
                k++;
            }
    }

    /**
     * Create a double layer drawer using values taken from an array of
     * integers and a given IColorMap.
     * 
     * @param name       The string describing the layer.
     * @param matrix     An array of integers of width * height length.
     * @param width      The width of the grid.
     * @param height     The height of the grid.
     * @param colorRange The IColorMap used to map values to colors.
     */
    public LayerIntGridDrawer(String name, int[] matrix, int width, int height,
            ColorMap colorRange) {
        description = name;
        m = matrix;
        color = colorRange;
        xSize = width;
        ySize = height;
        trasparencyColor = null;

        img = new BufferedImage(xSize * cellSize, ySize * cellSize, BufferedImage.TYPE_INT_RGB);
        buildBufferImage();
    }

    /**
     * Create a double layer drawer using values taken from an IntGrid matrix and
     * a given IColorMap.
     * 
     * @param name       The string describing the layer.
     * @param matrix     An IntGrid object.
     * @param colorRange The IColorMap used to map values to colors.
     */
    public LayerIntGridDrawer(String name, IntSpace matrix, ColorMap colorRange) {
        this(name, matrix.getMatrix(), matrix.getXSize(), matrix.getYSize(), colorRange);
    }

    /**
     * Create a double layer drawer using values taken from an array of
     * integers and a given IColorMap. It allows to define a trasparency color.
     * Every time the drawer has to plot the trasparentColor it stops drawing,
     * so the cell of underneath layer becomes visible.
     * 
     * @param name            The string describing the layer.
     * @param matrix          An array of integers of width * height length.
     * @param width           The width of the grid.
     * @param height          The height of the grid.
     * @param colorRange      The IColorMap used to map values to colors.
     * @param trasparentColor A color
     */
    public LayerIntGridDrawer(String name, int[] matrix, int width, int height,
            ColorMap colorRange, Color trasparentColor) {
        m = matrix;
        description = name;
        color = colorRange;
        xSize = width;
        ySize = height;
        trasparencyColor = new int[3];
        trasparencyColor[0] = trasparentColor.getRed();
        trasparencyColor[1] = trasparentColor.getGreen();
        trasparencyColor[2] = trasparentColor.getBlue();

        img = new BufferedImage(xSize * cellSize, ySize * cellSize, BufferedImage.TYPE_INT_ARGB);
        buildBufferImage();
    }

    /**
     * Create a double layer drawer using values using values taken from an IntGrid
     * matrix and a given IColorMap. It allows to define a trasparency color.
     * Every time the drawer has to plot the trasparentColor it stops drawing,
     * so the cell of underneath layer becomes visible.
     * 
     * @param name            The string describing the layer.
     * @param matrix          An IntGrid object.
     * @param colorRange      The IColorMap used to map values to colors.
     * @param trasparentColor A color
     */
    public LayerIntGridDrawer(String name, IntSpace matrix,
            ColorMap colorRange, Color trasparentColor) {
        this(name, matrix.getMatrix(), matrix.getXSize(), matrix.getYSize(),
                colorRange, trasparentColor);
    }

    // Implementing LayerDrawerInterface interface

    /**
     * Draw the layer using the given cell length.
     * 
     * @param g       The graphic context passed by container.
     * @param cellLen The length of a cell in pixels.
     */
    public void paint(Graphics g, int cellLen) {
        if (trasparencyColor != null)
            paintWithTrasparency(g, cellLen);
        else
            paintWithoutTrasparency(g, cellLen);
    }

    private void setCellLenght(int cellLength) {
        cellSize = cellLength;
        if (trasparencyColor == null)
            img = new BufferedImage(xSize * cellSize, ySize * cellSize, BufferedImage.TYPE_INT_RGB);
        else
            img = new BufferedImage(xSize * cellSize, ySize * cellSize, BufferedImage.TYPE_INT_ARGB);

        buildBufferImage();
    }

    private void paintWithoutTrasparency(Graphics g, int cellLen) {
        WritableRaster raster = img.getRaster();
        int[] pixels = new int[3 * cellLen * cellLen];

        int[] currColor;
        int currIndex;

        if (cellSize != cellLen)
            setCellLenght(cellLen);

        int k = 0;
        for (int j = 0; j < ySize; j++)
            for (int i = 0; i < xSize; i++) {
                currIndex = color.getColorIndex(m[k]);
                if (currIndex != stateBuffer[k]) {
                    currColor = color.getColorComponents(currIndex);
                    stateBuffer[k] = currIndex;
                    int XX = i * cellLen;
                    int YY = j * cellLen;

                    for (int z = 0; z < pixels.length; z += 3) {
                        pixels[z] = currColor[0];
                        pixels[z + 1] = currColor[1];
                        pixels[z + 2] = currColor[2];
                    }

                    raster.setPixels(XX, YY, cellLen, cellLen, pixels);
                }
                k++;
            }

        g.drawImage(img, 0, 0, null);
    }

    private void paintWithTrasparency(Graphics g, int cellLen) {
        WritableRaster raster = img.getRaster();
        int[] pixels = new int[4 * cellLen * cellLen];
        int[] currColor;
        int currIndex;
        int alpha = 0;

        if (cellSize != cellLen)
            setCellLenght(cellLen);

        int k = 0;
        for (int j = 0; j < ySize; j++)
            for (int i = 0; i < xSize; i++) {
                currIndex = color.getColorIndex(m[k]);
                if (currIndex != stateBuffer[k]) {
                    currColor = color.getColorComponents(currIndex);
                    stateBuffer[k] = currIndex;
                    int XX = i * cellLen;
                    int YY = j * cellLen;

                    if (Arrays.equals(currColor, trasparencyColor))
                        alpha = 0;
                    else
                        alpha = 255;

                    for (int z = 0; z < pixels.length; z += 4) {
                        pixels[z] = currColor[0];
                        pixels[z + 1] = currColor[1];
                        pixels[z + 2] = currColor[2];
                        pixels[z + 3] = alpha;
                    }

                    raster.setPixels(XX, YY, cellLen, cellLen, pixels);
                }
                k++;
            }

        g.drawImage(img, 0, 0, null);
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
     * to it, otherwise it is shown a message box with the value contained
     * by the clicked cell.
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

        javax.swing.JOptionPane.showMessageDialog(null,
                "Value at(" + atX + ", " + atY + "): " + m[atY * xSize + atX],
                "Probing " + getDescription(),
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
