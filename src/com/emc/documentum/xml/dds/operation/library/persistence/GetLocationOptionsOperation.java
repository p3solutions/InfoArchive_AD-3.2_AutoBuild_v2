/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.GetLocationOptionsOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Store;

public class GetLocationOptionsOperation
extends AbstractSingleStoreOperation<LocationOptions> {
    private final Location location;

    public GetLocationOptionsOperation(Location location) throws OperationException {
        this.declareStore(location.getStore());
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    @Override
    public String getExecutableClassName() {
        return GetLocationOptionsOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

