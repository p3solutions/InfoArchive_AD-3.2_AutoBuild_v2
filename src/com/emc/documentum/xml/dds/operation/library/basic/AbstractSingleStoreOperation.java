/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSingleStoreOperation<T>
extends AbstractOperation<T> {
    private String storeAlias;

    @Override
    public List<String> getStoreAliases() {
        ArrayList<String> result = new ArrayList<String>();
        if (this.storeAlias != null) {
            result.add(this.storeAlias);
        }
        return result;
    }

    public String getStoreAlias() {
        return this.storeAlias;
    }

    protected void declareStore(Store store) throws OperationException {
        if (store == null) {
            throw new OperationException("Tried to register null Store for Operation.");
        }
        if (this.storeAlias == null) {
            this.storeAlias = store.getAlias();
            return;
        }
        if (!this.storeAlias.equals(store.getAlias())) {
            throw new OperationException("Only one Store supported by this Operation.");
        }
    }
}

