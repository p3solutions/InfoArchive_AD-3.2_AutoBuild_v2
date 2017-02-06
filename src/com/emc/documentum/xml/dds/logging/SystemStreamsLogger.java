/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.logging;

import com.emc.documentum.xml.dds.logging.Logger;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemStreamsLogger
implements Logger {
    private static final SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
    private static final Object DFMUTEX = new Object();

    @Override
    public void close() {
    }

    @Override
    public void log(String msg) {
        this.writeMessage(msg, "L");
    }

    @Override
    public void warning(String msg) {
        this.writeMessage(msg, "W");
    }

    @Override
    public void error(String msg) {
        this.writeMessage(msg, "E");
    }

    @Override
    public void debug(String msg) {
        this.writeMessage(msg, "D");
    }

    @Override
    public void exception(String msg, Throwable throwable) {
        this.writeMessage(msg, "X");
        throwable.printStackTrace(System.out);
    }

    @Override
    public void log(Object sender, String msg) {
        this.writeMessage(sender, msg, "L");
    }

    @Override
    public void warning(Object sender, String msg) {
        this.writeMessage(sender, msg, "W");
    }

    @Override
    public void error(Object sender, String msg) {
        this.writeMessage(sender, msg, "E");
    }

    @Override
    public void debug(Object sender, String msg) {
        this.writeMessage(sender, msg, "D");
    }

    @Override
    public void exception(Object sender, String message, Throwable throwable) {
        this.writeMessage(sender, message, "X");
        throwable.printStackTrace(System.out);
    }

    @Override
    public void exception(Object sender, Throwable throwable) {
        this.exception(sender, "An Exception was thrown :", throwable);
    }

    private void writeMessage(Object sender, String msg, String prefix) {
        StringBuilder result = new StringBuilder(sender.getClass().getName());
        result.append(" : ").append(msg);
        this.writeMessage(result.toString(), prefix);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeMessage(String msg, String prefix) {
        StringBuilder result;
        Object object = DFMUTEX;
        synchronized (object) {
            result = new StringBuilder(DATEFORMATTER.format(new Date()));
        }
        result.append(" ").append(prefix).append(" ");
        result.append(msg);
        System.out.println(result.toString());
    }
}

