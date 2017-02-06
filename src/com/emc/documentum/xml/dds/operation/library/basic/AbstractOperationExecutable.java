/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.framework.OperationExecutable;

public abstract class AbstractOperationExecutable<P extends Operation<T>, T>
implements OperationExecutable<P, T> {
    private Application application;
    private P operation;

    @Override
    public Application getApplication() {
        return this.application;
    }

    @Override
    public void setApplication(Application application) {
        this.application = application;
    }

    @Override
    public P getOperation() {
        return this.operation;
    }

    @Override
    public void setOperation(P operation) {
        this.operation = operation;
    }

    @Override
    public void beforeRun() {
    }

    @Override
    public void afterRun() {
    }
}

