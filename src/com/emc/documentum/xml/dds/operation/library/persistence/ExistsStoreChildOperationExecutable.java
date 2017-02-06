/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import java.util.Map;

public class ExistsStoreChildOperationExecutable
extends PersistenceOperationExecutable<ExistsStoreChildOperation, Boolean> {
    @Override
    public Boolean run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((ExistsStoreChildOperation)this.getOperation()).getStoreAlias());
        return ((ExistsStoreChildOperation)this.getOperation()).getStoreChild().exists(session);
    }
}

