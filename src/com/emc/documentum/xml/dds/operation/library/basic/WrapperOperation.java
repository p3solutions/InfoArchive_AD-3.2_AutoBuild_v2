/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.basic;

import com.emc.documentum.xml.dds.operation.framework.Operation;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractOperation;
import com.emc.documentum.xml.dds.operation.library.basic.WrapperOperationExecutable;
import java.util.List;

public class WrapperOperation
extends AbstractOperation<Object> {
    private final Operation<?> wrappedOperation;

    public WrapperOperation(Operation<?> wrappedOperation) {
        this.wrappedOperation = wrappedOperation;
    }

    public Operation<?> getWrappedOperation() {
        return this.wrappedOperation;
    }

    @Override
    public String getExecutableClassName() {
        return WrapperOperationExecutable.class.getName();
    }

    @Override
    public List<String> getStoreAliases() {
        return this.wrappedOperation.getStoreAliases();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return this.wrappedOperation.isReadOnly(storeAlias);
    }
}

