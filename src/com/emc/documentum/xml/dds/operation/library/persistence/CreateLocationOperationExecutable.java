/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public class CreateLocationOperationExecutable
extends PersistenceOperationExecutable<CreateLocationOperation, Object> {
    @Override
    public Object run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((CreateLocationOperation)this.getOperation()).getStoreAlias());
        ((CreateLocationOperation)this.getOperation()).getLocation().create(session, ((CreateLocationOperation)this.getOperation()).getOptions(), ((CreateLocationOperation)this.getOperation()).getCreatePath());
        return null;
    }
}

