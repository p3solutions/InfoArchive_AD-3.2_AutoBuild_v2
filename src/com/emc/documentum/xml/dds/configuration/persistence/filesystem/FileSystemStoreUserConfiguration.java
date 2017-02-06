/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.filesystem;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public final class FileSystemStoreUserConfiguration
implements StoreUserConfiguration {
    private String id;
    private String encryptedPassword;

    public FileSystemStoreUserConfiguration(String id, String encryptedPassword) {
        this.id = id;
        this.encryptedPassword = encryptedPassword;
    }

    public String getId() {
        return this.id == null ? null : this.id.trim();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEncryptedPassword() {
        return this.encryptedPassword == null ? null : this.encryptedPassword.trim();
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    @Override
    public StoreType getType() {
        return StoreType.FILESYSTEM;
    }
}

