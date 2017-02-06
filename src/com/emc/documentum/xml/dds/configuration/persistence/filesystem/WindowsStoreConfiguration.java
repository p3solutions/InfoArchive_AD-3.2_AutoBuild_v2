/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.filesystem;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;

public final class WindowsStoreConfiguration
extends FileSystemStoreConfiguration {
    private String drive;

    public WindowsStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, String virtualRoot, String drive) {
        super(alias, defaultStoreUser, virtualRoot);
        this.drive = drive;
    }

    public String getDrive() {
        return this.drive == null ? null : this.drive.trim();
    }

    public void setDrive(String drive) {
        this.drive = drive;
    }

    @Override
    public FileSystemType getFileSystemType() {
        return FileSystemType.WINDOWS;
    }
}

