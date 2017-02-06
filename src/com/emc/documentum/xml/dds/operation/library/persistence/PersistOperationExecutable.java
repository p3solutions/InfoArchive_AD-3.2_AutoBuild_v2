/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public class PersistOperationExecutable
extends PersistenceOperationExecutable<PersistOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((PersistOperation)this.getOperation()).getStoreAlias());
        ((PersistOperation)this.getOperation()).getContainer().persist(session, ((PersistOperation)this.getOperation()).getContentDescriptor(), ((PersistOperation)this.getOperation()).getData(), ((PersistOperation)this.getOperation()).getReplace());
        return null;
    }
}

