/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.xbase.strategy;

import com.emc.documentum.xml.dds.persistence.Location;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStore;
import com.emc.documentum.xml.dds.persistence.xdb.internal.XDBStoreUser;
import com.emc.documentum.xml.dds.xbase.StorageStrategy;
import com.emc.documentum.xml.dds.xbase.XBase;
import com.emc.documentum.xml.dds.xbase.XBaseEntry;
import com.emc.documentum.xml.dds.xbase.XBaseFile;

public final class SingleFileStorageStrategy
implements StorageStrategy {
    private XBaseFile file;
    private XDBStore xdb;
    private XDBStoreUser storeUser;

    @Override
    public void prepare(XBase xBase, XDBStore store, XDBStoreUser user, Location location, String baseName) {
        this.xdb = store;
        this.storeUser = user;
        this.file = xBase.newXBaseFile(this.xdb, this.storeUser, location.getPath(false), baseName + ".db");
        this.file.create(false);
    }

    @Override
    public XBaseFile fileFor(XBaseEntry entry) {
        return this.file;
    }
}

