/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ListChildrenOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import java.util.Collection;

public class ListChildrenOperation
extends AbstractSingleStoreOperation<Collection<StoreChild>> {
    private final Location location;
    private final boolean includeLocations;
    private final boolean includeContainers;
    private final boolean recurse;

    public ListChildrenOperation(Location location, boolean includeLocations, boolean includeContainers, boolean recurse) throws OperationException {
        this.declareStore(location.getStore());
        this.location = location;
        this.includeLocations = includeLocations;
        this.includeContainers = includeContainers;
        this.recurse = recurse;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean getIncludeLocations() {
        return this.includeLocations;
    }

    public boolean getIncludeContainers() {
        return this.includeContainers;
    }

    public boolean getRecurse() {
        return this.recurse;
    }

    @Override
    public String getExecutableClassName() {
        return ListChildrenOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

