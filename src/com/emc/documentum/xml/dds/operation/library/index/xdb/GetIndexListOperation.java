/*
 * Decompiled with CFR 0_118.
 * 
 * Could not load the following classes:
 *  com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXDBIndex
 */
package com.emc.documentum.xml.dds.operation.library.index.xdb;

import com.emc.documentum.xml.dds.gwt.client.rpc.persistence.SerializableXDBIndex;
import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.index.xdb.GetIndexListOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;
import java.util.List;

public class GetIndexListOperation
extends AbstractSingleStoreOperation<List<SerializableXDBIndex>> {
    private final StoreChild storeChild;

    public GetIndexListOperation(StoreChild storeChild) throws OperationException {
        this.declareStore(storeChild.getStore());
        this.storeChild = storeChild;
    }

    public StoreChild getStoreChild() {
        return this.storeChild;
    }

    @Override
    public String getExecutableClassName() {
        return GetIndexListOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return true;
    }
}

