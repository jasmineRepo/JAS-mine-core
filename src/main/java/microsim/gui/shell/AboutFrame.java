package microsim.gui.shell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.apache.commons.io.IOUtils;

/**
 * The about frame of the JAS application.
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
public class AboutFrame extends JFrame {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    ImageIcon imageIcon = new ImageIcon(java.awt.Toolkit.getDefaultToolkit()
            .getImage(
                    getClass().getResource(
                            "/microsim/gui/icons/msIco.gif")));

    javax.swing.JTabbedPane jTabbedPane = null;
    javax.swing.JPanel jMainPanel = null;

    javax.swing.JPanel jLicencePanel = null;
    javax.swing.JScrollPane jScrollLicence = null;
    javax.swing.JTextArea jLicenceText = null;

    javax.swing.JPanel jSystemPanel = null;
    javax.swing.JTable jSystemTable = null;
    javax.swing.JScrollPane jSystemScroll = null;

    javax.swing.JPanel jLibrariesPanel = null;
    javax.swing.JScrollPane jLibrariesScroll = null;
    javax.swing.JTable jLibrariesTable = null;

    private javax.swing.JPanel jContentPane = null;

    public AboutFrame() {
        initialize();
    }

    private void initialize() {
        this.setContentPane(getJContentPane());
        setIconImage(imageIcon.getImage());
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int) ((d.getWidth() - 400) / 2),
                (int) ((d.getHeight() - 400) / 2));
        setSize(450, 450);
        setTitle("About JAS-mine");
    }

    public javax.swing.JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.setBorder(new TitledBorder(""));
            jTabbedPane.add(getJMainPanel(), "About JAS-mine");
            jTabbedPane.add(getJLicensePanel(), "License");
            // jTabbedPane.add(getJLibrariesPanel(), "Libraries");
            jTabbedPane.add(getJSystemPanel(), "System");
        }
        return jTabbedPane;
    }

    public javax.swing.JPanel getJMainPanel() {
        if (jMainPanel == null) {
            jMainPanel = new AboutPanel();
        }
        return jMainPanel;
    }

    public javax.swing.JPanel getJLicensePanel() {
        if (jLicencePanel == null) {
            jLicencePanel = new JPanel();
            jLicencePanel.setLayout(new BorderLayout());
            jLicencePanel.setBorder(BorderFactory.createEtchedBorder());
            jLicencePanel.add(getJScollLicensePanel(), BorderLayout.CENTER);
        }
        return jLicencePanel;
    }

    public javax.swing.JScrollPane getJScollLicensePanel() {
        if (jScrollLicence == null) {
            jScrollLicence = new JScrollPane();
            jScrollLicence.setViewportView(getJLicenceText());
        }
        return jScrollLicence;
    }

    private String getLicence() {
        try {
            final BufferedInputStream bis = new BufferedInputStream(
                    AboutFrame.class.getResourceAsStream("/jasmine_license.txt"));
            return IOUtils.toString(bis);

        } catch (Exception e) {
            return "WARNING: No licence file found!";
        }
    }

    public javax.swing.JTextArea getJLicenceText() {
        if (jLicenceText == null) {
            jLicenceText = new JTextArea();
            jLicenceText.setText(getLicence());
            jLicenceText.setCaretPosition(0);
        }
        return jLicenceText;
    }

    public javax.swing.JPanel getJSystemPanel() {
        if (jSystemPanel == null) {
            jSystemPanel = new JPanel();
            jSystemPanel.setBorder(BorderFactory.createEtchedBorder());
            jSystemPanel.setLayout(new BorderLayout());
            jSystemPanel.add(getJSystemScroll(), BorderLayout.CENTER);
        }
        return jSystemPanel;
    }

    public javax.swing.JTable getJSystemTable() {
        if (jSystemTable == null) {
            jSystemTable = new JTable(getSystem(), getSystemCols());
        }
        return jSystemTable;
    }

    public javax.swing.JScrollPane getJSystemScroll() {
        if (jSystemScroll == null) {
            jSystemScroll = new JScrollPane();
            jSystemScroll.setViewportView(getJSystemTable());
        }
        return jSystemScroll;
    }

    private Object[] getSystemCols() {
        return new Object[] { "Variable", "Value" };
    }

    private Object[][] getSystem() {
        Properties sysProp = System.getProperties();
        Object[][] systemProps = new Object[sysProp.size() + 2][2];

        systemProps[0][0] = "JVM total memory";
        systemProps[0][1] = (Runtime.getRuntime().totalMemory() / 1024) + " Kb";
        systemProps[1][0] = "Used JVM memory";
        systemProps[1][1] = (Runtime.getRuntime().freeMemory() / 1024) + " Kb";

        Enumeration<?> enumItem = sysProp.propertyNames();
        int i = 2;
        while (enumItem.hasMoreElements()) {
            String key = (String) enumItem.nextElement();
            systemProps[i][0] = key;
            systemProps[i][1] = sysProp.getProperty(key);
            i++;
        }

        return systemProps;
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }
}
