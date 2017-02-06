/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.logging;

import com.emc.documentum.xml.dds.logging.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileLogger
implements Logger {
    private static final SimpleDateFormat DATEFORMATTER = new SimpleDateFormat("yyyyMMdd hh:mm:ss");
    private static final SimpleDateFormat FILENAMEDATEFORMATTER = new SimpleDateFormat("yyyyMMdd_hhmmss");
    private static final Object FNDFMUTEX = new Object();
    private static final Object DFMUTEX = new Object();
    private PrintWriter writer;
    private boolean writerInitialized;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FileLogger(String logPath, String logPrefix, String logSuffix) {
        Object object = FNDFMUTEX;
        synchronized (object) {
            StringBuilder path = new StringBuilder(logPath == null ? "." : logPath);
            try {
                if (!path.toString().endsWith(File.separator)) {
                    path.append(File.separator);
                }
                path.append(logPrefix);
                path.append("_");
                path.append(FILENAMEDATEFORMATTER.format(new Date()));
                path.append(logSuffix);
                File file = new File(path.toString());
                file.getParentFile().mkdirs();
                this.writer = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(file, true), "UTF-8"));
                this.writerInitialized = true;
            }
            catch (Exception e) {
                System.out.println("Failed to open logfile " + path.toString());
                e.printStackTrace();
            }
        }
    }

    public FileLogger(URI fileUri) {
        try {
            this.writer = new PrintWriter(new OutputStreamWriter((OutputStream)new FileOutputStream(new File(fileUri)), "UTF-8"));
            this.writerInitialized = true;
        }
        catch (Exception e) {
            System.out.println("Failed to open logfile " + fileUri);
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (this.writerInitialized) {
            try {
                this.writer.close();
                this.writerInitialized = false;
            }
            catch (Exception e) {
                System.out.println("Failed to close logfile : " + e);
                e.printStackTrace();
            }
        }
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
        throwable.printStackTrace(this.writer);
        this.writer.flush();
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
        throwable.printStackTrace(this.writer);
        this.writer.flush();
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
        result.append(msg).append("\n");
        try {
            if (this.writerInitialized) {
                this.writer.write(result.toString());
                this.writer.flush();
            } else {
                System.out.println(result.toString());
            }
        }
        catch (Exception e) {
            System.out.println(result.toString());
        }
    }
}

