/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.internal;

import com.emc.documentum.xml.dds.persistence.StoreChild;
import com.emc.documentum.xml.dds.structure.Structure;

public abstract class AbstractStoreChild
implements StoreChild {
    private Structure context;

    @Override
    public Structure getContext() {
        return this.context;
    }

    @Override
    public void setContext(Structure context) {
        this.context = context;
    }
}

