/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xmlpersistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.GetNodeOperationExecutable;
import com.emc.documentum.xml.dds.operation.library.xmlpersistence.SingleNodeOperation;
import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.XMLNode;

public class GetNodeOperation
extends SingleNodeOperation<XMLNode> {
    private Location location;

    public GetNodeOperation(XMLNode xmlNode) throws OperationException {
        super(xmlNode);
    }

    public GetNodeOperation(Location location) throws OperationException {
        this.declareStore(location.getStore());
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String getExecutableClassName() {
        return GetNodeOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

