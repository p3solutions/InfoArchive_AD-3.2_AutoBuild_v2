/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xbase;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xbase.StoreEntryOperation;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import java.util.Map;

public class StoreEntryOperationExecutable
extends PersistenceOperationExecutable<StoreEntryOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((StoreEntryOperation)this.getOperation()).getStoreAlias());
        ((StoreEntryOperation)this.getOperation()).getXBase().store(session, ((StoreEntryOperation)this.getOperation()).getEntry());
        return null;
    }
}

