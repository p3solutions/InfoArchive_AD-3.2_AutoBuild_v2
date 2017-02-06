/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.WindowsStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemStore;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;
import com.emc.documentum.xml.dds.persistence.internal.AbstractLocation;

public final class WindowsStore
extends FileSystemStore {
    private final String drive;
    private final String translatedVirtualRoot;

    public WindowsStore(WindowsStoreConfiguration configuration) {
        super(configuration);
        this.drive = configuration.getDrive();
        StringBuilder vrPath = new StringBuilder();
        for (String pathComponent : AbstractLocation.parsePath(configuration.getVirtualRoot())) {
            vrPath.append(this.getType().getSeparator()).append(pathComponent);
        }
        this.translatedVirtualRoot = vrPath.toString();
    }

    public String getDrive() {
        return this.drive;
    }

    @Override
    public String getPrefix() {
        return this.translatedVirtualRoot == null ? this.drive : this.drive + this.translatedVirtualRoot;
    }

    @Override
    public FileSystemType getFileSystemType() {
        return FileSystemType.WINDOWS;
    }
}

