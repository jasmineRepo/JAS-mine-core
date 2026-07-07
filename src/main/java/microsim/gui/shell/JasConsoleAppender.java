package microsim.gui.shell;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.WriterAppender;

/**
 * JAS custom log4j appender to catch logs and write them into the
 * JAS Console window.<br/>
 * <br/>
 * 
 * <p>
 * Title: JAS
 * </p>
 * <p>
 * Description: Java Agent-based Simulation library
 * </p>
 * <p>
 * Copyright (C) 2002-13 Michele Sonnessa
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
public class JasConsoleAppender implements Appender {
    private WriterAppender writer;

    public JasConsoleAppender() {
        this.writer = WriterAppender.newBuilder().build();
    }

    public JasConsoleAppender(Layout<?> layout, OutputStream os) {
        var osw = new OutputStreamWriter(os);
        this.writer = WriterAppender.newBuilder().setLayout(layout).setTarget(osw).build();
    }

    public JasConsoleAppender(Layout<?> layout, Writer writer) {
        this.writer = WriterAppender.newBuilder().setLayout(layout).setTarget(writer).build();
    }

    @Override
    public void append(LogEvent event) {
        this.writer.append(event);
        if (MicrosimShell.currentShell != null)
            MicrosimShell.currentShell.log(event.getMessage().toString());
        else
            System.out.println(event.getMessage().toString());
    }

    @Override
    public State getState() {
        return this.writer.getState();
    }

    @Override
    public void initialize() {
        this.writer.initialize();
    }

    @Override
    public boolean isStarted() {
        return this.writer.isStarted();
    }

    @Override
    public boolean isStopped() {
        return this.writer.isStopped();
    }

    @Override
    public void start() {
        this.writer.start();
    }

    @Override
    public void stop() {
        this.writer.stop();
    }

    @Override
    public ErrorHandler getHandler() {
        return this.writer.getHandler();
    }

    @Override
    public Layout<? extends Serializable> getLayout() {
        return this.writer.getLayout();
    }

    @Override
    public String getName() {
        return this.writer.getName();
    }

    @Override
    public boolean ignoreExceptions() {
        return this.writer.ignoreExceptions();
    }

    @Override
    public void setHandler(ErrorHandler handler) {
        this.writer.setHandler(handler);
    }

}
