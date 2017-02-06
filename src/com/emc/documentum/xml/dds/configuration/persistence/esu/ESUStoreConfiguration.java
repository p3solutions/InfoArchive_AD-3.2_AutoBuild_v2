/*
 * Decompiled with CFR 0_118.
 */
package com.emc.documentum.xml.dds.configuration.persistence.esu;

import com.emc.documentum.xml.dds.configuration.persistence.StoreConfiguration;
import com.emc.documentum.xml.dds.configuration.persistence.StoreUserConfiguration;
import com.emc.documentum.xml.dds.persistence.StoreType;

public class ESUStoreConfiguration
extends StoreConfiguration {
    private String host;
    private int port;
    private String crStoreAlias;
    private String crName;

    public ESUStoreConfiguration(String alias, StoreUserConfiguration defaultStoreUser, String host, int port, String crStoreAlias, String crName) {
        super(alias, defaultStoreUser);
        this.host = host;
        this.port = port;
        this.crStoreAlias = crStoreAlias;
        this.crName = crName;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
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
        return StoreType.ESU;
    }
}

