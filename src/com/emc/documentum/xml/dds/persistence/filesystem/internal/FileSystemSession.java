/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.operation.exception.BeginFailedException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;

public class FileSystemSession
implements Session {
    private final StoreUser user;
    private final Store store;

    public FileSystemSession(StoreUser user, Store store) {
        this.user = user;
        this.store = store;
    }

    @Override
    public void begin() throws BeginFailedException {
    }

    @Override
    public void commit() throws CommitFailedException {
    }

    @Override
    public Object getSession() {
        return null;
    }

    @Override
    public Store getStore() {
        return this.store;
    }

    @Override
    public StoreType getStoreType() {
        return this.store.getType();
    }

    @Override
    public StoreUser getUser() {
        return this.user;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public void rollback() throws RollbackFailedException {
    }
}

