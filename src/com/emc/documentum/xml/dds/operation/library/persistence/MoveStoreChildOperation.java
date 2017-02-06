/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.persistence;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractTwoStoreOperation;
import com.emc.documentum.xml.dds.operation.library.persistence.MoveStoreChildOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.persistence.StoreChild;

public class MoveStoreChildOperation
extends AbstractTwoStoreOperation<Object> {
    private final StoreChild source;
    private final StoreChild target;
    private final boolean replace;

    public MoveStoreChildOperation(StoreChild source, StoreChild target, boolean replace) throws OperationException {
        this.declareSourceStore(source.getStore());
        this.declareTargetStore(target.getStore());
        this.source = source;
        this.target = target;
        this.replace = replace;
    }

    public StoreChild getSource() {
        return this.source;
    }

    public StoreChild getTarget() {
        return this.target;
    }

    public boolean getReplace() {
        return this.replace;
    }

    @Override
    public String getExecutableClassName() {
        return MoveStoreChildOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

