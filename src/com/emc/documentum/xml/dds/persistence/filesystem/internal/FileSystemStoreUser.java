/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.persistence.filesystem.internal;

import com.emc.documentum.xml.dds.configuration.persistence.filesystem.FileSystemStoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;
import com.emc.documentum.xml.dds.persistence.StoreUser;

public class FileSystemStoreUser
implements StoreUser {
    private final String id;

    public FileSystemStoreUser(FileSystemStoreUserConfiguration configuration) {
        this.id = configuration.getId();
    }

    public String getId() {
        return this.id;
    }

    @Override
    public StoreType getStoreType() {
        return StoreType.FILESYSTEM;
    }
}

