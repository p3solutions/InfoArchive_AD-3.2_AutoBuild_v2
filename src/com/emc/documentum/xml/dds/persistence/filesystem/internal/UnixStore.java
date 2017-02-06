/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.UnixStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemStore;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;

public class UnixStore
extends FileSystemStore {
    public UnixStore(UnixStoreConfiguration configuration) {
        super(configuration);
    }

    @Override
    public String getPrefix() {
        return this.getVirtualRoot() == null ? "" : this.getVirtualRoot();
    }

    @Override
    public FileSystemType getFileSystemType() {
        return FileSystemType.UNIX;
    }
}

