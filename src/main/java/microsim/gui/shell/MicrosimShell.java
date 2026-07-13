package microsim.gui.shell;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import com.formdev.flatlaf.FlatLightLaf;

import microsim.engine.EngineListener;
import microsim.engine.SimulationEngine;
import microsim.engine.SimulationManager;
import microsim.event.SystemEventType;
import microsim.exception.SimulationException;
import microsim.gui.GuiUtils;
import microsim.gui.shell.parameter.ParameterFrame;
import microsim.gui.shell.parameter.ParameterInspector;

/**
 * The JAS object is tne main GUI window. It represents the simulation
 * environment for the user's simulation models.
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
 * 
 */
public class MicrosimShell extends JFrame {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;

    public static final double scale = 1.;

    private static String settingsFileName = "jas.ini";

    /** The frame title. */
    public static String frameTitle = "JAS-mine";

    private final SimulationController controller = new SimulationController(this);

    private CaptureConsoleWindow consoleWindow = null;

    private javax.swing.JPanel jContentPane = null;

    private javax.swing.JMenuBar jJMenuBar = null;

    private javax.swing.JPanel jPanelTop = null;

    private javax.swing.JToolBar jToolBar = null;

    private javax.swing.JLabel jLabelTime = null;

    private javax.swing.JMenuItem jMenuFileExit = null;

    private javax.swing.JButton jBtnBuild = null;

    private javax.swing.JButton jBtnReload = null;

    private javax.swing.JButton jBtnPlay = null;

    private javax.swing.JButton jBtnStep = null;

    private javax.swing.JButton jBtnPause = null;

    private javax.swing.JButton jBtnUpdateParams = null;

    private javax.swing.JMenu jMenuSimulation = null;

    private javax.swing.JMenu jMenuTools = null;

    private javax.swing.JMenu jMenuHelp = null;

    private javax.swing.JMenuItem jMenuSimulationRestart = null;

    private javax.swing.JMenuItem jMenuSimulationPlay = null;

    private javax.swing.JMenuItem jMenuSimulationStep = null;

    private javax.swing.JMenuItem jMenuSimulationPause = null;

    private javax.swing.JMenuItem jMenuSimulationUpdateParams = null;

    private javax.swing.JMenuItem jMenuSimulationStop = null;

    private javax.swing.JMenuItem jMenuSimulationBuild = null;

    private javax.swing.JMenuItem jMenuSimulationEngine = null;

    private javax.swing.JPanel jPanelTime = null;

    private javax.swing.JLabel jLabelCurTime = null;

    private javax.swing.JPanel jPanelSlider = null;

    private javax.swing.JLabel jLabelSlider = null;

    private javax.swing.JCheckBox jSilentCheck = null;

    private javax.swing.JSlider jSlider = null;

    private javax.swing.JDesktopPane jDesktopPane = null;

    private javax.swing.JSplitPane jSplitPane = null;

    private javax.swing.JLabel jNullLabel = null;

    // private javax.swing.JMenuItem jMenuToolsOption = null;

    private javax.swing.JMenuItem jMenuToolsWindowPositions = null;

    private javax.swing.JMenuItem jMenuToolsDatabaseExplorer = null;

    private javax.swing.JMenuItem jMenuHelpAbout = null;

    // private javax.swing.JMenuItem jMenuHelpWebSite = null;

    private javax.swing.JSplitPane jSplitInternalDesktop = null; // @jve:visual-info
                                                                 // decl-index=0
                                                                 // visual-constraint="756,296"

    public static MicrosimShell currentShell;

    /**
     * This is the default full constructor
     */
    public MicrosimShell(SimulationEngine engine) {
        super();

        controller.openConfig();
        controller.attachToSimEngine(engine);

        initialize();

        // engine.setWindowManager(controller);
        // controller.refreshMRUMenu();
        setInitButtonStatus();

        // this.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        currentShell = this;

        this.pack();
        this.setSize((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth(),
                (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - 30);
        this.setVisible(true);
        jSplitInternalDesktop.setDividerLocation(jSplitInternalDesktop.getHeight() * 4 / 5);
    }

    public SimulationController getController() {
        return controller;
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(727, 426); // Was left commented out by Michele, but I (Ross) think it's better to have
                                // this to allow the user to immediately see the parameter boxes at the top half
                                // of the shell.
        this.setContentPane(getJContentPane());
        this.setJMenuBar(getJJMenuBar());
        this.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/microsim/gui/icons/logo_2.png")));
        this.setTitle("JAS-mine");

        this.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                controller.quitEngine();
            }
        });
        try {
            UIManager// .setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                     // .setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
                     // .setLookAndFeel("net.infonode.gui.laf.InfoNodeLookAndFeel");
                    .setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.out.println("Error loading L&F " + e);
        }

