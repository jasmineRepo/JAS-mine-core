package microsim.gui.probe;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

import java.lang.reflect.*;

/**
 * Not of interest for users.
 * A dialog used by the probe to get input parameters
 * from user.
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

public class MethodDialog extends JDialog {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    JPanel jPanelMain = new JPanel();
    JScrollPane jScrollPaneTable = new JScrollPane();
    MethodParameterDataModel parameters;// = new MethodParameterDataModel(null);
    JTable jTableMethods = new JTable();
    JButton jBtnCancel = new JButton();
    JButton jBtnExecute = new JButton();

    public boolean cancel;

    public MethodDialog(Frame frame, String title, boolean modal, Method m) {
        super(frame, title, modal);
        try {
            parameters = new MethodParameterDataModel(m);
            jTableMethods.setModel(parameters);

            for (int i = 0; i < jTableMethods.getColumnModel().getColumnCount(); i++)
                jTableMethods.getColumnModel().getColumn(i).setHeaderValue(
                        parameters.getHeaderText(i));

            jbInit();
            setSize(200, 200);
            setLocation(200, 200);
            setTitle("Enter parameters for method " + m.toString());
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public MethodDialog(Method m) {
        this(null, "", false, m);
    }

    void jbInit() throws Exception {
        jBtnExecute.setText("Execute");
        jBtnExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnExecute_actionPerformed(e);
            }
        });
        jBtnCancel.setText("Cancel");
        jBtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnCancel_actionPerformed(e);
            }
        });
        this.getContentPane().add(jPanelMain, BorderLayout.SOUTH);
        jPanelMain.add(jBtnCancel, null);
        jPanelMain.add(jBtnExecute, null);
        this.getContentPane().add(jScrollPaneTable, BorderLayout.CENTER);
        jScrollPaneTable.getViewport().add(jTableMethods, null);
    }

    void jBtnCancel_actionPerformed(ActionEvent e) {
        cancel = true;
        dispose();
    }

    void jBtnExecute_actionPerformed(ActionEvent e) {
        cancel = false;
        dispose();
    }

    public Object[] getParameters() {
        return parameters.getParams();
    }
}
