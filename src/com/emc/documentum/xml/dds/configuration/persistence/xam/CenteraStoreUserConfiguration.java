/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.xam;

import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public final class CenteraStoreUserConfiguration
implements StoreUserConfiguration {
    private String id;
    private String encryptedPassword;
    private String peaFileName;

    public CenteraStoreUserConfiguration(String id, String encryptedPassword) {
        this.id = id;
        this.encryptedPassword = encryptedPassword;
    }

    public CenteraStoreUserConfiguration(String peaFileName) {
        this.peaFileName = peaFileName;
    }

    public String getId() {
        return this.id == null ? null : this.id.trim();
    }

    public void setId(String id) {
        this.peaFileName = null;
        this.id = id;
    }

    public String getEncryptedPassword() {
        return this.encryptedPassword == null ? null : this.encryptedPassword.trim();
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.peaFileName = null;
        this.encryptedPassword = encryptedPassword;
    }

    public String getPeaFileName() {
        return this.peaFileName == null ? null : this.peaFileName.trim();
    }

    public void setPeaFileName(String peaFileName) {
        this.id = null;
        this.encryptedPassword = null;
        this.peaFileName = peaFileName;
    }

    @Override
    public StoreType getType() {
        return StoreType.XAM;
    }
}

