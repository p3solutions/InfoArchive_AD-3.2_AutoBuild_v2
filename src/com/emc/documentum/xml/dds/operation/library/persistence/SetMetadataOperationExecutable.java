/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.persistence.SetMetadataOperation;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public class SetMetadataOperationExecutable
extends PersistenceOperationExecutable<SetMetadataOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((SetMetadataOperation)this.getOperation()).getStoreAlias());
        ((SetMetadataOperation)this.getOperation()).getContainer().setMetadata(session, ((SetMetadataOperation)this.getOperation()).getMetadata());
        return null;
    }
}

