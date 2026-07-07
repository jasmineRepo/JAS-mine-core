package microsim.gui.shell;

import java.io.*;
import javax.swing.*;

/**
 * Internal component of the CaptureConsoleWindow.
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
 * @author Michele Sonnessa (taken from an example of Byte review).
 *         <p>
 */
public class ConsoleTextArea extends JTextArea {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 1L;
    // private final LoopedStreams ls;
    private PrintStream oldOut, oldErr;
    private boolean keepRunning = true;
    private ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();

    /**
     * It is attached to the default System.out and System.err streams.
     * 
     * @throws IOException
     *                     Thrown in case of stream error.
     */
    public ConsoleTextArea() throws IOException {
        // Redirect System.out & System.err.
        PrintStream ps = new PrintStream(byteArrayOS);
        oldOut = System.out;
        oldErr = System.err;
        System.setOut(ps);
        System.setErr(ps);

        startByteArrayReaderThread();
    }

    public void stopReading() {
        keepRunning = false;
    }

    public void startReading() {
        keepRunning = true;
    }

    public boolean isReading() {
        return keepRunning;
    }

    /** Release the captured streams. */
    public void dispose() {
        System.setOut(oldOut);
        System.setErr(oldErr);

    }

    public synchronized void log(String message) {
        append(message + "\n");
        setCaretPosition(getDocument().getLength());
    }

    private void startByteArrayReaderThread() {
        new Thread(new Runnable() {
            public void run() {
                String buff = "";
                while (true) {
                    // Check for bytes in the stream.
                    if (byteArrayOS.size() > 0) {
                        if (keepRunning) {
                            synchronized (byteArrayOS) {
                                buff = byteArrayOS.toString();
                                byteArrayOS.reset();
                            }
                            append(buff);
                            setCaretPosition(getDocument().getLength());
                        } else {
                            synchronized (byteArrayOS) {
                                byteArrayOS.reset(); // Clear the buffer.
                            }
                        }
                    } else
                        // No data available, go to sleep.
                        try {
                            // Check the ByteArrayOutputStream every
                            // 1 second for new data.
                            Thread.sleep(500);
                            Thread.yield();
                        } catch (InterruptedException e) {
                        }
                }
            }
        }).start();
    }

}
