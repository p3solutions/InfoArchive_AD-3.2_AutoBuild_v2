/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.WindowsUNCStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemStore;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;
import com.emc.documentum.xml.dds.persistence.internal.AbstractLocation;

public class WindowsUNCStore
extends FileSystemStore {
    private final String hostName;
    private final String shareName;
    private final String translatedVirtualRoot;

    public WindowsUNCStore(WindowsUNCStoreConfiguration configuration) {
        super(configuration);
        this.hostName = configuration.getHostName();
        this.shareName = configuration.getShareName();
        StringBuilder vrPath = new StringBuilder();
        for (String pathComponent : AbstractLocation.parsePath(configuration.getVirtualRoot())) {
            vrPath.append(this.getType().getSeparator()).append(pathComponent);
        }
        this.translatedVirtualRoot = vrPath.toString();
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getShareName() {
        return this.shareName;
    }

    @Override
    public String getPrefix() {
        StringBuilder prefix = new StringBuilder("\\\\");
        prefix.append(this.hostName).append("\\").append(this.shareName);
        return this.translatedVirtualRoot == null ? prefix.toString() : prefix.toString() + this.translatedVirtualRoot;
    }

    @Override
    public FileSystemType getFileSystemType() {
        return FileSystemType.WINDOWS_UNC;
    }
}

