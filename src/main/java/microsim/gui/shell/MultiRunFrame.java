package microsim.gui.shell;

import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import microsim.engine.EngineListener;
import microsim.engine.MultiRun;
import microsim.engine.MultiRunListener;
import microsim.engine.SimulationEngine;
import microsim.event.SystemEventType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.ui.tabbedui.VerticalLayout;

/**
 * Not of interest for users. This class implements the multi run control panel
 * shown by JAS when a MultiRun class is executed.
 * 
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright: Copyright (C) 2002 Michele Sonnessa
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Michele Sonnessa
 */

public class MultiRunFrame extends JFrame implements MultiRunListener, EngineListener {

    private static Logger log = LogManager.getLogger(MultiRunFrame.class);

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    private VerticalLayout borderLayout1 = new VerticalLayout();
    private JPanel jPanelNorth = new JPanel();
    private JLabel jLblNumber = new JLabel();
    private JLabel jLblRunNb = new JLabel();
    private JLabel jLblCurrentStep = new JLabel();
    private JLabel jLblCurrentStepLabel = new JLabel();
    private JLabel jLblCurrentRun = new JLabel();
    private JLabel jLblCurrentRunLabel = new JLabel();
    private JProgressBar jBar = new JProgressBar();
    private JButton jBtnQuit = new JButton();
    private JPanel jPanelBtns = new JPanel();
    private JButton jBtnStart = new JButton();

    private int forward = 1;

    private int maxRuns;

    private MultiRun test;

    public MultiRunFrame(MultiRun test, String title, int maxRuns) {
        this.test = test;
        this.maxRuns = maxRuns;
        test.getEngineListeners().add(this);
        test.getMultiRunListeners().add(this);
        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setTitle(title);
        setMaxRuns(maxRuns);
        this.setVisible(true);
        this.setResizable(false);
    }

    private void jbInit() throws Exception {
        this.getContentPane().setLayout(borderLayout1);
        jPanelNorth.setLayout(new VerticalLayout());

        JPanel h1 = new JPanel(new FlowLayout());
        jLblNumber.setText("0");
        jLblRunNb.setText("Current run number: ");
        h1.add(jLblRunNb);
        h1.add(jLblNumber);

        JPanel h2 = new JPanel(new FlowLayout());
        jLblCurrentStep.setText("0");
        jLblCurrentStepLabel.setText("Current run step: ");
        h2.add(jLblCurrentStepLabel);
        h2.add(jLblCurrentStep);

        JPanel h3 = new JPanel(new FlowLayout());
        jLblCurrentRun.setText("");
        jLblCurrentRunLabel.setText("Current step: ");
        h3.add(jLblCurrentRunLabel);
        h3.add(jLblCurrentRun);

        jBtnQuit.setText("Quit");
        jBtnQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnQuit_actionPerformed(e);
            }
        });
        jBtnStart.setText("Start");
        jBtnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jBtnStart_actionPerformed(e);
            }
        });

        jPanelBtns.add(jBtnStart, null);
        this.getContentPane().add(jPanelNorth);
        jPanelNorth.add(h1, null);
        jPanelNorth.add(h2, null);
        jPanelNorth.add(h3, null);
        jPanelNorth.add(new JLabel("-"), null);

        this.getContentPane().add(jBar);
        this.getContentPane().add(new JLabel("-"));

        this.getContentPane().add(jPanelBtns);
        jPanelBtns.add(jBtnQuit, null);

        setSize(300, 180);
        int x = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
        int y = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        setLocation((x - 300) / 2, (y - 300) / 2);
    }

    public void updateModelNumber(int currentRun, SimulationEngine engine) {
        jLblNumber.setText(currentRun + "");
        jLblCurrentRun.setText(engine.getMultiRunId());
    }

    void jBtnQuit_actionPerformed(ActionEvent e) {
        System.exit(0);
    }

    public void setMaxRuns(int maxRuns) {
        jBar.setMaximum(maxRuns);
    }

    public void updateBar() {
        if (jBar.getValue() == 0 || jBar.getValue() == jBar.getMaximum()) {
            if (forward == 1)
                forward = -1;
            else
                forward = 1;
        }

        jBar.setValue(jBar.getValue() + forward);

        repaint();
    }

    private class Timer extends Thread {
        private MultiRunFrame caller;

        public Timer(MultiRunFrame caller) {
            this.caller = caller;
        }

        public void run() {
            while (true) {
                caller.updateBar();
                try {
                    sleep(200);
                } catch (Exception e) {
                }
            }
        }
    }

    void jBtnStart_actionPerformed(ActionEvent e) {
        jBtnStart.setEnabled(false);
        Timer tm = new Timer(this);
        tm.start();
        test.start();
    }

    public void beforeSimulationStart(SimulationEngine engine) {
        if (maxRuns > 0 && test.getCounter() > maxRuns) {
            log.info("Maximum run number reached. Bye");
            System.exit(0);
        }

        updateModelNumber(test.getCounter(), engine);
    }

    public void afterSimulationCompleted(SimulationEngine engine) {

    }

    public void onEngineEvent(SystemEventType event) {
        if (event.equals(SystemEventType.Step))
            jLblCurrentStep.setText(SimulationEngine.getInstance().getTime() + "");
    }
}
