/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.operation.library.xbase;

import com.emc.documentum.xml.dds.operation.exception.OperationException;
import com.emc.documentum.xml.dds.operation.library.basic.AbstractSingleStoreOperation;
import com.emc.documentum.xml.dds.operation.library.xbase.StoreEntryOperationExecutable;
import com.emc.documentum.xml.dds.persistence.Store;
import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;

public class StoreEntryOperation
extends AbstractSingleStoreOperation<Object> {
    private final XBase xBase;
    private final XBaseEntry entry;

    public StoreEntryOperation(XBase xBase, XBaseEntry entry) throws OperationException {
        this.declareStore(xBase.getStore());
        this.xBase = xBase;
        this.entry = entry;
    }

    public XBase getXBase() {
        return this.xBase;
    }

    public XBaseEntry getEntry() {
        return this.entry;
    }

    @Override
    public String getExecutableClassName() {
        return StoreEntryOperationExecutable.class.getName();
    }

    @Override
    public boolean isReadOnly(String storeAlias) {
        return false;
    }
}

