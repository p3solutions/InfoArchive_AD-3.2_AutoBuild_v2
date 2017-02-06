/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase.logbase.internal;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.logging.LogCenter;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.emc.documentum.xml.dds.xbase.StorageStrategy;
import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import com.emc.documentum.xml.dds.xbase.XBaseFile;
import com.emc.documentum.xml.dds.xbase.logbase.LogBase;
import com.emc.documentum.xml.dds.xbase.logbase.LogBaseEntry;
import com.emc.documentum.xml.dds.xbase.logbase.LogBaseFile;
import com.emc.documentum.xml.dds.xbase.logbase.internal.LogBaseEntryImpl;
import com.emc.documentum.xml.dds.xbase.logbase.internal.LogBaseFileImpl;
import java.util.Map;
import org.w3c.dom.Node;

public final class LogBaseImpl
implements LogBase {
    private final String id;
    private final Location logBaseLocation;
    private final XDBStore xdb;
    private final XDBStoreUser storeUser;
    private final String baseName;
    private final StorageStrategy strategy;

    public LogBaseImpl(String id, XDBStore store, XDBStoreUser user, Location logBaseLocation, String baseName, StorageStrategy strategy) {
        this.id = id;
        this.xdb = store;
        this.storeUser = user;
        this.logBaseLocation = logBaseLocation;
        this.baseName = baseName;
        this.strategy = strategy;
        this.strategy.prepare(this, this.xdb, this.storeUser, this.logBaseLocation, this.baseName);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Store getStore() {
        return this.xdb;
    }

    @Override
    public LogBaseEntry newEntry(String xmlFragment) {
        return new LogBaseEntryImpl(xmlFragment);
    }

    @Override
    public LogBaseEntry newEntry(Node node) {
        return new LogBaseEntryImpl(node);
    }

    @Override
    public LogBaseEntry newEntry(Map<String, String> pairs) {
        return new LogBaseEntryImpl(pairs);
    }

    @Override
    public void clear() {
        Session session = null;
        try {
            session = this.xdb.getSession(this.storeUser, false);
            session.begin();
            if (this.logBaseLocation.exists(session)) {
                this.logBaseLocation.delete(session);
                this.logBaseLocation.create(session, null, true);
                session.commit();
                this.strategy.prepare(this, this.xdb, this.storeUser, this.logBaseLocation, this.baseName);
            } else {
                session.commit();
            }
        }
        catch (DDSException ise) {
            LogCenter.exception(this, (Throwable)ise);
            if (session != null) {
                try {
                    session.rollback();
                }
                catch (RollbackFailedException rfe) {
                    LogCenter.exception(this, "Could not clear Structure, and Rollback failed :(", ise);
                }
            }
            LogCenter.exception(this, "Could not clear Structure", ise);
        }
    }

    @Override
    public void store(Session session, XBaseEntry entry) throws DDSException {
        this.strategy.fileFor(entry).store(session, entry);
    }

    @Override
    public LogBaseFile newXBaseFile(XDBStore store, XDBStoreUser user, String documentPath, String documentName) {
        return new LogBaseFileImpl(store, user, documentPath, documentName);
    }
}

