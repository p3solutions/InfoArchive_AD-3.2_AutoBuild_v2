/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence;

import com.emc.documentum.xml.dds.operation.exception.BeginFailedException;
import com.emc.documentum.xml.dds.operation.exception.CommitFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;

public interface Session {
    public Object getSession();

    public StoreUser getUser();

    public Store getStore();

    public StoreType getStoreType();

    public void begin() throws BeginFailedException;

    public void commit() throws CommitFailedException;

    public void rollback() throws RollbackFailedException;

    public boolean isReadOnly();

    public boolean isOpen();
}

