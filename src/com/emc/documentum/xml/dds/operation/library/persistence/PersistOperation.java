/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.PersistOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Container;
import com.emc.documentum.xml.dds.persistence.ContentDescriptor;
import com.emc.documentum.xml.dds.persistence.Data;
import com.emc.documentum.xml.dds.persistence.Store;

public class PersistOperation
extends AbstractSingleStoreOperation<Object> {
    private final Container container;
    private final ContentDescriptor contentDescriptor;
    private final Data<?> data;
    private final boolean replace;

    public PersistOperation(Container container, ContentDescriptor contentDescriptor, Data<?> data, boolean replace) throws OperationException {
        this.declareStore(container.getStore());
        this.container = container;
        this.contentDescriptor = contentDescriptor;
        this.data = data;
        this.replace = replace;
    }

    public Container getContainer() {
        return this.container;
    }

    public ContentDescriptor getContentDescriptor() {
        return this.contentDescriptor;
    }

    public Data<?> getData() {
        return this.data;
    }

    public boolean getReplace() {
        return this.replace;
    }

    @Override
    public String getExecutableClassName() {
        return PersistOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

