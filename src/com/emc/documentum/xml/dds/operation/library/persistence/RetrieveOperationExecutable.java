/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public class RetrieveOperationExecutable
extends PersistenceOperationExecutable<RetrieveOperation, Data<?>> {
    @Override
    public Data<?> run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((RetrieveOperation)this.getOperation()).getStoreAlias());
        return ((RetrieveOperation)this.getOperation()).getContainer().retrieve(session, ((RetrieveOperation)this.getOperation()).getContentDescriptor());
    }
}

