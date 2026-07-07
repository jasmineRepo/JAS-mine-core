package microsim.gui.shell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import microsim.engine.EngineListener;
import microsim.engine.SimulationEngine;
import microsim.event.Event;
import microsim.event.EventGroup;
import microsim.event.SystemEventType;

/**
 * Not of interest for users. The frame that controls engine parameters. It is
 * shown when the 'Show engine status' menu item of the Control Panel is
 * choosen.
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
public class EngineParametersFrame extends JInternalFrame implements EngineListener {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    private SimulationEngine currentEngine;
    private long oldSeed;

    ImageIcon imageMiniPreferences = new ImageIcon(getClass().getResource(
            "/microsim/gui/icons/engine16.gif"));

    JTabbedPane jTabbedPane = null;
    JButton jBtnClose = null;
    JPanel jPanelProperties = null;

    JPanel jPanelSeed = null;
    JPanel jPanelButtons = null;
    JButton jBtnApply = null;

    JButton jBtnCancel = null;
    JPanel jPanelEventList = null;

    JLabel jLblEventList = null;
    JPanel jPanelLineTime = null;
    JPanel jPanelLineSeed = null;
    JTextField jTxtSeed = null;
    JButton jBtnGenerateSeed = null;

    JTextField jTxtRunNumber = null;

    private javax.swing.JPanel mainContentPane = null;
    private javax.swing.JScrollPane jScrollPane = null;
    private javax.swing.JTree jTree = null;

    /**
     * Constructor.
     * 
     * @param engine
     *               The simulation engine to edit.
     */
    public EngineParametersFrame(SimulationEngine engine) {
        currentEngine = engine;
        initialize();
        refresh();
    }

    /** Update frame content according to current engine status. */
    public void refresh() {
        jTxtSeed.setText("" + currentEngine.getRandomSeed());
        oldSeed = currentEngine.getRandomSeed();
        jTxtRunNumber.setText("" + SimulationEngine.getInstance().getCurrentRunNumber());
        updateEventList();
        currentEngine.addEngineListener(this);

        jBtnApply.setEnabled(false);

    }

    private void initialize() {
        // setIconImage(imageMiniPreferences.getImage());
        this.setContentPane(getMainContentPane());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setSize(new Dimension(450, 338));
        setTitle("JAS-mine engine current status");
        this.setResizable(true);

    }

    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.add(getJPanelProperties(), "Engine properties");
            jTabbedPane.add(getJPanelEventList(), "Event list");
        }
        return jTabbedPane;
    }

    private JPanel getJPanelEventList() {
        if (jPanelEventList == null) {
            jPanelEventList = new JPanel();
            jPanelEventList.setLayout(new java.awt.BorderLayout());
            jPanelEventList
                    .add(getJLblEventList(), java.awt.BorderLayout.NORTH);
            jPanelEventList.add(getJScrollPane(), java.awt.BorderLayout.CENTER);
        }
        return jPanelEventList;
    }

    private JPanel getJPanelProperties() {
        if (jPanelProperties == null) {
            jPanelProperties = new JPanel();
            jPanelProperties.setLayout(new BorderLayout());
            jPanelProperties.add(getJPanelSeed(), BorderLayout.NORTH);
        }
        return jPanelProperties;
    }

    private JPanel getJPanelSeed() {
        if (jPanelSeed == null) {
            jPanelSeed = new JPanel();
            GridLayout gl = new GridLayout();
            gl.setColumns(1);
            gl.setRows(2);
            jPanelSeed.setLayout(gl);
            jPanelSeed.setBorder(BorderFactory.createEtchedBorder());
            jPanelSeed.setMinimumSize(new Dimension(317, 70));
            jPanelSeed.setPreferredSize(new Dimension(317, 70));
            jPanelSeed.add(getJPanelLineTime(), null);
            jPanelSeed.add(getJPanelLineSeed(), null);
        }
        return jPanelSeed;
    }

    private JPanel getJPanelLineSeed() {
        if (jPanelLineSeed == null) {
            jPanelLineSeed = new JPanel();
            FlowLayout fl = new FlowLayout();
            fl.setAlignment(FlowLayout.LEFT);
            jPanelLineSeed.setLayout(fl);

            jPanelLineSeed.add(new JLabel("Seed random number"), null);
            jPanelLineSeed.add(getJTxtSeed(), null);
            jPanelLineSeed.add(getJBtnGenerateSeed(), null);
        }
        return jPanelLineSeed;
    }

    private JButton getJBtnGenerateSeed() {
        if (jBtnGenerateSeed == null) {
            jBtnGenerateSeed = new JButton();
            jBtnGenerateSeed.setText("Generate seed");
            jBtnGenerateSeed
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            jBtnGenerateSeed_actionPerformed(e);
                        }
                    });
        }
        return jBtnGenerateSeed;
    }

    private JTextField getJTxtSeed() {
        if (jTxtSeed == null) {
            jTxtSeed = new JTextField();
            jTxtSeed.setPreferredSize(new Dimension(150, 22));
            jTxtSeed.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jTxtSeed_actionPerformed(e);
                }
            });
            jTxtSeed.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    jTxtSeed_keyTyped(e);
                }
            });
        }
        return jTxtSeed;
    }

    private JPanel getJPanelLineTime() {
        if (jPanelLineTime == null) {
            jPanelLineTime = new JPanel();
            FlowLayout flowLayout = new FlowLayout();
            jPanelLineTime.setLayout(flowLayout);
            flowLayout.setAlignment(FlowLayout.LEFT);

            jPanelLineTime.add(Box.createHorizontalStrut(8), null);
            jPanelLineTime.add(new JLabel("Run #"), null);
            jPanelLineTime.add(getJTxtRunNumber(), null);
            jPanelLineTime.add(Box.createHorizontalStrut(8), null);
        }
        return jPanelLineTime;
    }

    private JTextField getJTxtRunNumber() {
        if (jTxtRunNumber == null) {
            jTxtRunNumber = new JTextField();
            jTxtRunNumber.setPreferredSize(new Dimension(50, 22));
            jTxtRunNumber.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    jTxtRunNumber_keyTyped(e);
                }
            });
        }
        return jTxtRunNumber;
    }

    void jBtnClose_actionPerformed(ActionEvent e) {
        jBtnApply_actionPerformed(e);
        close();
    }

    void jBtnGenerateSeed_actionPerformed(ActionEvent e) {
        jTxtSeed.setText("" + System.currentTimeMillis());
        jBtnApply.setEnabled(true);
    }

    void jBtnApply_actionPerformed(ActionEvent e) {
        long newSeed = Long.parseLong(jTxtSeed.getText());
        if (oldSeed != newSeed)
            currentEngine.setRandomSeed(newSeed);

        SimulationEngine.getInstance().setCurrentRunNumber(Integer.parseInt(jTxtRunNumber.getText()));

        jBtnApply.setEnabled(false);
    }

    void jBtnCancel_actionPerformed(ActionEvent e) {
        close();
    }

    private void close() {
        currentEngine.removeEngineListener(this);
        dispose();
    }

    private void updateEventList() {
        currentEngine.pause();
        Event[] eventArray = currentEngine.getEventQueue().getEventArray();

        DefaultMutableTreeNode root = new DefaultMutableTreeNode();

        for (int i = 0; i < eventArray.length; i++) {
            Event event = eventArray[i];
            DefaultMutableTreeNode folderNode = new DefaultMutableTreeNode(
                    event);
            root.add(folderNode);
            if (event instanceof EventGroup) {
                Event[] subEvents = ((EventGroup) event).eventsToArray();
                for (int j = 0; j < subEvents.length; j++) {
                    DefaultMutableTreeNode leafNode = new DefaultMutableTreeNode(
                            subEvents[j]);
                    folderNode.add(leafNode);
                }
            }
        }
        jTree.setModel(new DefaultTreeModel(root));
    }

    void jTxtSeed_keyTyped(KeyEvent e) {
        jBtnApply.setEnabled(true);
    }

    void jCmbTimeUnit_actionPerformed(ActionEvent e) {
        jBtnApply.setEnabled(true);
    }

    void jTxtSeed_actionPerformed(ActionEvent e) {
        jBtnApply.setEnabled(true);
    }

    void jTxtRunNumber_keyTyped(KeyEvent e) {
        jBtnApply.setEnabled(true);
    }

    private javax.swing.JPanel getMainContentPane() {
        if (mainContentPane == null) {
            mainContentPane = new javax.swing.JPanel();
            mainContentPane.setLayout(new java.awt.BorderLayout());
            mainContentPane.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
            mainContentPane
                    .add(getJPanelButtons(), java.awt.BorderLayout.SOUTH);
        }
        return mainContentPane;
    }

    private javax.swing.JPanel getJPanelButtons() {
        if (jPanelButtons == null) {
            jPanelButtons = new javax.swing.JPanel();
            jPanelButtons.add(getJBtnApply(), null);
            jPanelButtons.add(getJBtnCancel(), null);
            jPanelButtons.add(getJBtnClose(), null);
        }
        return jPanelButtons;
    }

    private javax.swing.JButton getJBtnClose() {
        if (jBtnClose == null) {
            jBtnClose = new javax.swing.JButton();
            jBtnClose.setText("OK");
            jBtnClose.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jBtnClose_actionPerformed(e);
                }
            });
        }
        return jBtnClose;
    }

    private javax.swing.JButton getJBtnCancel() {
        if (jBtnCancel == null) {
            jBtnCancel = new javax.swing.JButton();
            jBtnCancel.setText("Cancel");
            jBtnCancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jBtnCancel_actionPerformed(e);
                }
            });
        }
        return jBtnCancel;
    }

    private javax.swing.JButton getJBtnApply() {
        if (jBtnApply == null) {
            jBtnApply = new javax.swing.JButton();
            jBtnApply.setText("Apply changes");
            jBtnApply.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jBtnApply_actionPerformed(e);
                }
            });
        }
        return jBtnApply;
    }

    private javax.swing.JLabel getJLblEventList() {
        if (jLblEventList == null) {
            jLblEventList = new javax.swing.JLabel();
            jLblEventList.setBorder(BorderFactory.createEtchedBorder());
            jLblEventList.setText("Current event list");
        }
        return jLblEventList;
    }

    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private javax.swing.JScrollPane getJScrollPane() {
        if (jScrollPane == null) {
            jScrollPane = new javax.swing.JScrollPane();
            jScrollPane.setViewportView(getJTree());
        }
        return jScrollPane;
    }

    /**
     * This method initializes jTree
     * 
     * @return javax.swing.JTree
     */
    private javax.swing.JTree getJTree() {
        if (jTree == null) {
            jTree = new javax.swing.JTree();
            jTree.setRootVisible(false);
        }
        return jTree;
    }

    /** Update event list after a step is performed by event list. */
    public void onEngineEvent(SystemEventType event) {
        updateEventList();
    }
}
