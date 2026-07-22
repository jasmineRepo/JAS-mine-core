package microsim.gui.space;

import java.awt.*;
import java.awt.event.*;

import javax.swing.JPanel;
import java.util.List;
import java.util.ArrayList;

/**
 * Not of interest for users. It is the panel drawing the
 * {@code Layer<type>Drawer} objects added to the LayeredSurfaceFrame.
 * It manages mouse events, too.
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
public class LayeredSurfacePanel extends JPanel {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private List<ILayerDrawer> m_layers;
    private int xSize;
    private int ySize;
    private int cellLen;

    private int virtualWidth, virtualHeigth;

    private Color background;

    // Used for dragging
    private int lastX, lastY;

    /**
     * Create a panel with dimensions of (100, 100) and a cell length of 4 pixels.
     */
    public LayeredSurfacePanel() {
        this(100, 100, 4);
    }

    /**
     * Create a panel with given dimensions and given cell length.
     * 
     * @param width      The width of the grid to plot.
     * @param height     The height of the grid to plot.
     * @param cellLength The lenght of a grid cell in pixels.
     */
    public LayeredSurfacePanel(int width, int height, int cellLength) {
        xSize = width;
        ySize = height;
        cellLen = cellLength;
        setVirtualDimensions();
        m_layers = new ArrayList<ILayerDrawer>();
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                this_mouseDragged(e);
            }
        });
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                this_mouseClicked(e);
            }

            public void mousePressed(MouseEvent e) {
                this_mousePressed(e);
            }

            public void mouseReleased(MouseEvent e) {
                this_mouseReleased(e);
            }
        });
    }

    /**
     * Return the current background color.
     * 
     * @return The current background color. Null if color has been not set.
     */
    public Color getBackgroundColor() {
        return background;
    }

    /**
     * Set the current background color.
     * 
     * @param color The current background color.
     */
    public void setBackgroundColor(Color color) {
        background = color;
    }

    private void setVirtualDimensions() {
        virtualWidth = xSize * cellLen;
        virtualHeigth = ySize * cellLen;

        this.setSize(virtualWidth, virtualHeigth);
        this.setPreferredSize(new Dimension(virtualWidth, virtualHeigth));
    }

    /**
     * Change the size of the grid.
     * 
     * @param width  The width of the grid to plot.
     * @param height The height of the grid to plot.
     */
    public void setVirtualSize(int width, int height) {
        xSize = width;
        ySize = height;
        setVirtualDimensions();
    }

    /**
     * Add a ILayerDrawer to the layer list.
     * 
     * @param layer The ILayerDrawer to be plotted.
     */
    public void addLayer(ILayerDrawer layer) {
        m_layers.add(layer);
    }

    /**
     * Return the list of current added layers.
     * 
     * @return An ArrayList of ILayerDrawer objects.
     */
    public List<ILayerDrawer> getLayers() {
        return m_layers;
    }

    /**
     * Change the current cell length.
     * 
     * @param cellLength The new cell length in pixels.
     */
    public void setCellLength(int cellLength) {
        cellLen = cellLength;
        setVirtualDimensions();
    }

    /**
     * Draw the panel.
     * 
     * @param g The graphic context passed by container.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, virtualWidth, virtualHeigth);
        }

        ILayerDrawer lay;
        for (int i = 0; i < m_layers.size(); i++) {
            lay = m_layers.get(i);
            if (lay.isDisplayed())
                lay.paint(g, cellLen);
        }

    }

    private void this_mouseClicked(MouseEvent e) {
        ILayerDrawer lay;

        if (e.getClickCount() != 2)
            return;

        int x = e.getX() / cellLen;
        int y = e.getY() / cellLen;

        for (int i = m_layers.size() - 1; i >= 0; i--) {
            lay = m_layers.get(i);
            if (lay.isDisplayed())
                if (lay.performDblClickActionAt(x, y))
                    return;
        }

    }

    private void this_mousePressed(MouseEvent e) {
        lastX = e.getX() / cellLen;
        lastY = e.getY() / cellLen;
    }

    private void this_mouseDragged(MouseEvent e) {
    }

    private void this_mouseReleased(MouseEvent e) {
        ILayerDrawer lay;
        if (lastX < 0 || lastX > virtualWidth ||
                lastY < 0 || lastY > virtualHeigth)
            return;

        int x = e.getX() / cellLen;
        int y = e.getY() / cellLen;

        for (int i = m_layers.size() - 1; i >= 0; i--) {
            lay = m_layers.get(i);
            if (lay.isDisplayed())
                if (lay.performMouseMovedFromTo(lastX, lastY, x, y))
                    return;
        }

    }

}
