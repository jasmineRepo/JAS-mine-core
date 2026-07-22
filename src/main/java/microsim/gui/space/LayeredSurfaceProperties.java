package microsim.gui.space;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * Not of interest for users. It is the properties frame called
 * by the LayeredSurfaceFrame when the uses presses the 'Properties'
 * button.
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
public class LayeredSurfaceProperties extends JDialog {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    public boolean modified;
    public int newCellSize;

    private static final int MAX_CELL_LENGTH = 8;

    private java.util.List<ILayerDrawer> displayLayers;

    JPanel jpanel = new JPanel();
    JPanel jSizePanel = new JPanel();
    JPanel jMainPanel = new JPanel();
    JPanel jButtonPanel = new JPanel();
    JButton jBtnCancel = new JButton();
    JButton jBtnOK = new JButton();
    JComboBox<String> jCmbSize = new JComboBox<>();
    JLabel jLabel1 = new JLabel();
    JLabel jLabel2 = new JLabel();
    TitledBorder titledBorder1;
    GridLayout gridLayout1 = new GridLayout();

    public LayeredSurfaceProperties(Frame frame, String title,
            int cellSize, java.util.List<ILayerDrawer> layers) {
        super(frame, title, true);

        displayLayers = layers;

        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        for (int i = 1; i <= MAX_CELL_LENGTH; i++)
            jCmbSize.addItem("" + i);
        jCmbSize.setSelectedIndex(cellSize - 1);

        for (var lay : displayLayers) {
            JCheckBox jc = new JCheckBox(lay.getDescription());
            jc.setSelected(lay.isDisplayed());
            jMainPanel.add(jc);
        }

        this.setSize(300, 300);
        modified = false;
    }

    public LayeredSurfaceProperties() {
        this(null, "", 1, null);
    }

    void jbInit() throws Exception {
        titledBorder1 = new TitledBorder("");
        jButtonPanel.setBorder(BorderFactory.createEtchedBorder());
        jButtonPanel.setMinimumSize(new Dimension(85, 40));
        jButtonPanel.setPreferredSize(new Dimension(85, 40));
        jBtnCancel.setText("Cancel");
        jBtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnCancel_actionPerformed(e);
            }
        });
        jBtnOK.setText("OK");
        jBtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnOK_actionPerformed(e);
            }
        });
        jCmbSize.setPreferredSize(new Dimension(100, 22));
        jLabel1.setText("Cell width");
        jMainPanel.setLayout(gridLayout1);
        jLabel2.setText("Current layers:");
        jpanel.setBorder(BorderFactory.createEtchedBorder());
        jSizePanel.setBorder(BorderFactory.createEtchedBorder());
        gridLayout1.setColumns(1);
        gridLayout1.setRows(10);
        getContentPane().add(jpanel, BorderLayout.CENTER);
        this.getContentPane().add(jSizePanel, BorderLayout.NORTH);
        this.getContentPane().add(jButtonPanel, BorderLayout.SOUTH);
        jButtonPanel.add(jBtnCancel, null);
        jButtonPanel.add(jBtnOK, null);
        jpanel.add(jMainPanel, null);
        jMainPanel.add(jLabel2, null);
        jSizePanel.add(jLabel1, null);
        jSizePanel.add(jCmbSize, null);

        this.setSize(new Dimension(300, 300));
        this.setLocation(200, 200);
    }

    void jBtnCancel_actionPerformed(ActionEvent e) {
        dispose();
    }

    void jBtnOK_actionPerformed(ActionEvent e) {
        for (var lay : displayLayers) {
            lay.setDisplay(getStatusCheck(lay.getDescription()));
        }

        modified = true;
        newCellSize = jCmbSize.getSelectedIndex() + 1;
        dispose();
    }

    private boolean getStatusCheck(String checkName) {
        Component[] cs = jMainPanel.getComponents();
        for (int i = 0; i < cs.length; i++)
            if (cs[i] instanceof JCheckBox)
                if (((JCheckBox) cs[i]).getText().equals(checkName))
                    return ((JCheckBox) cs[i]).isSelected();

        return false;
    }
}
