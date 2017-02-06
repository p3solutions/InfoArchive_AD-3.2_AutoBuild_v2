/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.xproc.io.ResolverHandler
 */
package com.emc.documentum.xml.dds.xproc;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.user.User;
import com.emc.documentum.xml.dds.util.SessionPool;
import com.emc.documentum.xml.xproc.io.ResolverHandler;

public abstract class AbstractDDSResolverHandler
implements ResolverHandler {
    private Application application;
    private User user;
    private SessionPool sessionPool;

    public Application getApplication() {
        return this.application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SessionPool getSessionPool() {
        return this.sessionPool;
    }

    public void setSessionPool(SessionPool sessionPool) {
        this.sessionPool = sessionPool;
    }
}

