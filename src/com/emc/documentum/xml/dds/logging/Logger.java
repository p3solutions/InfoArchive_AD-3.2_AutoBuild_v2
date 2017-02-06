/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.logging;

public interface Logger {
    public void close();

    public void log(String var1);

    public void warning(String var1);

    public void error(String var1);

    public void debug(String var1);

    public void exception(String var1, Throwable var2);

    public void log(Object var1, String var2);

    public void warning(Object var1, String var2);

    public void error(Object var1, String var2);

    public void debug(Object var1, String var2);

    public void exception(Object var1, Throwable var2);

    public void exception(Object var1, String var2, Throwable var3);
}

