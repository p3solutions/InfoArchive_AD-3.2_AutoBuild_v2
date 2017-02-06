/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xquery;

import com.emc.documentum.xml.dds.application.Application;
import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.exception.RollbackFailedException;
import com.emc.documentum.xml.dds.operation.exception.RollbackNotAvailableException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xquery.ExecuteXQueryOperation;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;
import com.emc.documentum.xml.dds.xquery.XQueryExecutor;
import com.emc.documentum.xml.dds.xquery.XQueryResultHandler;
import java.util.Map;

public class ExecuteXQueryOperationExecutable
extends AbstractOperationExecutable<ExecuteXQueryOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        XQueryExecutor executor = this.getApplication().getStore(((ExecuteXQueryOperation)this.getOperation()).getStoreAlias()).getXQueryExecutor();
        Session session = sessionMap.get(((ExecuteXQueryOperation)this.getOperation()).getStoreAlias());
        if (((ExecuteXQueryOperation)this.getOperation()).getXMLNode() == null) {
            return executor.execute(session, ((ExecuteXQueryOperation)this.getOperation()).getXQuery(), ((ExecuteXQueryOperation)this.getOperation()).getVariables(), ((ExecuteXQueryOperation)this.getOperation()).getXQueryResultHandler());
        }
        return executor.executeOn(session, ((ExecuteXQueryOperation)this.getOperation()).getXQuery(), ((ExecuteXQueryOperation)this.getOperation()).getXMLNode(), ((ExecuteXQueryOperation)this.getOperation()).getVariables(), ((ExecuteXQueryOperation)this.getOperation()).getXQueryResultHandler());
    }

    @Override
    public boolean canRollback() {
        return false;
    }

    @Override
    public void rollback() throws RollbackNotAvailableException, RollbackFailedException {
    }
}

