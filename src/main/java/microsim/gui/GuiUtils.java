package microsim.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

import microsim.engine.SimulationManager;
import microsim.gui.probe.ProbeFrame;
import microsim.gui.shell.MicrosimShell;
import microsim.gui.shell.SimulationWindow;

public class GuiUtils {

    public static class WindowGrabber extends WindowAdapter {
        private JInternalFrame frame;

        public WindowGrabber(JInternalFrame internalFrame) {
            frame = internalFrame;
        }

        public void windowActivated(WindowEvent e) {
            e.getWindow().setVisible(false);
            frame.setVisible(true);
            frame.setSize(e.getWindow().getSize());
        }

        public void windowClosed(WindowEvent e) {
            frame.setVisible(false);
        }
    }

    /**
     * Opens a probe on the given object and return its reference.
     * 
     * @param on
     *                   Object to be probed. If on implements the IProbeFields
     *                   interface the probe will use this set of fields.
     * @param title
     *                   The title of the probe frame.
     * @param ownerModel
     *                   The caller SimModel.
     * @return A new instance of ProbeFrame.
     */
    public static ProbeFrame openProbe(Object on, String title,
            SimulationManager ownerModel) {
        ProbeFrame pf = new ProbeFrame(on, title);
        pf.setVisible(true);

        return pf;
    }

    /**
     * Opens a probe on the given object and return its reference.
     * 
     * @param on
     *              Object to be probed. If on implements the IProbeFields
     *              interface the probe will use this set of fields.
     * @param title
     *              The title of the probe frame.
     * @return A new instance of ProbeFrame.
     */
    public static ProbeFrame openProbe(Object on, String title) {
        return openProbe(on, title, null);
    }

    public static void addWindow(Frame window) {
        if (MicrosimShell.currentShell == null)
            window.setVisible(true);
        else {
            if (window instanceof JFrame)
                addWindow(buildInternalFrame((JFrame) window));
            else {
                SimulationWindow win = new SimulationWindow(null,
                        window.getTitle(), window);
                win.setDefaultPosition(window.getBounds());

                Rectangle r = win.getDefaultPosition();
                window.setBounds(r.x, r.y, r.width, r.height);
                window.setVisible(true);
            }
        }
    }

    public static void addWindow(Frame window, int x, int y, int width, int height) {

    }

    public static void addWindow(JInternalFrame window) {

        final JDesktopPane desk = MicrosimShell.currentShell.getJDesktopPane();

        desk.add(window);
        window.show();

        JInternalFrame[] allframes = desk.getAllFrames();
        int count = allframes.length;
        if (count == 0)
            return;

        // Determine the necessary grid size
        int sqrt = (int) Math.sqrt(count);
        int rows = sqrt;
        int cols = sqrt;
        if (rows * cols < count) {
            cols++;
            if (rows * cols < count) {
                rows++;
            }
        }

        // Define some initial values for size & location.
        Dimension size = desk.getSize();

        int x = 0;
        int y = 0;

        int maxH = 0;

        for (int i = 0; i < allframes.length; i++) {
            JInternalFrame f = allframes[i];
            if (!f.isClosed() && f.isIcon()) {
                try {
                    f.setIcon(false);
                } catch (PropertyVetoException ignored) {
                }
            }

            desk.getDesktopManager().resizeFrame(f, x, y, f.getWidth(), f.getHeight());
            if (f.getHeight() > maxH)
                maxH = f.getHeight();
            x += f.getWidth();
            if (x > size.width) {
                x = 0;
                y += maxH;
                maxH = 0;
            }
        }
    }

    public static void addWindow(JInternalFrame window, int x, int y,
            int width, int height) {
        // SimulationWindow win = new SimulationWindow(null, window.getTitle(),
        // window);
        // win.setDefaultPosition(window.getBounds());

        MicrosimShell.currentShell.getJDesktopPane().add(window);
        // Rectangle r = win.getDefaultPosition();
        window.reshape(x, y, width, height);
        window.show();
    }

    public static JInternalFrame buildInternalFrame(JFrame frame) {
        JInternalFrame intF = new JInternalFrame(frame.getTitle(),
                frame.isResizable(), false, frame.isResizable(), true);

        if (frame.getJMenuBar() != null)
            intF.setJMenuBar(frame.getJMenuBar());

        WindowGrabber wg = new WindowGrabber(intF);
        frame.addWindowListener(wg);

        intF.getContentPane().add(frame.getContentPane());
        if (frame.getIconImage() != null)
            intF.setFrameIcon(new ImageIcon(frame.getIconImage()));
        intF.setSize(frame.getSize());
        return intF;
    }

}
