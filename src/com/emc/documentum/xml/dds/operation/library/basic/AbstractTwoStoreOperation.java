/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperation;
import com.emc.documentum.xml.dds.persistence.Store;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTwoStoreOperation<T>
extends AbstractOperation<T> {
    private String sourceStoreAlias;
    private String targetStoreAlias;

    @Override
    public List<String> getStoreAliases() {
        ArrayList<String> result = new ArrayList<String>();
        if (this.sourceStoreAlias != null) {
            result.add(this.sourceStoreAlias);
        }
        if (this.targetStoreAlias != null && !this.targetStoreAlias.equals(this.sourceStoreAlias)) {
            result.add(this.targetStoreAlias);
        }
        return result;
    }

    public String getSourceStoreAlias() {
        return this.sourceStoreAlias;
    }

    public String getTargetStoreAlias() {
        return this.targetStoreAlias;
    }

    protected void declareSourceStore(Store store) throws OperationException {
        if (store == null) {
            throw new OperationException("Tried to register null Store for Operation.");
        }
        if (this.sourceStoreAlias == null) {
            this.sourceStoreAlias = store.getAlias();
            return;
        }
        if (this.sourceStoreAlias.equals(store.getAlias())) {
            return;
        }
        throw new OperationException("Tried to declare multiple source Stores.");
    }

    protected void declareTargetStore(Store store) throws OperationException {
        if (store == null) {
            throw new OperationException("Tried to register null Store for Operation.");
        }
        if (this.targetStoreAlias == null) {
            this.targetStoreAlias = store.getAlias();
            return;
        }
        if (!this.targetStoreAlias.equals(store.getAlias())) {
            throw new OperationException("Tried to declare multiple target Stores.");
        }
    }
}

