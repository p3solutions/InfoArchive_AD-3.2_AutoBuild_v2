/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.GetMetadataOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public class GetMetadataOperationExecutable
extends PersistenceOperationExecutable<GetMetadataOperation, Metadata> {
    @Override
    public Metadata run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((GetMetadataOperation)this.getOperation()).getStoreAlias());
        return ((GetMetadataOperation)this.getOperation()).getContainer().getMetadata(session, ((GetMetadataOperation)this.getOperation()).getScheme());
    }
}

