/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.ExistsStoreChildOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;

public class ExistsStoreChildOperation
extends AbstractSingleStoreOperation<Boolean> {
    private final StoreChild storeChild;

    public ExistsStoreChildOperation(StoreChild storeChild) throws OperationException {
        this.declareStore(storeChild.getStore());
        this.storeChild = storeChild;
    }

    public StoreChild getStoreChild() {
        return this.storeChild;
    }

    @Override
    public String getExecutableClassName() {
        return ExistsStoreChildOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

