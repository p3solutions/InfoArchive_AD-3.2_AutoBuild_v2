/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.RetrieveOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Store;

public class RetrieveOperation
extends AbstractSingleStoreOperation<Data<?>> {
    private final Container container;
    private final ContentDescriptor contentDescriptor;

    public RetrieveOperation(Container container, ContentDescriptor contentDescriptor) throws OperationException {
        this.declareStore(container.getStore());
        this.container = container;
        this.contentDescriptor = contentDescriptor;
    }

    public Container getContainer() {
        return this.container;
    }

    public ContentDescriptor getContentDescriptor() {
        return this.contentDescriptor;
    }

    @Override
    public String getExecutableClassName() {
        return RetrieveOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

