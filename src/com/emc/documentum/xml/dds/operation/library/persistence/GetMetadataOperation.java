/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.GetMetadataOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.Metadata;
import com.emc.documentum.xml.dds.persistence.MetadataScheme;
import com.emc.documentum.xml.dds.persistence.Store;

public class GetMetadataOperation
extends AbstractSingleStoreOperation<Metadata> {
    private final Container container;
    private final MetadataScheme scheme;

    public GetMetadataOperation(Container container, MetadataScheme scheme) throws OperationException {
        this.declareStore(container.getStore());
        this.container = container;
        this.scheme = scheme;
    }

    public Container getContainer() {
        return this.container;
    }

    public MetadataScheme getScheme() {
        return this.scheme;
    }

    @Override
    public String getExecutableClassName() {
        return GetMetadataOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

