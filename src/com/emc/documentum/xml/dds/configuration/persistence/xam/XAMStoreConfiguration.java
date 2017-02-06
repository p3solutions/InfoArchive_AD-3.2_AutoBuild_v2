/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.xam;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public class XAMStoreConfiguration
extends StoreConfiguration {
    private String connectionString;
    private String crStoreAlias;
    private String crName;

    public XAMStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, String connectionString, String crStoreAlias, String crName) {
        super(alias, defaultStoreUser);
        this.connectionString = connectionString;
        this.crStoreAlias = crStoreAlias;
        this.crName = crName;
    }

    public String getConnectionString() {
        return this.connectionString == null ? null : this.connectionString.trim();
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public String getContentRegistryStoreAlias() {
        return this.crStoreAlias == null ? null : this.crStoreAlias.trim();
    }

    public void setContentRegistryStoreAlias(String contentRegistryStoreAlias) {
        this.crStoreAlias = contentRegistryStoreAlias;
    }

    public String getContentRegistryName() {
        return this.crName == null ? null : this.crName.trim();
    }

    public void setContentRegistryName(String contentRegistryName) {
        this.crName = contentRegistryName;
    }

    @Override
    public StoreType getType() {
        return StoreType.XAM;
    }
}

