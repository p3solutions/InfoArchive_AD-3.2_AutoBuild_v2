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

public final class RoundRobinStorageStrategy
implements StorageStrategy {
    private final int nrFiles;
    private int currentFile;
    private XDBStore xdb;
    private XDBStoreUser storeUser;
    private XBaseFile[] files;
    private final Object mutex = new Object();

    public RoundRobinStorageStrategy(int nrFiles) {
        this.nrFiles = nrFiles;
    }

    @Override
    public void prepare(XBase xBase, XDBStore store, XDBStoreUser user, Location location, String baseName) {
        this.xdb = store;
        this.storeUser = user;
        this.files = new XBaseFile[this.nrFiles];
        this.currentFile = 0;
        for (int counter = 0; counter < this.nrFiles; ++counter) {
            this.files[counter] = xBase.newXBaseFile(this.xdb, this.storeUser, location.getPath(false), baseName + "_" + counter + ".db");
            this.files[counter].create(false);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XBaseFile fileFor(XBaseEntry entry) {
        Object object = this.mutex;
        synchronized (object) {
            if (this.currentFile == this.nrFiles) {
                this.currentFile = 0;
            }
            return this.files[this.currentFile++];
        }
    }
}

