package microsim.gui.shell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.sql.SQLException;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import microsim.data.db.DatabaseUtils;
import microsim.engine.SimulationEngine;

import org.h2.tools.Console;

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
 */
public class DatabaseExplorerFrame extends JInternalFrame {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    ImageIcon imageMiniPreferences = new ImageIcon(getClass().getResource(
            "/microsim/gui/icons/db.gif"));

    JButton jBtnClose = null;
    JButton jBtnDelete = null;
    JButton jBtnApply = null;
    JButton jBtnInit = null;
    JPanel jPanelProperties = null;
    JPanel jPanelButtons = null;

    JList<String> jList = null;
    File[] dirs = null;
    DefaultListModel<String> model = new DefaultListModel<String>();

    private javax.swing.JPanel mainContentPane = null;

    /**
     * Constructor.
     * 
     * @param engine
     *               The simulation engine to edit.
     */
    public DatabaseExplorerFrame(SimulationEngine engine) {
        initialize();
    }

    private void initialize() {
        // setIconImage(imageMiniPreferences.getImage());
        JScrollPane scrollPane = new JScrollPane(getMainContentPane());
        this.setContentPane(scrollPane);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setSize(new Dimension(450, 338));
        setTitle("Database Explorer");
        this.setResizable(true);

    }

    private JPanel getJPanelProperties() {
        if (jPanelProperties == null) {
            jPanelProperties = new JPanel();
            jPanelProperties.setLayout(new BorderLayout());

            jPanelProperties.add(new JLabel("Remember to disconnect database to come back"), BorderLayout.NORTH);
            jPanelProperties.add(getJList(), BorderLayout.CENTER);

        }
        return jPanelProperties;
    }

    private JList<String> getJList() {
        if (jList == null) {

            File outputDir = new File("output");
            // File[] dirs = outputDir.listFiles();
            dirs = outputDir.listFiles();
            if (dirs == null)
                dirs = new File[0];
            // String[] outDirs = new String[dirs.length + 1];
            // outDirs[0] = "INPUT";
            model.addElement("INPUT");
            for (int i = 0; i < dirs.length; i++) {
                File file = dirs[i];
                // outDirs[i + 1] = file.getName();
                model.addElement(file.getName());
            }

            // jList = new JList<String>(outDirs);
            jList = new JList<String>(model);
        }
        return jList;
    }

    void jBtnApply_actionPerformed(ActionEvent e) {
        if (jList.getSelectedValue() == null)
            return;

        try {
            if (jList.getSelectedIndex() == 0) // Added ";MVCC=TRUE;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO" in order to
                                               // allow input database to be inspected, closed and then the simulation
                                               // to be run. Without this, an exception is thrown as the database is
                                               // still connected.
                new Console()
                        .runTool(new String[] { "-url", "jdbc:h2:file:./input/input;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO",
                                "-user", "sa", "-password", "" });
            else
                new Console()
                        .runTool(new String[] { "-url", "jdbc:h2:file:./output/" + jList.getSelectedValue().toString()
                                + "/database/out;AUTO_SERVER=TRUE", "-user", "sa", "-password", "" });
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    void jBtnDelete_actionPerformed(ActionEvent e) {
        if (jList.getSelectedValue() == null)
            return;

        try {
            if (jList.getSelectedIndex() == 0) { // Don't delete input database!
                System.out.println("Only output databases can be deleted via the GUI!");
                return;
            } else {
                int indexToDelete = -1;
                for (int i = 0; i < dirs.length; i++) { // Cannot use jList.getSelectedIndex() to find dirs as index of
                                                        // dirs array is not updated after an element is deleted, unlike
                                                        // jList.
                    // System.out.println(dirs[i].getName() + ", jList selected value " +
                    // jList.getSelectedValue());
                    if (dirs[i].getName().equals(jList.getSelectedValue())) {
                        indexToDelete = i;
                        break;
                    }
                }
                if ((indexToDelete != -1) && deleteDirectory(dirs[indexToDelete].getAbsoluteFile())) { // Note that dirs
                                                                                                       // doesn't
                                                                                                       // contain
                                                                                                       // "INPUT" as
                                                                                                       // first entry,
                                                                                                       // unlike model.
                    model.removeElementAt(jList.getSelectedIndex());
                } else {
                    throw new FileSystemException(
                            "Database cannot be deleted; check that the database is not in use!  Try again, after closing all connections to the database or restarting the GUI.");
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Force deletion of directory
     * 
     * @param path
     * @return boolean
     */
    static private boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    void jBtnClose_actionPerformed(ActionEvent e) {
        dispose();
    }

    void jBtnInit_actionPerformed(ActionEvent e) {
        DatabaseUtils.databaseInputUrl = "./input/input";
        DatabaseUtils.inputSchemaUpdateEntityManger();
        try {
            new Console().runTool(new String[] { "-url", "jdbc:h2:file:./input/input;AUTO_SERVER=TRUE", "-user", "sa",
                    "-password", "" });
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    private javax.swing.JPanel getMainContentPane() {
        if (mainContentPane == null) {
            mainContentPane = new javax.swing.JPanel();
            mainContentPane.setLayout(new java.awt.BorderLayout());
            mainContentPane.add(getJPanelProperties(), java.awt.BorderLayout.CENTER);
            mainContentPane.add(getJPanelButtons(), java.awt.BorderLayout.NORTH);
        }
        return mainContentPane;
    }

    private javax.swing.JPanel getJPanelButtons() {
        if (jPanelButtons == null) {
            jPanelButtons = new javax.swing.JPanel();
            jPanelButtons.add(getJBtnInit(), null);
            jPanelButtons.add(getJBtnApply(), null);
            jPanelButtons.add(getJBtnDelete(), null);
            jPanelButtons.add(getJBtnClose(), null);
        }
        return jPanelButtons;
    }

    private javax.swing.JButton getJBtnClose() {
        if (jBtnClose == null) {
            jBtnClose = new javax.swing.JButton();
            jBtnClose.setText("Close");
            jBtnClose.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jBtnClose_actionPerformed(e);
                }
            });
        }
        return jBtnClose;
    }

    private javax.swing.JButton getJBtnDelete() {
        if (jBtnDelete == null) {
            jBtnDelete = new javax.swing.JButton();
            jBtnDelete.setText("Delete database");
            jBtnDelete.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jBtnDelete_actionPerformed(e);
                }
            });
        }
        return jBtnDelete;
    }

    private javax.swing.JButton getJBtnApply() {
        if (jBtnApply == null) {
            jBtnApply = new javax.swing.JButton();
            jBtnApply.setText("Show database");
            jBtnApply.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jBtnApply_actionPerformed(e);
                }
            });
        }
        return jBtnApply;
    }

    private javax.swing.JButton getJBtnInit() {
        if (jBtnInit == null) {
            jBtnInit = new javax.swing.JButton();
            jBtnInit.setText("Init input database");
            jBtnInit.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    jBtnInit_actionPerformed(e);
                }
            });
        }
        return jBtnInit;
    }
}
