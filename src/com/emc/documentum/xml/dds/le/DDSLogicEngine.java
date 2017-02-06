/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.le.engine.LogicEngine
 */
package com.emc.documentum.xml.dds.le;

import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.le.engine.LogicEngine;
import java.util.Collection;

public interface DDSLogicEngine {
    public LogicEngine getLogicEngine();

    public SessionPool getSessionPool();

    public Collection<Session> getOpenSessions();
}

