/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.XProc
 */
package com.emc.documentum.xml.dds.xproc;

import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.xproc.XProc;
import java.util.Collection;

public interface DDSXProc {
    public XProc getXProc();

    public SessionPool getSessionPool();

    public Collection<Session> getOpenSessions();
}

