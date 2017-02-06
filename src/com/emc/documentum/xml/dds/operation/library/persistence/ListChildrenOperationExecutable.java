/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.ListChildrenOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Session;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import java.util.Collection;
import java.util.Map;

public class ListChildrenOperationExecutable
extends PersistenceOperationExecutable<ListChildrenOperation, Collection<StoreChild>> {
    @Override
    public Collection<StoreChild> run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((ListChildrenOperation)this.getOperation()).getStoreAlias());
        return ((ListChildrenOperation)this.getOperation()).getLocation().listChildren(session, ((ListChildrenOperation)this.getOperation()).getIncludeLocations(), ((ListChildrenOperation)this.getOperation()).getIncludeContainers(), ((ListChildrenOperation)this.getOperation()).getRecurse());
    }
}

