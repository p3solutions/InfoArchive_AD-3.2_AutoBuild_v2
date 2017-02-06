/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.xhive.core.interfaces.XhiveSessionIf
 *  com.xhive.core.interfaces.XhiveSessionIf$TimeStamp
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.operation.exception.BeginFailedException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.xdb.internal.ReplicatedXDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBSession;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.xhive.core.interfaces.XhiveSessionIf;
import java.util.Collection;
import java.util.HashMap;

public class ReplicatedXDBSession
implements Session {
    private static ThreadLocal<HashMap<String, XhiveSessionIf.TimeStamp>> storeTSes = new ThreadLocal<HashMap<String, XhiveSessionIf.TimeStamp>>(){

        @Override
        public HashMap<String, XhiveSessionIf.TimeStamp> initialValue() {
            return new HashMap<String, XhiveSessionIf.TimeStamp>();
        }
    };
    private final Session session;
    private final long timeout;
    private final ReplicatedXDBStore rxdb;

    public ReplicatedXDBSession(ReplicatedXDBStore rxdb, Session session, long timeout) {
        this.rxdb = rxdb;
        this.session = session;
        this.timeout = timeout;
    }

    @Override
    public void begin() throws BeginFailedException {
        XhiveSessionIf.TimeStamp storeTs;
        String chAlias = this.session.getStore().getAlias();
        if (this.session.isReadOnly() && (storeTs = storeTSes.get().get(chAlias)) != null) {
            if (!((XhiveSessionIf)this.session.getSession()).waitForTimeStamp(storeTs, this.timeout)) {
                throw new BeginFailedException("Could not start read-only session on Replicated XDB Store because Replica is not up-to-date.");
            }
            storeTSes.get().remove(chAlias);
        }
        this.session.begin();
    }

    @Override
    public void commit() throws CommitFailedException {
        String chAlias = this.session.getStore().getAlias();
        ((XDBSession)this.session).replicatedCommit();
        if (!this.session.isReadOnly()) {
            XhiveSessionIf.TimeStamp ts = ((XhiveSessionIf)this.session.getSession()).getUpdateTimeStamp();
            for (XDBStore slave : this.rxdb.getSlaves()) {
                storeTSes.get().put(slave.getAlias(), ts);
            }
        } else {
            storeTSes.get().remove(chAlias);
        }
        ((XDBSession)this.session).replicatedTerminate();
    }

    @Override
    public Object getSession() {
        return this.session.getSession();
    }

    @Override
    public Store getStore() {
        return this.session.getStore();
    }

    @Override
    public StoreType getStoreType() {
        return this.session.getStoreType();
    }

    @Override
    public StoreUser getUser() {
        return this.session.getUser();
    }

    @Override
    public boolean isOpen() {
        return this.session.isOpen();
    }

    @Override
    public boolean isReadOnly() {
        return this.session.isReadOnly();
    }

    @Override
    public void rollback() throws RollbackFailedException {
        ((XDBSession)this.session).replicatedRollback();
        if (!this.session.isReadOnly()) {
            XhiveSessionIf.TimeStamp ts = ((XhiveSessionIf)this.session.getSession()).getUpdateTimeStamp();
            for (XDBStore slave : this.rxdb.getSlaves()) {
                storeTSes.get().put(slave.getAlias(), ts);
            }
        }
        ((XDBSession)this.session).replicatedTerminate();
    }

}

