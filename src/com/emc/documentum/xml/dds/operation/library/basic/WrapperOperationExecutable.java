/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.framework.Executor;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.framework.OperationExecutable;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.basic.WrapperOperation;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public class WrapperOperationExecutable
extends AbstractOperationExecutable<WrapperOperation, Object> {
    private OperationExecutable<? extends Operation<?>, ?> wrappedExecutable;

    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        this.wrappedExecutable = Executor.createExecutable(this.getApplication(), ((WrapperOperation)this.getOperation()).getWrappedOperation());
        return this.wrappedExecutable.run(sessionMap);
    }

    @Override
    public boolean canRollback() {
        return this.wrappedExecutable.canRollback();
    }

    @Override
    public void rollback() throws RollbackNotAvailableException, RollbackFailedException {
        this.wrappedExecutable.rollback();
    }
}

