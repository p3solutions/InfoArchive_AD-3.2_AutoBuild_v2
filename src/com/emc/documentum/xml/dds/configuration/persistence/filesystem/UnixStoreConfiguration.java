/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.filesystem;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreConfiguration;
import com.emc.documentum.xml.dds.persistence.filesystem.internal.FileSystemType;

public final class UnixStoreConfiguration
extends FileSystemStoreConfiguration {
    public UnixStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, String virtualRoot) {
        super(alias, defaultStoreUser, virtualRoot);
    }

    @Override
    public FileSystemType getFileSystemType() {
        return FileSystemType.UNIX;
    }
}

