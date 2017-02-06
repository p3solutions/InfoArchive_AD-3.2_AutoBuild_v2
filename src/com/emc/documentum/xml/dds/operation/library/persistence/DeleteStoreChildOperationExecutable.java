/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.DeleteStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import java.util.Map;

public class DeleteStoreChildOperationExecutable
extends PersistenceOperationExecutable<DeleteStoreChildOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((DeleteStoreChildOperation)this.getOperation()).getStoreAlias());
        ((DeleteStoreChildOperation)this.getOperation()).getStoreChild().delete(session);
        return null;
    }
}

