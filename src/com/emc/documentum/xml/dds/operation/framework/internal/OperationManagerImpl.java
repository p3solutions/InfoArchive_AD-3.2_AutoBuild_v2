/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.framework.internal;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.operation.exception.OperationFailedException;
import com.emc.documentum.xml.dds.operation.framework.Executor;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.framework.OperationManager;
import com.emc.documentum.xml.dds.persistence.SessionStoreUserStrategy;
import com.emc.documentum.xml.dds.user.User;

public class OperationManagerImpl
implements OperationManager {
    private final Executor executor;
    private final SessionStoreUserStrategy sessionStoreUserStrategy;

    public OperationManagerImpl(Application application, SessionStoreUserStrategy strategy) {
        this.executor = new Executor(application, strategy);
        this.sessionStoreUserStrategy = strategy;
    }

    @Override
    public SessionStoreUserStrategy getSessionStoreUserStrategy() {
        return this.sessionStoreUserStrategy;
    }

    @Override
    public <T> T execute(User user, Operation<T> operation) throws OperationFailedException {
        return (T) this.executor.execute(user, operation);
    }
}

