/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.MultiNodeOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.XMLPersistenceOperationExecutable;

public abstract class MultiNodeOperationExecutable<P extends MultiNodeOperation<T>, T>
extends XMLPersistenceOperationExecutable<P, T> {
    @Override
    public boolean canRollback() {
        return false;
    }

    @Override
    public void rollback() throws RollbackNotAvailableException, RollbackFailedException {
    }
}

