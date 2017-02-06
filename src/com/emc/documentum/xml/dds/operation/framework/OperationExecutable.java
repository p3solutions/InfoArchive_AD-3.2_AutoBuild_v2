/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.framework;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public interface OperationExecutable<P extends Operation<T>, T> {
    public Application getApplication();

    public void setApplication(Application var1);

    public P getOperation();

    public void setOperation(P var1);

    public void beforeRun();

    public T run(Map<String, Session> var1) throws DDSException;

    public void afterRun();

    public void rollback() throws RollbackNotAvailableException, RollbackFailedException;

    public boolean canRollback();
}

