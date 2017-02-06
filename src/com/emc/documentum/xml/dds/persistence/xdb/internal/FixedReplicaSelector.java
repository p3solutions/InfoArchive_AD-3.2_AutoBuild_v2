/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.xdb.internal;

import com.emc.documentum.xml.dds.persistence.xdb.ReplicaSelector;
import com.emc.documentum.xml.dds.persistence.xdb.internal.ReplicatedXDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;

public final class FixedReplicaSelector
implements ReplicaSelector {
    private final String alias;
    private transient XDBStore slave;

    public FixedReplicaSelector(String alias) {
        this.alias = alias;
    }

    @Override
    public void initialize(ReplicatedXDBStore store) {
        this.slave = store.getSlave(this.alias);
    }

    @Override
    public XDBStore pickStore() {
        return this.slave;
    }
}

