/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.xdb;

import com.emc.documentum.xml.dds.persistence.xdb.internal.ReplicatedXDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;

public interface ReplicaSelector {
    public void initialize(ReplicatedXDBStore var1);

    public XDBStore pickStore();
}

