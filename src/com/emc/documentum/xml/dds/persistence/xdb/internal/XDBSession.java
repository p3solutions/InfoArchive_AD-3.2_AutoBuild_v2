/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.operation.exception.BeginFailedException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.xhive.core.interfaces.XhiveSessionIf;

public class XDBSession
implements Session {
    private final XhiveSessionIf session;
    private final XDBStoreUser user;
    private final XDBStore store;

    public XDBSession(XhiveSessionIf session, XDBStoreUser user, XDBStore store) {
        this.session = session;
        this.user = user;
        this.store = store;
    }

    public XhiveSessionIf getSession() {
        return this.session;
    }

    @Override
    public XDBStoreUser getUser() {
        return this.user;
    }

    @Override
    public XDBStore getStore() {
        return this.store;
    }

    @Override
    public StoreType getStoreType() {
        return this.store.getType();
    }

    @Override
    public void begin() throws BeginFailedException {
        this.session.begin();
    }

    @Override
    public void commit() throws CommitFailedException {
        this.session.commit();
        this.session.terminate();
    }

    protected void replicatedCommit() throws CommitFailedException {
        this.session.commit();
    }

    protected void replicatedTerminate() {
        this.session.terminate();
    }

    @Override
    public void rollback() throws RollbackFailedException {
        this.session.rollback();
        this.session.terminate();
    }

    protected void replicatedRollback() throws RollbackFailedException {
        this.session.rollback();
    }

    @Override
    public boolean isOpen() {
        return this.session.isOpen();
    }

    @Override
    public boolean isReadOnly() {
        return this.session.getReadOnlyMode();
    }
}

