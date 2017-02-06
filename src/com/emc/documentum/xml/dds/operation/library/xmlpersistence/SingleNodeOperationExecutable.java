/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperation;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.XMLPersistenceOperationExecutable;

public abstract class SingleNodeOperationExecutable<P extends SingleNodeOperation<T>, T>
extends XMLPersistenceOperationExecutable<P, T> {
    @Override
    public boolean canRollback() {
        return false;
    }

    @Override
    public void rollback() throws RollbackNotAvailableException, RollbackFailedException {
    }
}

