package microsim.gui.shell;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

/**
 * An independent frame that is able to
 * grab System.out and System.err streams,
 * showing their content in a window. It is useful when the application
 * is launched with <i>javaw.exe</i> command, without terminal console.
 * <p>
 * It is possible to save the output in a file.
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
public class CaptureConsoleWindow extends JInternalFrame {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    ImageIcon imageIcon = new ImageIcon(
            CaptureConsoleWindow.class.getResource("/microsim/gui/icons/console.gif"));

    ConsoleTextArea cta = null;
    javax.swing.JScrollPane jScrollText = null;

    private javax.swing.JToolBar jToolBar = null;
    private javax.swing.JButton jBtnClear = null;
    private javax.swing.JButton jBtnSave = null;
    private javax.swing.JToggleButton jBtnRead = null;

    private javax.swing.JPanel jContentPane = null;

    public CaptureConsoleWindow() {
        initialize();
    }

    private void initialize() {
        this.setContentPane(getJContentPane());
        this.setSize(508, 263);
        this.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/microsim/gui/icons/tree.gif")));
        this.setTitle("Output stream");
        this.setResizable(true);
        this.setMaximizable(false);
        this.setIconifiable(false);
    }

    private ConsoleTextArea getJConsoleTextArea() {
        if (cta == null) {
            try {
                cta = new ConsoleTextArea();
                cta.setFont(java.awt.Font.decode("monospaced"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cta;
    }

    private javax.swing.JScrollPane getJScrollText() {
        if (jScrollText == null) {
            jScrollText = new JScrollPane(getJConsoleTextArea());
        }
        return jScrollText;
    }

    private void saveText() {
        JFileChooser jfc = new JFileChooser(new File("."));
        FileFilter ff = new FileFilter() {
            public boolean accept(File f) {
                return (f.getName().toLowerCase().endsWith(".txt") ||
                        f.isDirectory());
            }

            public String getDescription() {
                return "Text file (.txt)";
            }
        };
        jfc.setFileFilter(ff);

        int result = jfc.showSaveDialog(this);
        if (result == JFileChooser.CANCEL_OPTION)
            return;

        try {
            BufferedWriter f = new BufferedWriter(new FileWriter(jfc.getSelectedFile()));
            // f.write();

            // PrintWriter outp = new PrintWriter(
            // new FileWriter(jfc.getSelectedFile()) );
            // outp.println(cta.getText());
            // outp.close();

            // for (int i = 0; i < cta.getRows(); i++)
            f.write(cta.getText());

            f.close();
        } catch (Exception err) {
            // String msg = new String("Error writing file:\n"
            // + err.getMessage());
            String msg = "Error writing file:\n" // Modification by Ross (See J. Bloch "Effective Java" 2nd Edition,
                                                 // Item 5)
                    + err.getMessage();
            JOptionPane.showMessageDialog(this, msg,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        return;

    }

    public void dispose() {
        cta.dispose();
    }

    private void clearText() {
        cta.setText("");
    }

    /*
     * private void dump()
     * {
     * System.out.println("The java virtual machine has " +
     * (Runtime.getRuntime().totalMemory() / 1024)
     * + "Kb of memory.");
     * 
     * System.out.println("The current amount of free memory is " +
     * (Runtime.getRuntime().freeMemory() / 1024)
     * + " Kb.");
     * Properties sysProp = System.getProperties();
     * Enumeration enumItem = sysProp.propertyNames();
     * while (enumItem.hasMoreElements())
     * {
     * String key = (String) enumItem.nextElement();
     * System.out.println(key + "=" + sysProp.getProperty(key));
     * }
     * }
     */

    /**
     * This method initializes jToolBar
     * 
     * @return javax.swing.JToolBar
     */
    private javax.swing.JToolBar getJToolBar() {
        if (jToolBar == null) {
            jToolBar = new javax.swing.JToolBar();
            jToolBar.add(getJBtnClear());
            jToolBar.addSeparator();
            jToolBar.add(getJBtnSave());
            jToolBar.addSeparator();
            jToolBar.add(getJBtnRead());
        }
        return jToolBar;
    }

    /**
     * This method initializes jBtnClear
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnClear() {
        if (jBtnClear == null) {
            jBtnClear = new javax.swing.JButton();
            jBtnClear.setIcon(new ImageIcon(getClass().getResource("/microsim/gui/icons/clear16.gif")));
            jBtnClear.setToolTipText("Clear the content of the window");
            jBtnClear.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    clearText();
                }
            });
        }
        return jBtnClear;
    }

    /**
     * This method initializes jBtnSave
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnSave() {
        if (jBtnSave == null) {
            jBtnSave = new javax.swing.JButton();
            jBtnSave.setIcon(new ImageIcon(getClass().getResource("/microsim/gui/icons/Save16.gif")));
            jBtnSave.setToolTipText("Save the text");
            jBtnSave.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    saveText();
                }
            });
        }
        return jBtnSave;
    }

    public void changeReadingStatus() {
        if (cta.isReading())
            cta.stopReading();
        else
            cta.startReading();

        jBtnRead.setSelected(cta.isReading());
    }

    /**
     * This method initializes jBtnRead
     * 
     * @return javax.swing.JToggleButton
     */
    private javax.swing.JToggleButton getJBtnRead() {
        if (jBtnRead == null) {
            jBtnRead = new javax.swing.JToggleButton();
            jBtnRead.setIcon(new javax.swing.ImageIcon(getClass().getResource("/microsim/gui/icons/view.gif")));
            jBtnRead.setToolTipText("Enable/disable output stream listening");
            jBtnRead.setSelected(true);
            jBtnRead.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    changeReadingStatus();
                }
            });
        }
        return jBtnRead;
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
            jContentPane.add(getJScrollText(), java.awt.BorderLayout.CENTER);
            jContentPane.add(getJToolBar(), java.awt.BorderLayout.NORTH);
        }
        return jContentPane;
    }

    public void log(String message) {
        cta.log(message);
    }
} // @jve:visual-info decl-index=0 visual-constraint="10,10"
