package microsim.gui.probe;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Not of interest for users.
 * Its the panel containing the table using ObjectDataModel.
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
public class PanelObjectCollection extends JPanel {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    BorderLayout borderLayout = new BorderLayout();
    ObjectDataModel dataModel;

    JButton jBtnNewProbe = new JButton();
    JScrollPane jScrollPaneObjects = new JScrollPane();
    JTable jTableObjects = new JTable();

    public PanelObjectCollection(Object o) {
        try {
            dataModel = new ObjectDataModel(o);
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    void jbInit() throws Exception {
        this.setLayout(borderLayout);
        jTableObjects.setModel(dataModel);
        jTableObjects.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jTableObjects_mouseClicked(e);
            }
        });

        jBtnNewProbe.setText("Open probe on selected object");
        jBtnNewProbe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnNewProbe_actionPerformed(e);
            }
        });

        for (int i = 0; i < jTableObjects.getColumnModel().getColumnCount(); i++)
            jTableObjects.getColumnModel().getColumn(i).setHeaderValue(
                    dataModel.getHeaderText(i));

        this.add(jBtnNewProbe, BorderLayout.SOUTH);
        this.add(jScrollPaneObjects, BorderLayout.CENTER);
        jScrollPaneObjects.getViewport().add(jTableObjects, null);
    }

    void jBtnNewProbe_actionPerformed(ActionEvent e) {
        openNewProbe();
    }

    private void openNewProbe() {
        if (jTableObjects.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Please select an element to probe first.",
                    "Probe an element of a list", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object o = dataModel.getObjectAtRow(jTableObjects.getSelectedRow());
        if (o == null) {
            JOptionPane.showMessageDialog(null, "The selected element is null.",
                    "Probe an element of a list", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String s = dataModel.getObjectNameAtRow(jTableObjects.getSelectedRow());
        ProbeFrame pF = new ProbeFrame(o, this.toString() + "." + s);
        pF.setVisible(true);
    }

    public void updateList() {
        dataModel.update();
        updateUI();
    }

    public Object getProbedObject() {
        return dataModel.getProbedObject();
    }

    void jTableObjects_mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2)
            openNewProbe();
    }
}
