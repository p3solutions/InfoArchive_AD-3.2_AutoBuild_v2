/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.esu.api.EsuException
 *  com.emc.esu.api.rest.EsuRestApi
 */
package com.emc.documentum.xml.dds.persistence.esu.internal;

import com.emc.documentum.xml.dds.operation.exception.BeginFailedException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUStore;
import com.emc.documentum.xml.dds.persistence.esu.internal.ESUStoreUser;
import com.emc.esu.api.EsuException;
import com.emc.esu.api.rest.EsuRestApi;

public class ESUSession
implements Session {
    private final ESUStoreUser storeUser;
    private final ESUStore store;
    private EsuRestApi esu;

    public ESUSession(ESUStoreUser storeUser, ESUStore store) {
        this.storeUser = storeUser;
        this.store = store;
    }

    @Override
    public void begin() throws BeginFailedException {
        try {
            this.esu = new EsuRestApi(this.store.getHost(), this.store.getPort().intValue(), this.storeUser.getUid(), this.storeUser.getSharedSecret());
        }
        catch (EsuException eee) {
            throw new BeginFailedException("Could not connect to the ESU Store.", (Throwable)eee);
        }
    }

    public EsuRestApi getESU() {
        return this.esu;
    }

    @Override
    public void commit() throws CommitFailedException {
    }

    @Override
    public void rollback() throws RollbackFailedException {
    }

    @Override
    public boolean isOpen() {
        return this.esu != null;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Object getSession() {
        return null;
    }

    @Override
    public StoreUser getUser() {
        return this.storeUser;
    }

    @Override
    public Store getStore() {
        return this.store;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.ESU;
    }
}