        consoleWindow = new CaptureConsoleWindow();
        jSplitInternalDesktop.setBottomComponent(consoleWindow);
        consoleWindow.show();
    }

    public void attachToSimEngine(SimulationEngine engine) {
        controller.attachToSimEngine(engine);
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    public javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getJPanelTop(), java.awt.BorderLayout.NORTH);
            jContentPane.add(getJSplitPane(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes jJMenuBar
     * 
     * @return javax.swing.JMenuBar
     */
    private javax.swing.JMenuBar getJJMenuBar() {
        if (jJMenuBar == null) {
            jJMenuBar = new javax.swing.JMenuBar();
            // jJMenuBar.add(getJMenuFile());
            jJMenuBar.add(getJMenuSimulation());
            jJMenuBar.add(getJMenuTools());
            jJMenuBar.add(getJMenuHelp());
        }
        return jJMenuBar;
    }

    /**
     * This method initializes jPanelTop
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJPanelTop() {
        if (jPanelTop == null) {
            jPanelTop = new javax.swing.JPanel();
            jPanelTop.setLayout(new java.awt.BorderLayout());
            jPanelTop.add(getJToolBar(), java.awt.BorderLayout.NORTH);
            jPanelTop.add(getJPanelTime(), java.awt.BorderLayout.CENTER);
            jPanelTop
                    .setBorder(javax.swing.BorderFactory
                            .createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        }
        return jPanelTop;
    }

    /**
     * This method initializes jToolBar
     * 
     * @return javax.swing.JToolBar
     */
    private javax.swing.JToolBar getJToolBar() {
        if (jToolBar == null) {
            jToolBar = new javax.swing.JToolBar();
            // jToolBar.add(getJBtnLoad());
            jToolBar.addSeparator();
            jToolBar.add(getJBtnReload());
            jToolBar.addSeparator();
            jToolBar.add(getJBtnBuild());
            jToolBar.addSeparator();
            jToolBar.addSeparator();
            jToolBar.add(getJBtnPlay());
            jToolBar.add(getJBtnStep());
            // jToolBar.add(getJBtnTimeStep());
            jToolBar.add(getJBtnPause());
            jToolBar.add(getJBtnUpdateParameters());
            jToolBar.addSeparator();
            // jToolBar.add(getJSilentCheck()); //Ross: This has been removed in order to
            // avoid misuse by inexperienced users, who might try to import/export to the
            // database despite switching the connection off. Now all JAS-mine models
            // launched from the GUI will automatically have the database connection
            // created. If the user wants to turn this connection off, they can do so
            // programmatically by setting turnOffDatabaseConnection to true in the Start
            // class template of the simulation project created by the JAS-mine plugin for
            // Eclipse IDE.
            // jToolBar.addSeparator();
            jToolBar.addSeparator(new Dimension(50, 30));
            jToolBar.add(getJPanelSlider());
            jToolBar.setPreferredSize(new java.awt.Dimension(414, 40));
        }
        return jToolBar;
    }

    /**
     * This method initializes jLabelTime
     * 
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelTime() {
        if (jLabelTime == null) {
            jLabelTime = new javax.swing.JLabel();
            jLabelTime.setFont(new Font(jLabelTime.getFont().getFontName(), jLabelTime.getFont().getStyle(),
                    (int) (scale * jLabelTime.getFont().getSize())));
        }
        return jLabelTime;
    }

    /**
     * This method initializes jMenuFileExit
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuFileExit() {
        if (jMenuFileExit == null) {
            jMenuFileExit = new javax.swing.JMenuItem();
            jMenuFileExit.setText("Quit");
            jMenuFileExit.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/quit.gif")));
            jMenuFileExit
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.quitEngine();
                        }
                    });
        }
        return jMenuFileExit;
    }

    /**
     * This method initializes jBtnBuild
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnBuild() {
        if (jBtnBuild == null) {
            jBtnBuild = new javax.swing.JButton();
            jBtnBuild.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "/microsim/gui/icons/simulation_build.gif")));
            jBtnBuild.setToolTipText("Build simulation model");
            jBtnBuild.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    controller.buildModel();
                }
            });
        }
        return jBtnBuild;
    }

    public void setTimeLabel(String newTime) {
        jLabelTime.setText(newTime);
    }

    /** Enable and disable the simulation buttons as the initial state. */
    public void setInitButtonStatus() {
        jBtnPlay.setEnabled(false);
        jMenuSimulationPlay.setEnabled(false);
        jBtnStep.setEnabled(false);
        jMenuSimulationStep.setEnabled(false);
        jMenuSimulationStop.setEnabled(false);
        jMenuSimulationPause.setEnabled(false);
        jMenuSimulationUpdateParams.setEnabled(false);
        jBtnPause.setEnabled(false);
        jBtnUpdateParams.setEnabled(false);
        jBtnBuild.setEnabled(true);
        jMenuSimulationBuild.setEnabled(true);
        jMenuSimulationRestart.setEnabled(false);
        jMenuSimulation.revalidate();
        jMenuSimulation.repaint();

        getContentPane().setCursor(
                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /** Enable and disable the simulation buttons according to the built state. */
    public void setBuiltButtonStatus() {
        jBtnPlay.setEnabled(true);
        jMenuSimulationPlay.setEnabled(true);
        jBtnStep.setEnabled(true);
        jMenuSimulationStep.setEnabled(true);
        jMenuSimulationStop.setEnabled(true);
        jBtnBuild.setEnabled(false);
        jMenuSimulationPause.setEnabled(true);
        jMenuSimulationUpdateParams.setEnabled(true);
        jBtnPause.setEnabled(true);
        jBtnUpdateParams.setEnabled(true);
        jMenuSimulationBuild.setEnabled(false);
        jMenuSimulationRestart.setEnabled(true);
        jMenuSimulation.revalidate();
        jMenuSimulation.repaint();
    }

    public static class RootViews extends FileSystemView {

        public File[] getRoots() {
            File[] oldRoots = super.getRoots();
            File[] roots = new File[2 + oldRoots.length];
            // roots[0] = new File(Sim.jasProjectsPath);
            // roots[1] = new File(Sim.getStartDirectory());
            System.arraycopy(oldRoots, 0, roots, 2, oldRoots.length);

            return roots;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.filechooser.FileSystemView#createNewFolder(java.io.File)
         */
        public File createNewFolder(File arg0) throws IOException {
            String newFolder = JOptionPane.showInputDialog(null,
                    "Type the name of the new folder");
            if (newFolder == null)
                return null;

            File newFile = new File(arg0, newFolder);

            if (newFile.mkdir())
                return newFile;
            else
                return null;
        }
    }

    public class SimulationController implements EngineListener {

        private SimulationEngine callerEngine;

        // private HashMap<String, SimulationWindow> windowBag;

        private Properties settings;

        JFileChooser jfc;

        private MicrosimShell jasWindow;

        private List<ParameterFrame> parameterFrames = new ArrayList<ParameterFrame>();

        public SimulationController(MicrosimShell owner) {
            jasWindow = owner;
            // windowBag = new HashMap<String, SimulationWindow>();
        }

        private void openConfig() {
            settings = new Properties();
            settings.setProperty("LookAndFeel", "");

            settings.setProperty("ProjectsPath", "");
            settings.setProperty("EditorPath", "");

            try {
                settings.load(new java.io.FileInputStream(settingsFileName));
            } catch (java.io.IOException ioe) {
            }

            if (!settings.getProperty("ProjectsPath").equals("")) {
                File fl = new File(settings.getProperty("ProjectsPath"));
                if (!fl.exists())
                    JOptionPane
                            .showMessageDialog(
                                    jasWindow,
                                    "The JAS-mine projects path does no more exist on this file system.\n "
                                            + "Please check it from the Tool\\JAS Options menu");
                else
                    // Sim.jasProjectsPath = settings.getProperty("ProjectsPath");
                    ;
            }

            // if (!settings.getProperty("EditorPath").equals(""))
            // Sim.setEditorPath(settings.getProperty("EditorPath"));
        }

        public void showProperties() {
            boolean st = callerEngine.getRunningStatus();
            callerEngine.pause();

            EngineParametersFrame paramFrame = new EngineParametersFrame(callerEngine);
            getJDesktopPane().add(paramFrame);
            paramFrame.show();
            callerEngine.setRunningStatus(st);
        }

        public void setTurnOffDatabaseConnection(boolean turnOffDatabaseConnection) {
            callerEngine.setTurnOffDatabaseConnection(turnOffDatabaseConnection);
        }

        public boolean isTurnOffDatabaseConnection() {
            return callerEngine.isTurnOffDatabaseConnection();
        }

        public void showDatabaseExplorer() {
            boolean st = callerEngine.getRunningStatus();
            callerEngine.pause();

            DatabaseExplorerFrame dbFrame = new DatabaseExplorerFrame(callerEngine);
            getJDesktopPane().add(dbFrame);
            dbFrame.show();
            callerEngine.setRunningStatus(st);
        }

        public void editProperties() {
            // (new JASParameters(jasWindow, settings)).setVisible(true);
        }

        public void startModel() {
            callerEngine.startSimulation();
        }

        public void pauseModel() {
            callerEngine.pause();
        }

        public void updateModelParams() {
            for (ParameterFrame parameterFrame : parameterFrames) {
                parameterFrame.save();
            }
        }

        public void stopModel() {
            callerEngine.performAction(SystemEventType.Stop);
        }

        public void restartModel() {
            callerEngine.pause();

            closeCurrentModels();
            callerEngine.rebuildModels();

            // attachToSimEngine(callerEngine);

            setInitButtonStatus();
        }

        // public void showProperties() {
        // boolean st = callerEngine.getRunningStatus();
        // callerEngine.stop();
        //
        // EngineParametersFrame paramFrame = new EngineParametersFrame(
        // callerEngine, jasWindow);
        // getJDesktopPane().add(paramFrame);
        // paramFrame.show();
        // callerEngine.setRunning(st);
        // }
        //
        // public void changeEventTimeTreshold(int value) {
        // callerEngine.getEventList().setEventTimeTreshold(value);
        // }

        public void attachToSimEngine(SimulationEngine engine) {
            callerEngine = engine;
            callerEngine.addEngineListener(this);
        }

        public void doStep() throws SimulationException {
            callerEngine.pause();
            callerEngine.step();
        }

        public void changeEventTimeTreshold(int value) {
            callerEngine.setEventTimeTreshold(value);
        }

        // public void showAPI(String relativeDir, String fileName) {
        // String path = Sim.getStartDirectory() + relativeDir;
        //
        // String command;
        // if (System.getProperty("file.separator").equals("/")) {
        // command = "netscape " + path + "/" + fileName;
        // } else
        // command = "cmd /C start /D\"" + path.replace('/', '\\') + "\" " + fileName;
        //
        // try {
        // Runtime.getRuntime().exec(command);
        // } catch (Exception ex) {
        // }
        // }

        public void navigateWebSite() {
            var path = "http://www.jas-mine.net";
            ProcessBuilder cmd;
            if (System.getProperty("file.separator").equals("/"))
                cmd = new ProcessBuilder("netscape", path);
            else
                cmd = new ProcessBuilder("cmd", "/C", "start", "path");

            try {
                cmd.start();
            } catch (Exception ex) {
            }
        }

        private void quitEngine() {
            callerEngine.pause();

            callerEngine.quit();

        }

        public void closeCurrentModels() {
            try {
                JInternalFrame[] frames = getJDesktopPane().getAllFrames();
                for (int i = 0; i < frames.length; i++)
                    if (frames[i] != consoleWindow)
                        frames[i].dispose();
            } catch (Exception ex) {
            }

            setTimeLabel("");
        }

        /** Ask engine to build currently loaded models. */
        public void buildModel() {
            getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            callerEngine.buildModels();
            setBuiltButtonStatus();

            getContentPane().setCursor(
                    Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }

        /**
         * Return a reference to the settings list.
         * 
         * @return An instance of the Java standard Properties class.
         */
        public Properties getSettings() {
            return settings;
        }

        /*
         * (non-Javadoc)
         * 
         * @see jas.engine.IWindowManager#disposeSimWindows()
         */
        // public void disposeSimWindows() {
        // Iterator<?> it = windowBag.values().iterator();
        // while (it.hasNext())
        // ((SimulationWindow) it.next()).setWindow(null);
        //
        // // Remove all frames but output window
        // JInternalFrame[] frames = jDesktopPane.getAllFrames();
        // int i = frames.length;
        // while (--i >= 0)
        // if (frames[i] != consoleWindow)
        // jDesktopPane.remove(frames[i]);
        // jDesktopPane.repaint();
        //
        // try {
        // Frame[] frmes = MicrosimShell.getFrames();
        // for (i = 0; i < frmes.length; i++)
        // if (frmes[i] != jasWindow)
        // frmes[i].dispose();
        // } catch (Exception ex) {
        // }
        //
        // // windowBag.clear();
        // }

        public void printWindowPositions() {
            for (JInternalFrame jInternalFrame : jDesktopPane.getAllFrames()) {
                System.out.println(jInternalFrame.getTitle() + " [X,Y,W,H] " + jInternalFrame.getX() + ", " +
                        jInternalFrame.getY() + ", " + jInternalFrame.getWidth() + ", " + jInternalFrame.getHeight());
            }
        }
        /*
         * (non-Javadoc)
         * 
         * @see jas.engine.IWindowManager#addSimWindow(jas.engine.ISimModel,
         * javax.swing.JFrame)
         */
        // public void addSimWindow(SimulationManager owner, Frame window) {
        // if (window instanceof JFrame)
        // addSimWindow(owner, buildInternalFrame((JFrame) window));
        // else {
        // SimulationWindow win = (SimulationWindow) windowBag.get(window.getTitle());
        // if (win == null) {
        // String modelId = (owner == null ? "" : owner.getClass().getCanonicalName());
        // win = new SimulationWindow(modelId, window.getTitle(), window);
        // windowBag.put(win.getKey(), win);
        // win.setDefaultPosition(window.getBounds());
        // } else
        // win.setWindow(window);
        //
        // Rectangle r = win.getDefaultPosition();
        // window.setBounds(r.x, r.y, r.width, r.height);
        // window.setVisible(true);
        // }
        // }

        // private JInternalFrame buildInternalFrame(JFrame frame) {
        // JInternalFrame intF = new JInternalFrame(frame.getTitle(), frame
        // .isResizable(), false, frame.isResizable(), true);
        //
        // if (frame.getJMenuBar() != null)
        // intF.setJMenuBar(frame.getJMenuBar());
        //
        // WindowGrabber wg = new WindowGrabber(intF);
        // frame.addWindowListener(wg);
        //
        // intF.getContentPane().add(frame.getContentPane());
        // if (frame.getIconImage() != null)
        // intF.setFrameIcon(new ImageIcon(frame.getIconImage()));
        // intF.setSize(frame.getSize());
        // return intF;
        // }

        /*
         * (non-Javadoc)
         * 
         * @see jas.engine.IWindowManager#addSimWindow(jas.engine.ISimModel,
         * javax.swing.JInternalFrame)
         */
        // public void addSimWindow(SimulationManager owner, JInternalFrame window) {
        // SimulationWindow win = (SimulationWindow) windowBag.get(window.getTitle());
        // if (win == null) {
        // String modelId = (owner == null ? "" : owner.getClass().getCanonicalName());
        // win = new SimulationWindow(modelId, window.getTitle(), window);
        // windowBag.put(window.getTitle(), win);
        // win.setDefaultPosition(window.getBounds());
        // } else
        // win.setWindow(window);
        //
        // jasWindow.getJDesktopPane().add(window);
        // Rectangle r = win.getDefaultPosition();
        // window.reshape(r.x, r.y, r.width, r.height);
        // window.show();
        // }

        /*
         * (non-Javadoc)
         * 
         * @see jas.engine.IWindowManager#getSimWindows()
         */
        // public SimulationWindow[] getSimWindows() {
        // Iterator<?> it;
        // int i;
        //
        // SimulationWindow[] wins = new SimulationWindow[windowBag.values().size()];
        // for (i = 0, it = windowBag.values().iterator(); it.hasNext(); i++)
        // wins[i] = (SimulationWindow) it.next();
        //
        // return wins;
        // }
        //
        // /*
        // * (non-Javadoc)
        // *
        // * @see jas.engine.IWindowManager#addSimWindow(jas.engine.SimWindow)
        // */
        // public void addSimWindow(SimulationWindow window) {
        // windowBag.put(window.getKey(), window);
        // }

        public void onEngineEvent(SystemEventType event) {
            if (event.equals(SystemEventType.Step))
                getJLabelTime().setText("" + callerEngine.getEventQueue().getTime());
            else if (event.equals(SystemEventType.Setup)) {
                parameterFrames.clear();
                for (SimulationManager model : controller.callerEngine.getModelArray()) {
                    List<Field> fields = ParameterInspector.extractModelParameters(model.getClass());

                    // Check that getter and setter exists for each model parameter (to ensure
                    // ability to control via microsim.shell GUI)
                    HashSet<String> getters = new HashSet<String>();
                    HashSet<String> setters = new HashSet<String>();
                    Method[] methods = model.getClass().getMethods();

                    for (Method method : methods) {
                        if (isGetter(method)) {
                            getters.add(method.getName());
                        } else if (isSetter(method)) {
                            setters.add(method.getName());
                        }
                    }

                    for (Field modelParameter : fields) {
                        String modelParameterName = modelParameter.getName();
                        if (modelParameterName.length() > 1) {
                            if (Character.isLowerCase(modelParameterName.charAt(0))
                                    && Character.isUpperCase(modelParameterName.charAt(1))) {
                                if (!getters.contains("get" + modelParameterName)) { // handles cases for fields with a
                                                                                     // name whose first character is
                                                                                     // lower case, followed by a
                                                                                     // capital letter, e.g. nWorkers.
                                                                                     // In this case, the Java Beans
                                                                                     // convention is for a getter
                                                                                     // called getnWorkers, instead of
                                                                                     // getNWorkers.
                                    if (!getters.contains("is" + modelParameterName)) { // handles case for boolean 'is'
                                                                                        // getter methods
                                        throw new RuntimeException("Model parameter " + modelParameterName
                                                + " has no getter method.  Please create a getter method called get"
                                                + modelParameterName + " in the " + model.getClass()
                                                + " to enable this model parameter to be read by the GUI.");
                                    }
                                }
                                if (!setters.contains("set" + modelParameterName)) {
                                    throw new RuntimeException("Model parameter " + modelParameterName
                                            + " has no setter method.  Please create a setter method called set"
                                            + modelParameterName + " in the " + model.getClass()
                                            + " to enable this model parameter to be controlled via the GUI.");
                                }
                            } else {
                                String capModelParameterName = modelParameterName.substring(0, 1).toUpperCase()
                                        + modelParameterName.substring(1, modelParameterName.length()); // Ensure first
                                                                                                        // letter of
                                                                                                        // name is
                                                                                                        // capitalised
                                String getterName = "get" + capModelParameterName;
                                String setterName = "set" + capModelParameterName;

                                if (!getters.contains(getterName)) {
                                    if (!getters.contains("is" + capModelParameterName)) { // handles case for boolean
                                                                                           // 'is' getter methods
                                        throw new RuntimeException("Model parameter " + modelParameterName
                                                + " has no getter method.  Please create a getter method called "
                                                + getterName + " in the " + model.getClass()
                                                + " to enable this model parameter to be read by the GUI.");
                                    }
                                }
                                if (!setters.contains(setterName)) {
                                    throw new RuntimeException("Model parameter " + modelParameterName
                                            + " has no setter method.  Please create a setter method called "
                                            + setterName + " in the " + model.getClass()
                                            + " to enable this model parameter to be controlled via the GUI.");
                                }
                            }
                        } else { // Still need to check that getter/setters exist for case where a single
                                 // character is used for the model parameter name.
                            String capModelParameterName = modelParameterName.substring(0, 1).toUpperCase()
                                    + modelParameterName.substring(1, modelParameterName.length()); // Ensure first
                                                                                                    // letter of name is
                                                                                                    // capitalised
                            String getterName = "get" + capModelParameterName;
                            String setterName = "set" + capModelParameterName;

                            if (!getters.contains(getterName)) {
                                if (!getters.contains("is" + capModelParameterName)) { // handles case for boolean 'is'
                                                                                       // getter methods
                                    throw new RuntimeException("Model parameter " + modelParameterName
                                            + " has no getter method.  Please create a getter method called "
                                            + getterName + " in the " + model.getClass()
                                            + " to enable this model parameter to be read by the GUI.");
                                }
                            }
                            if (!setters.contains(setterName)) {
                                throw new RuntimeException("Model parameter " + modelParameterName
                                        + " has no setter method.  Please create a setter method called " + setterName
                                        + " in the " + model.getClass()
                                        + " to enable this model parameter to be controlled via the GUI.");
                            }
                        }
                    }

                    if (fields.size() > 0) {
                        ParameterFrame parameterFrame = new ParameterFrame(model);
                        parameterFrame.setResizable(false); // Now in scrollpane, cannot resize anyway, so set to false.
                        GuiUtils.addWindow(parameterFrame);
                        // GuiUtils.addWindow(parameterFrame);
                        parameterFrames.add(parameterFrame);
                    }
                }
            } else if (event.equals(SystemEventType.Build)) {
                for (ParameterFrame parameterFrame : parameterFrames) {
                    parameterFrame.save();
                }
            }
        }

    }

    private static boolean isGetter(Method method) {
        if (!(method.getName().startsWith("get") || method.getName().startsWith("is")))
            return false;
        if (method.getParameterTypes().length != 0)
            return false;
        if (void.class.equals(method.getReturnType()))
            return false;
        return true;
    }

    private static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set"))
            return false;
        if (method.getParameterTypes().length != 1)
            return false;
        return true;
    }

    /**
     * This method initializes jBtnReload
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnReload() {
        if (jBtnReload == null) {
            jBtnReload = new javax.swing.JButton();
            jBtnReload.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_refresh.gif")));
            // .getResource("/icons/simulation_refresh.gif")));
            jBtnReload.setToolTipText("Restart simulation model");
            jBtnReload.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    controller.restartModel();
                }
            });
        }
        return jBtnReload;
    }

    /**
     * This method initializes jBtnPlay
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnPlay() {
        if (jBtnPlay == null) {
            jBtnPlay = new javax.swing.JButton();
            jBtnPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "/microsim/gui/icons/simulation_play.gif")));
            jBtnPlay.setToolTipText("Start simulation");
            jBtnPlay.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    controller.startModel();
                }
            });
        }
        return jBtnPlay;
    }

    /**
     * This method initializes jBtnStep
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnStep() {
        if (jBtnStep == null) {
            jBtnStep = new javax.swing.JButton();
            jBtnStep.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "/microsim/gui/icons/simulation_step.gif")));
            jBtnStep.setToolTipText("Execute next scheduled action");
            jBtnStep.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        controller.doStep();
                    } catch (SimulationException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            });
        }
        return jBtnStep;
    }

    /**
     * This method initializes jBtnPause
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnPause() {
        if (jBtnPause == null) {
            jBtnPause = new javax.swing.JButton();
            jBtnPause.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "/microsim/gui/icons/simulation_pause.gif")));
            jBtnPause.setToolTipText("Pause simulation");
            jBtnPause.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    controller.pauseModel();
                }
            });
        }
        return jBtnPause;
    }

    /**
     * This method initializes jBtnPause
     * 
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getJBtnUpdateParameters() {
        if (jBtnUpdateParams == null) {
            jBtnUpdateParams = new javax.swing.JButton();
            jBtnUpdateParams.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "/microsim/gui/icons/simulation_update_params.png")));
            jBtnUpdateParams.setToolTipText("Update parameters in the live simulation");
            jBtnUpdateParams.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    controller.updateModelParams();
                }
            });
        }
        return jBtnUpdateParams;
    }

    /**
     * This method initializes jMenuSimulation
     * 
     * @return javax.swing.JMenu
     */
    private javax.swing.JMenu getJMenuSimulation() {
        if (jMenuSimulation == null) {
            jMenuSimulation = new javax.swing.JMenu();
            jMenuSimulation.add(getJMenuSimulationBuild());
            jMenuSimulation.add(getJMenuSimulationRestart());
            jMenuSimulation.addSeparator();
            jMenuSimulation.add(getJMenuSimulationPlay());
            jMenuSimulation.add(getJMenuSimulationStep());
            // jMenuSimulation.add(getJMenuSimulationTimeStep());
            jMenuSimulation.add(getJMenuSimulationPause());
            jMenuSimulation.add(getJMenuSimulationUpdateParams());
            jMenuSimulation.add(getJMenuSimulationStop());
            jMenuSimulation.addSeparator();
            jMenuSimulation.add(getJMenuSimulationEngine());
            jMenuSimulation.setText("Simulation");
            jMenuSimulation.setFont(new Font(jMenuSimulation.getFont().getFontName(),
                    jMenuSimulation.getFont().getStyle(), (int) (scale * jMenuSimulation.getFont().getSize())));
            // jMenuSimulation.addSeparator();
            jMenuSimulation.add(getJMenuFileExit());
        }
        return jMenuSimulation;
    }

    /**
     * This method initializes jMenuSimulationEngine
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationEngine() {
        if (jMenuSimulationEngine == null) {
            jMenuSimulationEngine = new javax.swing.JMenuItem();
            jMenuSimulationEngine.setText("Show engine status");
            jMenuSimulationEngine
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.showProperties();
                        }
                    });
        }
        return jMenuSimulationEngine;
    }

    /**
     * This method initializes jMenuTools
     * 
     * @return javax.swing.JMenu
     */
    private javax.swing.JMenu getJMenuTools() {
        if (jMenuTools == null) {
            jMenuTools = new javax.swing.JMenu();
            jMenuTools.setText("Tools");
            jMenuTools.setFont(new Font(jMenuTools.getFont().getFontName(), jMenuTools.getFont().getStyle(),
                    (int) (scale * jMenuTools.getFont().getSize())));
            // jMenuTools.add(getJMenuToolsParameter());
            // jMenuTools.add(getJMenuToolsGraph());
            // jMenuTools.add(getJMenuToolsDB());
            // jMenuTools.addSeparator();
            // jMenuTools.add(getJMenuToolsOption());
            jMenuTools.add(getJMenuToolsWindowPositions());
            jMenuTools.add(getJMenuToolsDatabaseExplorer());
        }
        return jMenuTools;
    }

    /**
     * This method initializes jMenuHelp
     * 
     * @return javax.swing.JMenu
     */
    private javax.swing.JMenu getJMenuHelp() {
        if (jMenuHelp == null) {
            jMenuHelp = new javax.swing.JMenu();
            // jMenuHelp.add(getJMenuHelpGuide());
            // jMenuHelp.addSeparator();
            // jMenuHelp.add(getJMenuHelpAPI());
            // jMenuHelp.add(getJMenuHelpLibraries());
            // jMenuHelp.add(getJMenuHelpWebSite());
            // jMenuHelp.addSeparator();
            jMenuHelp.add(getJMenuHelpAbout());
            jMenuHelp.setText("Help");
            jMenuHelp.setFont(new Font(jMenuHelp.getFont().getFontName(), jMenuHelp.getFont().getStyle(),
                    (int) (scale * jMenuHelp.getFont().getSize())));
        }
        return jMenuHelp;
    }

    /**
     * This method initializes jMenuSimulationRestart
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationRestart() {
        if (jMenuSimulationRestart == null) {
            jMenuSimulationRestart = new javax.swing.JMenuItem();
            jMenuSimulationRestart.setText("Restart simulation");
            jMenuSimulationRestart.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_refresh.gif")));
            jMenuSimulationRestart
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.restartModel();
                        }
                    });
        }
        return jMenuSimulationRestart;
    }

    /**
     * This method initializes jMenuSimulationPlay
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationPlay() {
        if (jMenuSimulationPlay == null) {
            jMenuSimulationPlay = new javax.swing.JMenuItem();
            jMenuSimulationPlay.setText("Play");
            jMenuSimulationPlay.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_play.gif")));
            jMenuSimulationPlay
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.startModel();
                        }
                    });
        }
        return jMenuSimulationPlay;
    }

    /**
     * This method initializes jMenuSimulationStep
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationStep() {
        if (jMenuSimulationStep == null) {
            jMenuSimulationStep = new javax.swing.JMenuItem();
            jMenuSimulationStep.setText("Step");
            jMenuSimulationStep.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_step.gif")));
            jMenuSimulationStep
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            try {
                                controller.doStep();
                            } catch (SimulationException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    });
        }
        return jMenuSimulationStep;
    }

    /**
     * This method initializes jMenuSimulationPause
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationPause() {
        if (jMenuSimulationPause == null) {
            jMenuSimulationPause = new javax.swing.JMenuItem();
            jMenuSimulationPause.setText("Pause");
            jMenuSimulationPause.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_pause.gif")));
            jMenuSimulationPause
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.pauseModel();
                        }
                    });
        }
        return jMenuSimulationPause;
    }

    /**
     * This method initializes jMenuSimulationPause
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationUpdateParams() {
        if (jMenuSimulationUpdateParams == null) {
            jMenuSimulationUpdateParams = new javax.swing.JMenuItem();
            jMenuSimulationUpdateParams.setText("Update Parameters");
            jMenuSimulationUpdateParams.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_update_params.png")));
            jMenuSimulationUpdateParams
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.updateModelParams();
                        }
                    });
        }
        return jMenuSimulationUpdateParams;
    }

    /**
     * This method initializes jMenuSimulationStop
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationStop() {
        if (jMenuSimulationStop == null) {
            jMenuSimulationStop = new javax.swing.JMenuItem();
            jMenuSimulationStop.setText("Stop");
            jMenuSimulationStop.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_stop.gif")));
            jMenuSimulationStop
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.stopModel();
                        }
                    });
        }
        return jMenuSimulationStop;
    }

    private javax.swing.JMenuItem getJMenuToolsWindowPositions() {
        if (jMenuToolsWindowPositions == null) {
            jMenuToolsWindowPositions = new javax.swing.JMenuItem();
            jMenuToolsWindowPositions.setText("Print window positions");
            jMenuToolsWindowPositions.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/console.gif")));
            jMenuToolsWindowPositions
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.printWindowPositions();
                        }
                    });
        }
        return jMenuToolsWindowPositions;
    }

    private javax.swing.JCheckBox getJSilentCheck() {
        if (jSilentCheck == null) {
            jSilentCheck = new javax.swing.JCheckBox();
            jSilentCheck.setSelected(controller.isTurnOffDatabaseConnection());
            jSilentCheck.setText("Turn off database");
            jSilentCheck.setFont(new Font(jSilentCheck.getFont().getFontName(), jSilentCheck.getFont().getStyle(),
                    (int) (scale * jSilentCheck.getFont().getSize())));
            jSilentCheck
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.setTurnOffDatabaseConnection(jSilentCheck.isSelected());
                            jSilentCheck.setSelected(controller.isTurnOffDatabaseConnection());
                        }
                    });
        }
        return jSilentCheck;
    }

    private javax.swing.JMenuItem getJMenuToolsDatabaseExplorer() {
        if (jMenuToolsDatabaseExplorer == null) {
            jMenuToolsDatabaseExplorer = new javax.swing.JMenuItem();
            jMenuToolsDatabaseExplorer.setText("Database explorer");
            jMenuToolsDatabaseExplorer.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/console.gif")));
            jMenuToolsDatabaseExplorer
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.showDatabaseExplorer();
                        }
                    });
        }
        return jMenuToolsDatabaseExplorer;
    }

    /**
     * This method initializes jMenuSimulationBuild
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuSimulationBuild() {
        if (jMenuSimulationBuild == null) {
            jMenuSimulationBuild = new javax.swing.JMenuItem();
            jMenuSimulationBuild.setText("Build model");
            jMenuSimulationBuild.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/simulation_build.gif")));
            jMenuSimulationBuild
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            controller.buildModel();
                        }
                    });
        }
        return jMenuSimulationBuild;
    }

    /**
     * This method initializes jPanelTime
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJPanelTime() {
        if (jPanelTime == null) {
            jPanelTime = new javax.swing.JPanel();
            jPanelTime.setLayout(new java.awt.BorderLayout());
            jPanelTime.add(getJLabelCurTime(), java.awt.BorderLayout.WEST);
            jPanelTime.add(getJLabelTime(), java.awt.BorderLayout.CENTER);
        }
        return jPanelTime;
    }

    /**
     * This method initializes jLabelCurTime
     * 
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelCurTime() {
        if (jLabelCurTime == null) {
            jLabelCurTime = new javax.swing.JLabel();
            jLabelCurTime.setText("Current time:");
            jLabelCurTime.setFont(new java.awt.Font("Franklin Gothic Medium",
                    java.awt.Font.ITALIC, (int) (scale * 12)));
        }
        return jLabelCurTime;
    }

    /**
     * This method initializes jPanelSlider
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJPanelSlider() {
        if (jPanelSlider == null) {
            jPanelSlider = new javax.swing.JPanel();
            jPanelSlider.setLayout(null);
            jPanelSlider.add(getJLabelSlider(), null);
            jPanelSlider.add(getJSlider(), null);

            jPanelSlider.setSize(100, 30);
            jPanelSlider.setPreferredSize(new java.awt.Dimension(100, 30));
        }
        return jPanelSlider;
    }

    /**
     * This method initializes jLabelSlider
     * 
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelSlider() {
        if (jLabelSlider == null) {
            jLabelSlider = new javax.swing.JLabel();
            jLabelSlider.setBounds(0, 0, (int) (scale * 173), 18);
            jLabelSlider.setText(" Simulation speed: max");
            jLabelSlider.setFont(new java.awt.Font("Franklin Gothic Medium",
                    java.awt.Font.PLAIN, (int) (scale * 12)));
            jLabelSlider.setName("jLabelSlider");
        }
        return jLabelSlider;
    }

    /**
     * This method initializes jSlider
     * 
     * @return javax.swing.JSlider
     */
    private javax.swing.JSlider getJSlider() {
        if (jSlider == null) {
            jSlider = new javax.swing.JSlider();
            jSlider.setBounds(0, 18, (int) (scale * 173), 18);
            jSlider.setName("jSlider");
            jSlider.setMaximum(200);
            jSlider.setValue(0);
            jSlider.setInverted(true);
            jSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    int value = jSlider.getValue();
                    if (value == jSlider.getMinimum())
                        jLabelSlider.setText(" Simulation speed: max");
                    else
                        jLabelSlider.setText(" Simulation speed: "
                                + (200 - value));

                    controller.changeEventTimeTreshold(value);
                }
            });
        }
        return jSlider;
    }

    /**
     * This method initializes jDesktopPane
     * 
     * @return javax.swing.JDesktopPane
     */
    public javax.swing.JDesktopPane getJDesktopPane() {
        if (jDesktopPane == null) {
            jDesktopPane = new javax.swing.JDesktopPane();
            // jDesktopPane.setLayout(new GridLayout(2, 2));
        }
        return jDesktopPane;
    }

    /**
     * This method initializes jSplitPane
     * 
     * @return javax.swing.JSplitPane
     */
    private javax.swing.JSplitPane getJSplitPane() {
        if (jSplitPane == null) {
            jSplitPane = new javax.swing.JSplitPane();
            jSplitPane.setRightComponent(getJSplitInternalDesktop());
            jSplitPane.setLeftComponent(getJNullLabel());
            jSplitPane.setOneTouchExpandable(true);
        }
        return jSplitPane;
    }

    /**
     * This method initializes jNullLabel
     * 
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJNullLabel() {
        if (jNullLabel == null) {
            jNullLabel = new javax.swing.JLabel();
        }
        return jNullLabel;
    }

    // private class WindowGrabber extends WindowAdapter {
    // private JInternalFrame frame;
    //
    // public WindowGrabber(JInternalFrame internalFrame) {
    // frame = internalFrame;
    // }
    //
    // public void windowActivated(WindowEvent e) {
    // e.getWindow().setVisible(false);
    // frame.setVisible(true);
    // frame.setSize(e.getWindow().getSize());
    // }
    //
    // public void windowClosed(WindowEvent e) {
    // frame.setVisible(false);
    // }
    // }

    // /**
    // * This method initializes jMenuToolsOption
    // *
    // * @return javax.swing.JMenuItem
    // */
    // private javax.swing.JMenuItem getJMenuToolsOption() {
    // if (jMenuToolsOption == null) {
    // jMenuToolsOption = new javax.swing.JMenuItem();
    // jMenuToolsOption.setText("JAS options");
    // jMenuToolsOption
    // .addActionListener(new java.awt.event.ActionListener() {
    // public void actionPerformed(java.awt.event.ActionEvent e) {
    // controller.editProperties();
    // }
    // });
    // }
    // return jMenuToolsOption;
    // }

    /**
     * This method initializes jMenuHelpAbout
     * 
     * @return javax.swing.JMenuItem
     */
    private javax.swing.JMenuItem getJMenuHelpAbout() {
        if (jMenuHelpAbout == null) {
            jMenuHelpAbout = new javax.swing.JMenuItem();
            jMenuHelpAbout.setText("About JAS-mine");
            jMenuHelpAbout.setIcon(new javax.swing.ImageIcon(getClass()
                    .getResource("/microsim/gui/icons/msIco.gif")));
            jMenuHelpAbout
                    .addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            (new AboutFrame()).setVisible(true);
                        }
                    });
        }
        return jMenuHelpAbout;
    }

    // /**
    // * This method initializes jMenuSimulationEngine
    // *
    // * @return javax.swing.JMenuItem
    // */
    // private javax.swing.JMenuItem getJMenuSimulationEngine() {
    // if (jMenuSimulationEngine == null) {
    // jMenuSimulationEngine = new javax.swing.JMenuItem();
    // jMenuSimulationEngine.setText("Show engine status");
    // jMenuSimulationEngine
    // .addActionListener(new java.awt.event.ActionListener() {
    // public void actionPerformed(java.awt.event.ActionEvent e) {
    // controller.showProperties();
    // }
    // });
    // }
    // return jMenuSimulationEngine;
    // }

    // /**
    // * This method initializes jMenuHelpWebSite
    // *
    // * @return javax.swing.JMenuItem
    // */
    // private javax.swing.JMenuItem getJMenuHelpWebSite() {
    // if (jMenuHelpWebSite == null) {
    // jMenuHelpWebSite = new javax.swing.JMenuItem();
    // jMenuHelpWebSite.setText("JAS web site");
    // jMenuHelpWebSite
    // .addActionListener(new java.awt.event.ActionListener() {
    // public void actionPerformed(java.awt.event.ActionEvent e) {
    // controller.navigateWebSite();
    // }
    // });
    //
    // }
    // return jMenuHelpWebSite;
    // }

    /**
     * This method initializes jSplitInternalDesktop
     * 
     * @return javax.swing.JSplitPane
     */
    private javax.swing.JSplitPane getJSplitInternalDesktop() {
        if (jSplitInternalDesktop == null) {
            jSplitInternalDesktop = new javax.swing.JSplitPane();
            jSplitInternalDesktop.setTopComponent(getJDesktopPane());
            jSplitInternalDesktop.setBottomComponent(null);
            jSplitInternalDesktop.setSize(31, 59);
            jSplitInternalDesktop
                    .setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
            jSplitInternalDesktop.setOneTouchExpandable(true);
        }
        return jSplitInternalDesktop;
    }

    public void log(String message) {
        consoleWindow.log(message);
    }
} // @jve:visual-info decl-index=0 visual-constraint="10,10"
