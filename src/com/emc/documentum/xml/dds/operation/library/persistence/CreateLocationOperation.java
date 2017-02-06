/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.CreateLocationOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.LocationOptions;
import com.emc.documentum.xml.dds.persistence.Store;

public class CreateLocationOperation
extends AbstractSingleStoreOperation<Object> {
    private final Location location;
    private final LocationOptions options;
    private final boolean createPath;

    public CreateLocationOperation(Location location, LocationOptions options, boolean createPath) throws OperationException {
        this.declareStore(location.getStore());
        this.location = location;
        this.options = options;
        this.createPath = createPath;
    }

    public Location getLocation() {
        return this.location;
    }

    public LocationOptions getOptions() {
        return this.options;
    }

    public boolean getCreatePath() {
        return this.createPath;
    }

    @Override
    public String getExecutableClassName() {
        return CreateLocationOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

