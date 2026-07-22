package microsim.gui.space;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;

import microsim.gui.GuiUtils;

/**
 * Not of interest for users. A window used by LayeredSurfaceFrame
 * to choose the object to be probed when
 * user click on a cell of a LayerMultiObjectGridDrawer.
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
public class CellObjectChooser extends JDialog {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    JPanel panel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JList<Object> jListObjects = new JList<>();
    JPanel jPanel1 = new JPanel();
    JButton jBtnCancel = new JButton();
    JButton jBtnOK = new JButton();

    public CellObjectChooser(Object[] objs, Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        jListObjects.setListData(objs);
        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getSize().width) / 2,
                (d.height - getSize().height) / 2);
    }

    public CellObjectChooser(Object[] objs) {
        this(objs, null, "", false);
    }

    void jbInit() throws Exception {
        panel1.setLayout(borderLayout1);
        jBtnCancel.setText("Cancel");
        jBtnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnCancel_actionPerformed(e);
            }
        });
        jBtnOK.setText("Open probe");
        jBtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnOK_actionPerformed(e);
            }
        });
        jListObjects.setBorder(BorderFactory.createEtchedBorder());
        jListObjects.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jListObjects_mouseClicked(e);
            }
        });
        getContentPane().add(panel1);
        panel1.add(jListObjects, BorderLayout.CENTER);
        this.getContentPane().add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(jBtnCancel, null);
        jPanel1.add(jBtnOK, null);
    }

    void jListObjects_mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2)
            if (jListObjects.getSelectedValue() != null) {
                GuiUtils.openProbe(jListObjects.getSelectedValue(), "Selected object");
                dispose();
            }
    }

    void jBtnCancel_actionPerformed(ActionEvent e) {
        dispose();
    }

    void jBtnOK_actionPerformed(ActionEvent e) {
        if (jListObjects.getSelectedValue() != null)
            GuiUtils.openProbe(jListObjects.getSelectedValue(), "Selected object");
        else
            return;
        dispose();
    }
}
