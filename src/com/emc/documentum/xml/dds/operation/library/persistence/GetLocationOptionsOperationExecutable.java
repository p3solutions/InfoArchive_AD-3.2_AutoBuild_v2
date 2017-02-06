/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.exception.DDSException;
import com.emc.documentum.xml.dds.operation.library.persistence.GetLocationOptionsOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistenceOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Session;
import java.util.Map;

public class GetLocationOptionsOperationExecutable
extends PersistenceOperationExecutable<GetLocationOptionsOperation, LocationOptions> {
    @Override
    public LocationOptions run(Map<String, Session> sessionMap) throws DDSException {
        Session session = sessionMap.get(((GetLocationOptionsOperation)this.getOperation()).getStoreAlias());
        return ((GetLocationOptionsOperation)this.getOperation()).getLocation().getOptions(session);
    }
}

