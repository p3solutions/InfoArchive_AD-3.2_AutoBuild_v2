/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.SetMetadataOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.Store;

public class SetMetadataOperation
extends AbstractSingleStoreOperation<Object> {
    private final Container container;
    private final Metadata metadata;

    public SetMetadataOperation(Container container, Metadata metadata) throws OperationException {
        this.declareStore(container.getStore());
        this.container = container;
        this.metadata = metadata;
    }

    public Container getContainer() {
        return this.container;
    }

    public Metadata getMetadata() {
        return this.metadata;
    }

    @Override
    public String getExecutableClassName() {
        return SetMetadataOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

