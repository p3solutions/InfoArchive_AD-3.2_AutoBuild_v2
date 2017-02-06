/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.filesystem;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;

public final class WindowsUNCStoreConfiguration
extends FileSystemStoreConfiguration {
    private String hostName;
    private String shareName;

    public WindowsUNCStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, String virtualRoot, String hostName, String shareName) {
        super(alias, defaultStoreUser, virtualRoot);
        this.hostName = hostName;
        this.shareName = shareName;
    }

    public String getHostName() {
        return this.hostName == null ? null : this.hostName.trim();
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getShareName() {
        return this.shareName == null ? null : this.shareName.trim();
    }

    public void setShareName(String shareName) {
        this.shareName = shareName;
    }

    @Override
    public FileSystemType getFileSystemType() {
        return FileSystemType.WINDOWS_UNC;
    }
}

