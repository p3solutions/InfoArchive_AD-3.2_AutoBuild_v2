/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMultiStoreOperation<T>
extends AbstractOperation<T> {
    private final List<String> storeAliases = new ArrayList<String>();

    @Override
    public List<String> getStoreAliases() {
        return this.storeAliases;
    }

    protected void declareStore(Store store) throws OperationException {
        if (store == null) {
            throw new OperationException("Tried to register null Store for Operation.");
        }
        if (!this.storeAliases.contains(store.getAlias())) {
            this.storeAliases.add(store.getAlias());
        }
    }
}

