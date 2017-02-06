/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.framework;

import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.user.User;

public interface OperationManager {
    public <T> T execute(User var1, Operation<T> var2) throws OperationFailedException;

    public SessionStoreUserStrategy getSessionStoreUserStrategy();
}

