/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.filesystem;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;

public abstract class FileSystemStoreConfiguration
extends StoreConfiguration {
    private String virtualRoot;

    public FileSystemStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, String virtualRoot) {
        super(alias, defaultStoreUser);
        this.virtualRoot = virtualRoot;
    }

    public final String getVirtualRoot() {
        return this.virtualRoot == null ? null : this.virtualRoot.trim();
    }

    public final void setVirtualRoot(String virtualRoot) {
        this.virtualRoot = virtualRoot;
    }

    @Override
    public final StoreType getType() {
        return StoreType.FILESYSTEM;
    }

    public abstract FileSystemType getFileSystemType();
}

