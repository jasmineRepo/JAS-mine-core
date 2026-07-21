package microsim.gui.space;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import microsim.event.CommonEventType;
import microsim.event.EventListener;
import microsim.gui.shell.MicrosimShell;

/**
 * It is the Space Viewer window. It draws grid layers using a list of
 * ILayerDrawer objects. See {@code Layered<type>GridDrawer} classes of this
 * library.
 * They are wrapper classes for Grid objects of the jas.space.* library and are
 * able to plot their contents.
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
public class LayeredSurfaceFrame extends JInternalFrame implements
        EventListener {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private final static int MIN_WIDTH = 10; // 200;
    private final static int MIN_HEIGHT = 10; // 100;

    private final static int DEFAULT_CELL_LENGHT = 4;

    private int xSize;
    private int ySize;
    private int cellLen;
    private Dimension screenSize;

    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane jScrollPane = new JScrollPane();
    LayeredSurfacePanel jLayeredPanel;

    JPopupMenu popupMenu = new JPopupMenu();

    /**
     * Create a new frame with given dimensions and a cell length of 4 pixels.
     * 
     * @param width
     *               The width of the grid to plot.
     * @param height
     *               The height of the grid to plot.
     * @throws IllegalArgumentException if {@code width <= 0 || height <= 0}.
     */
    public LayeredSurfaceFrame(int width, int height) {
        this(width, height, width, height, DEFAULT_CELL_LENGHT);
    }

    /**
     * Create a new frame with given dimensions and given cell length.
     * 
     * @param width
     *                   The width of the grid to plot.
     * @param height
     *                   The height of the grid to plot.
     * @param cellLength
     *                   The lenght of a grid cell in pixels.
     * @throws IllegalArgumentException if {@code width <= 0 || height <= 0}.
     */
    public LayeredSurfaceFrame(int width, int height, int cellLength) {
        this(width, height, width, height, cellLength);
    }

    /**
     * Create a new frame with given dimensions, given cell length and given
     * view-port dimensions.
     * 
     * @param width
     *                   The width of the viewable area in cells.
     * @param height
     *                   The height of the viewable area in cells.
     * @param cellLength
     *                   The lenght of a grid cell in pixels.
     * @param gridWidth
     *                   The real width of the grid to plot.
     * @param gridHeight
     *                   The real height of the grid to plot.
     * @throws IllegalArgumentException if {@code width <= 0 || height <= 0}.
     */
    public LayeredSurfaceFrame(int width, int height, int gridWidth,
            int gridHeight, int cellLength) {

        // ImageIcon imageIcon = new ImageIcon(
        // LayeredSurfaceFrame.class.getResource("/jas/images/ca.gif"));

        if (width <= 0 || height <= 0)
            throw new IllegalArgumentException(
                    "LayeredSurfaceFrame must be created "
                            + "with positive width and heigth values.");

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        xSize = width;
        ySize = height;
        cellLen = cellLength;

        jLayeredPanel = new LayeredSurfacePanel(gridWidth, gridHeight, cellLen);

        // setIconImage(imageIcon.getImage());

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception {
        this.setResizable(true);
        this.setTitle("Space viewer");
        setLocation(50, 50);
        this.getContentPane().setLayout(borderLayout1);

        jLayeredPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                jLayeredPanel_mouseReleased(e);
            }
        });
        this.getContentPane().add(jScrollPane, BorderLayout.CENTER);
        jScrollPane.getViewport().add(jLayeredPanel, null);

        JMenuItem props = new JMenuItem("Properties");
        props.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnProperties_actionPerformed(e);
            }
        });
        popupMenu.add(props);
        // popupMenu.addSeparator();

        adjustSize();
    }

    private void adjustSize() {
        setSize(0, 0);
    }

    /**
     * Change the current cell length.
     * 
     * @param cellLength
     *                   The new cell length in pixels.
     */
    public void setCellLength(int cellLength) {
        cellLen = cellLength;
        jLayeredPanel.setCellLength(cellLength);
    }

    /**
     * Add a ILayerDrawer to the layer list.
     * 
     * @param layer
     *              The ILayerDrawer to be plotted.
     */
    public void addLayer(ILayerDrawer layer) {
        jLayeredPanel.addLayer(layer);
    }

    /** Repaint the plot area. */
    public void update() {
        jLayeredPanel.repaint();
    }

    private void jBtnProperties_actionPerformed(ActionEvent e) {
        LayeredSurfaceProperties dlg = new LayeredSurfaceProperties(
                MicrosimShell.currentShell, "Space viewer properties", cellLen,
                jLayeredPanel.getLayers());
        dlg.setVisible(true);

        if (!dlg.modified)
            return;

        if (dlg.newCellSize > 0)
            setCellLength(dlg.newCellSize);

        adjustSize();
        this.setVisible(true);
    }

    /**
     * Update the window size according to the parameters passed to the
     * constructor.
     * 
     * @param x
     *          It is ignored. The width is computed automatically.
     * @param y
     *          It is ignored. The height is computed automatically.
     */
    public void setSize(int x, int y) {
        int width = cellLen * xSize + 10;
        int height = cellLen * ySize + 28; // BUTTON_PANEL_HEIGHT;

        if (width > screenSize.getWidth())
            width = (int) screenSize.getWidth();
        if (height > screenSize.getHeight())
            height = (int) screenSize.getHeight();

        if (width < MIN_WIDTH)
            width = MIN_WIDTH;
        if (height < MIN_HEIGHT)
            height = MIN_HEIGHT;

        super.setSize(width, height);
    }

    /**
     * React to system events.
     * 
     * @param type
     *             Reacts to the Sim.EVENT_UPDATE event repainting the plot area.
     */
    public void onEvent(Enum<?> type) {
        if (type == CommonEventType.Update) {
            update();
        }
    }

    void jLayeredPanel_mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger() || e.getButton() == 3)
            popupMenu.show(e.getComponent(), e.getX(), e.getY());
    }

}
