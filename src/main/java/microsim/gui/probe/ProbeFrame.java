package microsim.gui.probe;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.lang.reflect.Method;

/**
 * The probe window class. It is able to inspect content of objects.
 * If the probed object implements the IProbeFields interface
 * the inspected fields are the only ones specified by the getProbeFields()
 * method. Otherwise, the object will be completely inspected.
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

public class ProbeFrame extends JFrame {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private String frameName = "";
    private String objectContent = "";

    private VariableDataModel variables; // = new VariableDataModel(this);
    private MethodsDataModel methods;
    private List<PanelObjectCollection> openedPanels;
    protected Object probedObject;

    // ImageIcon imageIcon = new ImageIcon(
    // ProbeFrame.class.getResource("/jas/images/Find16.gif"));

    // Main Frame
    JTabbedPane jTabbedPaneMain = new JTabbedPane();
    JPanel jPanelLowerButtons = new JPanel();

    // First tab
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel jPaneVariables = new JPanel();
    JTable jTableVariables = new JTable();
    JScrollPane jScrollVariables = new JScrollPane(jTableVariables);
    JPanel jPaneVariablesButtons = new JPanel();
    JButton jBtnNewProbe = new JButton();
    JButton jBtnListValues = new JButton();

    // Second tab
    BorderLayout borderLayout2 = new BorderLayout();
    JPanel jPaneMethods = new JPanel();
    JList jListMethods = new JList();
    JScrollPane jScrollMethods = new JScrollPane(jListMethods);
    JButton jBtnInvoke = new JButton();

    // Lower buttons
    JButton jBtnOK = new JButton();
    JButton jBtnRefresh = new JButton();
    JToggleButton jBtnPrivate = new JToggleButton();
    FlowLayout flowLayout1 = new FlowLayout();
    JPanel jNorthPanel = new JPanel();
    JLabel jObjectName = new JLabel("");
    BorderLayout borderLayout3 = new BorderLayout();
    JComboBox jCmbSuperclass = new JComboBox();

    /**
     * This constructor checks if the given object implements the IProbeFields
     * interface.
     * 
     * @param o    The object to probe.
     * @param name The title of the frame window.
     */
    public ProbeFrame(Object o, String name) {
        if (o instanceof IProbeFields) {
            variables = new VariableDataModel(o);
            methods = new MethodsDataModel(o);
            setup(o, name);
            jBtnPrivate.setVisible(false);
            jCmbSuperclass.setVisible(false);
            jNorthPanel.setPreferredSize(new Dimension(200, 22));
        } else {
            variables = new VariableDataModel(o, true);
            methods = new MethodsDataModel(o, true);
            setup(o, name);
        }
    }

    /**
     * This constructor ignores the IProbeFields interface and shows all the
     * fields of the object.
     * 
     * @param o             The object to probe.
     * @param name          The title of the frame window.
     * @param privateFields If true the probe will show only the public
     *                      properties and method. If false it will be shown public,
     *                      protected
     *                      and private fields.
     */
    public ProbeFrame(Object o, String name, boolean privateFields) {
        variables = new VariableDataModel(o, privateFields);
        methods = new MethodsDataModel(o, privateFields);
        setup(o, name);
    }

    private void setup(Object o, String name) {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
        if (o == null) {
            this.dispose();
            return;
        }

        probedObject = o;
        frameName = name;
        objectContent = o.getClass().getName() + " (" + o.toString() + ")";

        jTableVariables.setModel(variables);
        jListMethods.setModel(methods);
        openedPanels = new ArrayList<PanelObjectCollection>();

        try {
            jbInit();

            if (ProbeReflectionUtils.isCollection(o.getClass()) || o.getClass().isArray())
                addCollectionPanel(o);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Class<?> cl = o.getClass();
        while (cl != null) {
            jCmbSuperclass.addItem(cl.getName());
            cl = cl.getSuperclass();
        }

        // setIconImage(imageIcon.getImage());
    }

    /** Show off the frame window. */
    public void dispose() {
        probedObject = null;
        variables = null;
        methods = null;
        ;
        openedPanels.clear();
        super.dispose();
    }

    private void jbInit() throws Exception {
        // Build variable tab
        jPaneVariables.setLayout(borderLayout1);
        jTableVariables.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        jTableVariables.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jTableVariables_mouseClicked(e);
            }
        });

        for (int i = 0; i < jTableVariables.getColumnModel().getColumnCount(); i++)
            jTableVariables.getColumnModel().getColumn(i).setHeaderValue(
                    variables.getHeaderText(i));

        jBtnNewProbe.setText("Open probe on selected variable");
        jBtnNewProbe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnNewProbe_actionPerformed(e);
            }
        });
        jBtnListValues.setText("List values");
        jBtnListValues.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnListValues_actionPerformed(e);
            }
        });
        jBtnPrivate.setSelected(true);
        jBtnPrivate.setText("Private");
        jBtnPrivate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnPrivate_actionPerformed(e);
            }
        });
        jListMethods.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                jListMethods_mouseClicked(e);
            }
        });
        jBtnPrivate.setSelected(false);
        jPaneVariablesButtons.setLayout(flowLayout1);
        jObjectName.setText(objectContent);
        jNorthPanel.setLayout(borderLayout3);
        jCmbSuperclass.setMinimumSize(new Dimension(200, 22));
        jCmbSuperclass.setPreferredSize(new Dimension(200, 22));
        jCmbSuperclass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jCmbSuperclass_actionPerformed(e);
            }
        });
        jNorthPanel.setPreferredSize(new Dimension(200, 44));
        jPaneVariablesButtons.add(jBtnListValues, null);
        jPaneVariablesButtons.add(jBtnNewProbe, null);
        jPaneVariables.add(jPaneVariablesButtons, BorderLayout.SOUTH);
        jPaneVariables.add(jScrollVariables, BorderLayout.CENTER);

        // Build methods tab
        jPaneMethods.setLayout(borderLayout2);
        jBtnInvoke.setText("Execute selected method");
        jBtnInvoke.setVerticalAlignment(SwingConstants.BOTTOM);
        jBtnInvoke.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnInvoke_actionPerformed(e);
            }
        });
        jPaneMethods.add(jBtnInvoke, BorderLayout.SOUTH);
        jPaneMethods.add(jScrollMethods, BorderLayout.CENTER);
        this.getContentPane().add(jNorthPanel, BorderLayout.NORTH);

        // Build main frame

        jBtnOK.setText("Close");
        jBtnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnOK_actionPerformed(e);
            }
        });
        jBtnRefresh.setText("Refresh");
        jBtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnRefresh_actionPerformed(e);
            }
        });
        this.getContentPane().add(jPanelLowerButtons, BorderLayout.SOUTH);
        jPanelLowerButtons.add(jBtnPrivate, null);
        jPanelLowerButtons.add(jBtnRefresh, null);
        jPanelLowerButtons.add(jBtnOK, null);

        this.getContentPane().add(jTabbedPaneMain, BorderLayout.CENTER);
        jTabbedPaneMain.add(jPaneVariables, "Variables");
        jTabbedPaneMain.add(jPaneMethods, "Methods");
        jNorthPanel.add(jObjectName, BorderLayout.CENTER);

        setSize(new Dimension(414, 403));
        setLocation(0, 100);
        setTitle(frameName);
        jNorthPanel.add(jCmbSuperclass, BorderLayout.SOUTH);
    }

    void jBtnOK_actionPerformed(ActionEvent e) {
        this.dispose();
    }

    void jBtnRefresh_actionPerformed(ActionEvent e) {
        refreshData();
    }

    private void refreshData() {
        variables.update();
        jTableVariables.updateUI();
        methods.update();
        jListMethods.updateUI();
        openedPanels.forEach(PanelObjectCollection::updateList);
    }

    void jBtnNewProbe_actionPerformed(ActionEvent e) {
        openNewProbe();
    }

    void jBtnInvoke_actionPerformed(ActionEvent e) {
        invokeMethod();
    }

    void jBtnListValues_actionPerformed(ActionEvent e) {
        if (jTabbedPaneMain.getSelectedComponent() != jPaneVariables)
            return;

        if (jTableVariables.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Please select a variable to list first.",
                    "Probe variable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object o = variables.getObjectAtRow(jTableVariables.getSelectedRow());
        if (o == null) {
            JOptionPane.showMessageDialog(null, "The selected variable is null.",
                    "Probe variable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (ProbeReflectionUtils.isCollection(o.getClass()) || o.getClass().isArray()) {
            String s = variables.getObjectNameAtRow(jTableVariables.getSelectedRow());
            addCollectionPanel(o, s);
        } else {
            JOptionPane.showMessageDialog(null, "The selected variable is not a collection.",
                    "Probe variable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
    }

    private void addCollectionPanel(Object o) {
        PanelObjectCollection pn = new PanelObjectCollection(o);
        jTabbedPaneMain.add(pn, "List values", 0);
        openedPanels.add(pn);
    }

    private void addCollectionPanel(Object o, String s) {
        Iterator<?> it = openedPanels.iterator();
        while (it.hasNext())
            if (((PanelObjectCollection) it.next()).getProbedObject() == o)
                return;

        PanelObjectCollection pn = new PanelObjectCollection(o);
        jTabbedPaneMain.add(pn, s);
        jTabbedPaneMain.setSelectedIndex(jTabbedPaneMain.getTabCount() - 1);
        openedPanels.add(pn);
    }

    void jBtnPrivate_actionPerformed(ActionEvent e) {
        variables.setViewPrivate(jBtnPrivate.isSelected());
        methods.setViewPrivate(jBtnPrivate.isSelected());
        refreshData();
    }

    private void openNewProbe() {
        if (jTabbedPaneMain.getSelectedComponent() != jPaneVariables)
            return;

        if (jTableVariables.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(null, "Please select a variable to probe first.",
                    "Probe variable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Object o = variables.getObjectAtRow(jTableVariables.getSelectedRow());
        if (o == null) {
            JOptionPane.showMessageDialog(null, "The selected variable is null.",
                    "Probe variable", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String s = variables.getObjectNameAtRow(jTableVariables.getSelectedRow());
        ProbeFrame pF = new ProbeFrame(o, this.getTitle() + "." + s);
        pF.setVisible(true);
    }

    private void invokeMethod() {
        Object[] params = {};
        if (jListMethods.getSelectedIndex() == -1)
            return;

        Method m = (Method) methods.getElementAt(jListMethods.getSelectedIndex());
        if (!ProbeReflectionUtils.isAnExecutableMethod(m)) {
            JOptionPane.showMessageDialog(null,
                    "Sorry but this method requires complex arguments.\nThis function is not yet implemented.",
                    "Invoke method", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (m.getParameterTypes().length > 0) {
            MethodDialog md = new MethodDialog(null, "P", true, m);
            md.setVisible(true);
            if (md.cancel)
                return;
            params = md.getParameters();
        }

        if (jListMethods.getSelectedIndex() < 0) {
            JOptionPane.showMessageDialog(null, "Please select a method to invoke first.",
                    "Invoke method", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (m.getParameterTypes().length > 0)
            methods.invokeMethodAt(jListMethods.getSelectedIndex(), params);
        else
            methods.invokeMethodAt(jListMethods.getSelectedIndex());
    }

    void jTableVariables_mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2)
            openNewProbe();
    }

    void jListMethods_mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2)
            invokeMethod();
    }

    void jCmbSuperclass_actionPerformed(ActionEvent e) {
        if (jCmbSuperclass.getSelectedIndex() < 0)
            return;
        variables.setDeepLevel(jCmbSuperclass.getSelectedIndex());
        methods.setDeepLevel(jCmbSuperclass.getSelectedIndex());
        refreshData();

    }

}
